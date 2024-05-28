package hu.bme.aut.api.frontend;

import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.OrderDTO;
import hu.bme.aut.api.dto.ProductDTO;
import io.qt.widgets.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.sql.Date;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MyWindow extends QWidget {
    public MyWindow() {
        super();
        QVBoxLayout layout = new QVBoxLayout(this);

        userComboBox = new QComboBox(this);
        layout.addWidget(userComboBox);
        userComboBox.addItem("John Doe");
        userComboBox.addItem("Alice Smith");

        QTabWidget tabWidget = new QTabWidget(this);
        layout.addWidget(tabWidget);

        QWidget searchTab = new QWidget(tabWidget);
        searchTab.setObjectName("searchTab");
        tabWidget.addTab(searchTab, "search");
        QVBoxLayout searchTabLayout = new QVBoxLayout(searchTab);

        QWidget cartTab = new QWidget(tabWidget);
        cartTab.setObjectName("cartTab");
        tabWidget.addTab(cartTab, "cart");
        QVBoxLayout cartTabLayout = new QVBoxLayout(cartTab);

        QWidget ordersTab = new QWidget(tabWidget);
        ordersTab.setObjectName("ordersTab");
        tabWidget.addTab(ordersTab, "orders");
        QVBoxLayout ordersTabLayout = new QVBoxLayout(ordersTab);

        QFormLayout searchForm = new QFormLayout();
        searchTabLayout.addLayout(searchForm);

        QLineEdit nameLineEdit = new QLineEdit(searchTab);
        searchForm.addRow("name:", nameLineEdit);
        QComboBox categoryComboBox = new QComboBox(searchTab);
        categoryComboBox.addItem("<all>");
        categoryComboBox.addItem("electronics");
        categoryComboBox.addItem("books");
        searchForm.addRow("category:", categoryComboBox);
        QPushButton searchButton = new QPushButton("search", searchTab);
        searchForm.addRow(searchButton);

        searchResultsTable = new QTableWidget(searchTab);
        searchTabLayout.addWidget(searchResultsTable);
        searchResultsTable.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers);
        searchResultsTable.setColumnCount(4);
        searchResultsTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In cart"));

        totalLabel = new QLabel("Total: " + priceToString(0.0));
        cartTabLayout.addWidget(totalLabel);

        cartTable = new QTableWidget(cartTab);
        cartTabLayout.addWidget(cartTable);
        cartTable.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers);
        cartTable.setColumnCount(4);
        cartTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In cart"));

        buyButton = new QPushButton("buy", cartTab);
        buyButton.setEnabled(false);
        cartTabLayout.addWidget(buyButton);

        ordersTable = new QTableWidget(ordersTab);
        ordersTabLayout.addWidget(ordersTable);
        ordersTable.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers);
        ordersTable.setColumnCount(4);
        ordersTable.setHorizontalHeaderLabels(List.of("Date", "Status", "Price", "Contents"));

        refreshCurrentTab = () -> {
            QWidget currentTab = tabWidget.currentWidget();
            if (currentTab == null) {
                throw new IllegalStateException("There is no active tab.");
            }
            String currentTabName = currentTab.objectName();
            switch (currentTabName) {
                case "searchTab" -> refreshSearchResultsTable();
                case "cartTab" -> setCartTabContents();
                case "ordersTab" -> setOrdersTabContents();
                default ->
                    //TODO this is for debug purposes
                        throw new IllegalStateException("Could not match any tab.");
            }
        };
        userComboBox.currentTextChanged.connect((String s) -> refreshCurrentTab.run());
        tabWidget.currentChanged.connect(refreshCurrentTab::run);
        buyButton.clicked.connect(this::sendOrder);
        searchButton.clicked.connect(() -> {
            String name = nameLineEdit.text();
            String category = categoryComboBox.currentText();
            searchProducts(name, category);
        });

        clearRemoteBaskets();
    }

    private final Runnable refreshCurrentTab;
    private final QTableWidget searchResultsTable;
    List<ProductDTO> searchResults = new ArrayList<>();
    private void setSearchResultsTableContents(List<ProductDTO> products) {
        BasketDTO cart = getLocalBasket();
        searchResultsTable.setRowCount(products.size());
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            searchResultsTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            searchResultsTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            searchResultsTable.setItem(i, 2, new QTableWidgetItem(priceToString(product.getPrice())));
            QSpinBox spinBox = new QSpinBox(searchResultsTable);
            spinBox.setValue(cart.getProducts().stream()
                    .filter(x -> x.getProductId().equals(product.getProductId()))
                    .map(ProductDTO::getQuantity)
                    .findAny().orElse(0));
            spinBox.valueChanged.connect((Integer value) -> {
                changeLocalBasket(product, value);
            });
            searchResultsTable.setCellWidget(i, 3, spinBox);
        }
    }
    private void refreshSearchResultsTable() {
        setSearchResultsTableContents(searchResults);
    }

    private final QTableWidget cartTable;
    private final QLabel totalLabel;
    private void setCartTabContents() {
        pushLocalBasket();
        BasketDTO cart = getRemoteBasket();
        if (cart == null) {
            buyButton.setEnabled(false);
            totalLabel.setText("Total: " + priceToString(0.0));
            cartTable.setRowCount(0);
            return;
        }
        totalLabel.setText("Total: " + priceToString(cart.getSubtotalAmount()));

        List<ProductDTO> products = cart.getProducts();
        cartTable.setRowCount(products.size());
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            cartTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            cartTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            cartTable.setItem(i, 2, new QTableWidgetItem(priceToString(product.getPrice())));
            var spinBox = new QSpinBox(cartTable) {public int oldValue = 0;};
//            spinBox.setValue(cart.getProducts().stream()
//                    .filter(x -> {
//                        if (x.getProductId() == null) {
//                            log.info("blocked null id product");
//                            return false;
//                        }
//                        return true;
//                    })
//                    .filter(x -> x.getProductId().equals(product.getProductId()))
//                    .map(ProductDTO::getQuantity)
//                    .findAny().orElse(0));
            spinBox.setValue(cart.getProducts().stream()
                    .filter(x -> x.getName().equals(product.getName()))
                    .map(ProductDTO::getQuantity)
                    .findAny().orElse(0));
            spinBox.oldValue = spinBox.getValue();
            spinBox.valueChanged.connect((Integer value) -> {
                if (!changeRemoteBasket(product, value)) {
                    spinBox.blockSignals(true);
                    spinBox.setValue(spinBox.oldValue);
                    spinBox.blockSignals(false);
                } else {
                    spinBox.oldValue = value;
                }
            });
            cartTable.setCellWidget(i, 3, spinBox);
        }
        buyButton.setEnabled(true);
    }

    private final QTableWidget ordersTable;
    private void setOrdersTabContents() {
        ordersTable.setRowCount(0);
        List<OrderDTO> orders = getOrders();
        if (orders == null) {
            return;
        }
        ordersTable.setRowCount(orders.size());
        for (int i = 0; i < orders.size(); i++) {
            OrderDTO order = orders.get(i);
            ordersTable.setItem(i, 0, new QTableWidgetItem(order.getOrderDate().toString()));
            ordersTable.setItem(i, 1, new QTableWidgetItem(order.getOrderStatus()));
            ordersTable.setItem(i, 2, new QTableWidgetItem(priceToString(order.getTotalAmount())));
            QPushButton showDetailsButton = new QPushButton("show", ordersTable);
            showDetailsButton.clicked.connect(() -> showOrderContents(order.getBasket().getProducts()));
            ordersTable.setCellWidget(i, 3, showDetailsButton);
        }
    }
    private void showOrderContents(List<ProductDTO> products) {
        QDialog orderContentsDialog = new QDialog(this);
        QVBoxLayout dialogLayout = new QVBoxLayout(orderContentsDialog);

        QTableWidget orderContentsTable = new QTableWidget(orderContentsDialog);
        dialogLayout.addWidget(orderContentsTable);
        orderContentsTable.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers);
        orderContentsTable.setColumnCount(5);
        orderContentsTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "Amount", "Total"));
        orderContentsTable.setRowCount(products.size());
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            orderContentsTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            orderContentsTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            orderContentsTable.setItem(i, 2, new QTableWidgetItem(priceToString(product.getPrice())));
            orderContentsTable.setItem(i, 3, new QTableWidgetItem(product.getQuantity().toString()));
            double total = product.getQuantity() * product.getPrice();
            orderContentsTable.setItem(i, 4, new QTableWidgetItem(priceToString(total)));
        }
        orderContentsDialog.exec();
    }

    private final QPushButton buyButton;
    private final QComboBox userComboBox;
    private String getUserName() {
        return userComboBox.getCurrentText();
    }
    private static String getCardId(String userName) {
        if (userName.equals("John Doe")) {
            return "C0001";
        } else {
            return "C0002";
        }
    }
    private static String getUserToken(String userName) {
        if (userName.equals("John Doe")) {
            return "am9obkBleGFtcGxlLmNvbSYx";
        } else {
            return "YWxpY2VAZXhhbXBsZS5jb20mMg==";
        }
    }
    private static final BasketDTO johnsCart = BasketDTO.builder()
            .basketId(null)
            .basketStatus(null)
            .subtotalAmount(null)
            .products(new ArrayList<>())
            .build();
    private static final BasketDTO alicesCart = BasketDTO.builder()
            .basketId(null)
            .basketStatus(null)
            .subtotalAmount(null)
            .products(new ArrayList<>())
            .build();

    private static String priceToString(double price) {
        return "%.2f".formatted(price);
    }

    private void searchProducts(String name, String category) {//TODO may fail, may retry
        List<ProductDTO> result = new ArrayList<>();
        WebClient webClient = WebClient.create("http://localhost:8084");
        if (name.isEmpty() && category.equals("<all>")) {
            Mono<ResponseEntity<String>> productsMono = webClient.get()
                    .uri("/api/products")
                    .retrieve()
                    .toEntity(String.class);
            ResponseEntity<String> response = productsMono.block();
            assert response != null;
            JSONObject body = new JSONObject(response.getBody());
            JSONArray data = body.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                ProductDTO newProduct = parseProduct(object);
                if (newProduct.getProductId() == null) {
                    log.info("blocked null id product");
                } else {
                    result.add(newProduct);
                }
            }
        } else if (!name.isEmpty() && category.equals("<all>")) {
            Mono<ResponseEntity<String>> productsMono = webClient.get()
                    .uri("/api/products/contains/{contain}", name)
                    .retrieve()
                    .toEntity(String.class);
            ResponseEntity<String> response = productsMono.block();
            assert response != null;
            JSONObject body = new JSONObject(response.getBody());
            JSONArray data = body.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                ProductDTO product = parseProduct(object);
                if (product.getProductId() == null) {
                    log.info("blocked null id product");
                } else {
                    result.add(product);
                }
            }
        } else {
            Mono<ResponseEntity<String>> productsMono = webClient.get()
                    .uri("/api/products/category/{category}", category)
                    .retrieve()
                    .toEntity(String.class);
            ResponseEntity<String> response = productsMono.block();
            assert response != null;
            JSONObject body = new JSONObject(response.getBody());
            JSONArray data = body.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                ProductDTO product = parseProduct(object);
                if (product.getProductId() == null) {
                    log.info("blocked null id product");
                } else {
                    result.add(product);
                }
            }
            if (!name.isEmpty()) {
                result = new ArrayList<>(result.stream()
                        .filter(x -> x.getName().contains(name))
                        .toList());
            }
        }

        searchResults = result;
        refreshSearchResultsTable();
    }

    private static ProductDTO parseProduct(JSONObject product) {
        return ProductDTO.builder()
                .productId(product.isNull("productId") ? null : product.getLong("productId"))
                .name(product.getString("name"))
                .category(product.getString("category"))
                .price(product.getDouble("price"))
                .quantity(product.isNull("quantity") ? null : product.getInt("quantity"))
                .build();
    }

    //TODO may fail, may retry
    private void sendOrder() {
        String cardId = getCardId(getUserName());
        WebClient.create("http://localhost:8084").post()
                .uri("/api/orders/{cardId}", cardId)
                .header("User-Token", getUserToken(getUserName()))
                .retrieve().toEntity(String.class).block();
        refreshCurrentTab.run();
    }

    private List<OrderDTO> getOrders() {
        Mono<ResponseEntity<String>> ordersMono = WebClient.create("http://localhost:8084").get()
                .uri("/api/orders")
                .header("User-Token", getUserToken(getUserName()))
                .retrieve()
                .toEntity(String.class);
        ResponseEntity<String> response = ordersMono.block();
        JSONObject body = new JSONObject(response.getBody());
        JSONArray array = body.getJSONArray("data");
        List<OrderDTO> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            result.add(OrderDTO.builder()
                    .orderId(object.getLong("orderId"))
                    .orderDate(Date.from(ZonedDateTime.parse(object.getString("orderDate"), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant()))
                    .orderStatus(object.getString("orderStatus"))
                    .totalAmount(object.getDouble("totalAmount"))
                    .basket(parseBasket(object.getJSONObject("basket")))
                    .build());
        }
        return result;
    }
    private static BasketDTO parseBasket2(JSONObject basket) {
        List<ProductDTO> productsList = new ArrayList<>();
        JSONArray products = basket.getJSONArray("products");
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            ProductDTO productDTO = parseProduct(product);
            if (productDTO.getProductId() == null) {
                log.info("detected null id product in parseBasket2");
                productsList.add(productDTO);
            } else {
                productsList.add(productDTO);
            }
        }
        return BasketDTO.builder()
                .basketId(basket.getLong("basketId"))
                .basketStatus(basket.getString("basketStatus"))
                .subtotalAmount(basket.getDouble("subtotalAmount"))
                .products(productsList)
                .build();
    }

    private static BasketDTO parseBasket(JSONObject basket) {
        List<ProductDTO> productsList = new ArrayList<>();
        JSONArray products = basket.getJSONArray("products");
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            ProductDTO productDTO = parseProduct(product);
            if (productDTO.getProductId() == null) {
                log.info("blocked null id product");
            } else {
                productsList.add(productDTO);
            }
        }
        return BasketDTO.builder()
                .basketId(basket.getLong("basketId"))
                .basketStatus(basket.getString("basketStatus"))
                .subtotalAmount(basket.getDouble("subtotalAmount"))
                .products(productsList)
                .build();
    }

    private BasketDTO getLocalBasket() {
        if (getUserName().equals("John Doe")) {
            return johnsCart;
        }
        return alicesCart;
    }

    private void changeLocalBasket(ProductDTO product, Integer amount) {
        BasketDTO cart = getLocalBasket();
        Optional<ProductDTO> foundProduct = cart.getProducts().stream()
                .filter(x -> x.getProductId().equals(product.getProductId()))
                .findAny();
        if (foundProduct.isEmpty()) {
            cart.getProducts().add(ProductDTO.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .category(product.getCategory())
                            .quantity(amount)
                            .build());
        } else {
            foundProduct.get().setQuantity(amount);
        }
    }

    private BasketDTO getRemoteBasket() {//TODO may fail. may be repeated.
        Mono<ResponseEntity<String>> basketMono = WebClient.create("http://localhost:8084")
                .get()
                .uri("/api/basket")
                .header("User-Token", getUserToken(getUserName()))
                .retrieve()
                .toEntity(String.class);
        ResponseEntity<String> response = basketMono.block();
        JSONObject body = new JSONObject(response.getBody());
        JSONObject cart = body.getJSONObject("data");
        return parseBasket2(cart);
    }

    private boolean changeRemoteBasket(ProductDTO localProduct, Integer amount) {
        BasketDTO cart = getRemoteBasket();
        int previousAmount = cart.getProducts().stream()
//                .filter(x -> {
//                    if (x.getProductId() == null) {
//                        log.info("blocked null id product");
//                        return false;
//                    }
//                    return true;
//                })
                .filter(x -> x.getName().equals(localProduct.getName()))
                .map(x -> x.getQuantity())
                .findAny().orElse(0);
        if (amount == previousAmount) return true;
        if (amount < previousAmount) {
            WebClient.create("http://localhost:8084").delete()
                    .uri("/api/basket/{productId}/{productQuantity}", localProduct.getProductId().toString(), String.valueOf(previousAmount - amount))
                    .header("User-Token", getUserToken(getUserName()))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            return true;
        } else {
            WebClient.create("http://localhost:8084").put()
                    .uri("/api/basket/{productId}/{productQuantity}", localProduct.getProductId().toString(), String.valueOf(amount - previousAmount))
                    .header("User-Token", getUserToken(getUserName()))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
            return true;
        }
    }

    private void pushLocalBasket() {
        clearRemoteBasket();
        BasketDTO localCart = getLocalBasket();
        localCart.getProducts().forEach(x -> changeRemoteBasket(x, x.getQuantity()));
    }

    long getProductIdFromName(String name) {
        if (name.equals("Smartphone")) {
            return 1L;
        }
        if (name.equals("Laptop")) {
            return 2L;
        }
        return 3L;
    }

    private void clearRemoteBasket() {
//        BasketDTO remoteCart = getRemoteBasket();
//        remoteCart.getProducts().forEach(x -> changeRemoteBasket(x, 0)); //bit weird
    }

    private static void clearRemoteBaskets() {
//        {
//            Mono<ResponseEntity<String>> basketMono = WebClient.create("http://localhost:8084")
//                    .get()
//                    .uri("/api/basket")
//                    .header("User-Token", getUserToken("John Doe"))
//                    .retrieve()
//                    .toEntity(String.class);
//            ResponseEntity<String> response = basketMono.block();
//            JSONObject body = new JSONObject(response.getBody());
//            JSONObject cart = body.getJSONObject("data");
//            BasketDTO johnsRemoteBasket = parseBasket(cart);
//            johnsRemoteBasket.getProducts().forEach(x -> {
//                WebClient.create("http://localhost:8084").delete()
//                        .uri("/api/basket/{productId}/{productQuantity}", x.getProductId().toString(), x.getQuantity().toString())
//                        .header("User-Token", getUserToken("John Doe"))
//                        .retrieve()
//                        .toEntity(String.class)
//                        .block();
//            });
//        }
//        {
//            Mono<ResponseEntity<String>> basketMono = WebClient.create("http://localhost:8084")
//                    .get()
//                    .uri("/api/basket")
//                    .header("User-Token", getUserToken("Alice Smith"))
//                    .retrieve()
//                    .toEntity(String.class);
//            ResponseEntity<String> response = basketMono.block();
//            JSONObject body = new JSONObject(response.getBody());
//            JSONObject cart = body.getJSONObject("data");
//            BasketDTO alicesRemoteBasket = parseBasket(cart);
//            alicesRemoteBasket.getProducts().forEach(x -> {
//                WebClient.create("http://localhost:8084").delete()
//                        .uri("/api/basket/{productId}/{productQuantity}", x.getProductId().toString(), x.getQuantity().toString())
//                        .header("User-Token", getUserToken("Alice Smith"))
//                        .retrieve()
//                        .toEntity(String.class)
//                        .block();
//            });
//        }
    }
}
