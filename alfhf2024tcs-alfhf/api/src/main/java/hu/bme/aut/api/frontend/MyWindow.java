package hu.bme.aut.api.frontend;

import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.OrderDTO;
import hu.bme.aut.api.dto.ProductDTO;
import io.qt.widgets.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

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
        searchForm.addRow("category:", categoryComboBox);
        QPushButton searchButton = new QPushButton("search", searchTab);
        searchForm.addRow(searchButton);

        searchResultsTable = new QTableWidget(searchTab);
        searchTabLayout.addWidget(searchResultsTable);
        searchResultsTable.setColumnCount(4);
        searchResultsTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In cart"));

        totalLabel = new QLabel("Total: " + priceToString(0.0));
        cartTabLayout.addWidget(totalLabel);

        cartTable = new QTableWidget(cartTab);
        cartTabLayout.addWidget(cartTable);
        cartTable.setColumnCount(4);
        cartTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In cart"));

        QPushButton buyButton = new QPushButton("buy", cartTab);
        cartTabLayout.addWidget(buyButton);

        ordersTable = new QTableWidget(ordersTab);
        ordersTabLayout.addWidget(ordersTable);
        ordersTable.setColumnCount(4);
        ordersTable.setHorizontalHeaderLabels(List.of("Date", "Status", "Price", "Contents"));

//        //this is a test
//        setOrdersTabContents(List.of(new OrderDTO(0L, Date.valueOf("1010-10-10"), "completed", 250.0, new BasketDTO(
//        0L, "completed", 250.0, List.of(new ProductDTO(0L, "name", "category", 250.0, 1))))));

        Runnable refreshCurrentTab = () -> {
            String currentTab = tabWidget.currentWidget().objectName();
            switch (currentTab) {
                case "searchTab" -> setProductSpinboxTableContents(searchResults, searchResultsTable);
                case "cartTab" -> {
                    BasketDTO basket = getBasket();
                    if (basket != null) {
                        setCartTabContents(basket);
                    }
                }
                case "ordersTab" -> {
                    List<OrderDTO> orders = getOrders();
                    if (orders != null) {
                        setOrdersTabContents(orders);
                    }
                }
                default ->
                    //TODO this is for debug purposes
                        throw new RuntimeException("refreshCurrentTab currentWidget has no match");
            }
        };
        userComboBox.currentTextChanged.connect((String s) -> refreshCurrentTab.run());
        tabWidget.currentChanged.connect(refreshCurrentTab::run);
        buyButton.clicked.connect(() -> {
            sendOrder();
            refreshCurrentTab.run();
        });
        searchButton.clicked.connect(() -> {
            String name = nameLineEdit.text();
            String category = categoryComboBox.currentText();
            if (queryProducts(name, category)) {
                setSearchResultsTableContents(searchResults);
            }
        });
    }

    private final QTableWidget searchResultsTable;
    List<ProductDTO> searchResults = new ArrayList<>();
    private void setSearchResultsTableContents(List<ProductDTO> products) {
        setProductSpinboxTableContents(products, searchResultsTable);
    }
    private final QTableWidget cartTable;
    private final QLabel totalLabel;
    private void setCartTabContents(BasketDTO basket) {
        String priceString = basket == null ? priceToString(0.0) : priceToString(basket.getSubtotalAmount());
        totalLabel.setText("Total: " + priceString);
        List<ProductDTO> products = basket == null ? new ArrayList<>() : basket.getProducts();
        setProductSpinboxTableContents(products, cartTable);
    }
    private void setProductSpinboxTableContents(List<ProductDTO> products, QTableWidget table) {
        cartTable.setRowCount(products.size());
        BasketDTO cart = getBasket();
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            cartTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            cartTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            cartTable.setItem(i, 2, new QTableWidgetItem(priceToString(product.getPrice())));
            var spinBox = new QSpinBox(cartTable) {public int oldValue = 0;};
            if (cart != null) {
                spinBox.setValue(cart.getProducts().stream()
                        .filter(x -> x.getProductId().equals(product.getProductId()))
                        .map(ProductDTO::getQuantity)
                        .findAny().orElse(0));
                spinBox.oldValue = spinBox.getValue();
            }
            spinBox.valueChanged.connect(() -> {
                int value = spinBox.getValue();
                if (changeBasket(product.getProductId(), value)) {
                    spinBox.blockSignals(true);
                    spinBox.setValue(spinBox.oldValue);
                    spinBox.blockSignals(false);
                } else {
                    spinBox.oldValue = value;
                }
            });
            cartTable.setCellWidget(i, 4, spinBox);
        }
    }

    private final QTableWidget ordersTable;
    private void setOrdersTabContents(List<OrderDTO> orders) {
        ordersTable.setRowCount(orders.size());
        for (int i = 0; i < orders.size(); i++) {
            OrderDTO order = orders.get(i);
            ordersTable.setItem(i, 0, new QTableWidgetItem(order.getOrderDate().toString()));
            ordersTable.setItem(i, 1, new QTableWidgetItem(order.getOrderStatus()));
            ordersTable.setItem(i, 2, new QTableWidgetItem(priceToString(order.getTotalAmount())));
            QPushButton showDetailsButton = new QPushButton("show", ordersTable);
            showDetailsButton.clicked.connect(() -> {
                showOrderContents(order.getBasket());
            });
            ordersTable.setCellWidget(i, 3, showDetailsButton);
        }
    }
    private void showOrderContents(BasketDTO basket) {
        QDialog orderContentsDialog = new QDialog(this);
        QVBoxLayout dialogLayout = new QVBoxLayout(orderContentsDialog);

        QTableWidget orderContentsTable = new QTableWidget(orderContentsDialog);
        dialogLayout.addWidget(orderContentsTable);
        orderContentsTable.setColumnCount(5);
        orderContentsTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "Amount", "Total"));
        orderContentsTable.setRowCount(basket.getProducts().size());
        for (int i = 0; i < basket.getProducts().size(); i++) {
            ProductDTO product = basket.getProducts().get(i);
            orderContentsTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            orderContentsTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            orderContentsTable.setItem(i, 2, new QTableWidgetItem(priceToString(product.getPrice())));
            orderContentsTable.setItem(i, 3, new QTableWidgetItem(product.getQuantity().toString()));
            double total = product.getQuantity() * product.getPrice();
            orderContentsTable.setItem(i, 4, new QTableWidgetItem(priceToString(total)));
        }
        orderContentsDialog.exec();
    }

    //TODO-------------------------
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

    private static String priceToString(double price) {
        return "%.2f".formatted(price);
    }

    // GET api/products
    // GET api/products/category/{category}
    // GET api/products/contains/{contains}
    //an empty list will be displayed as no products
    private boolean queryProducts(String name, String category) {
        return false;
    }

    // POST api/orders/{cardid}
    private void sendOrder() {
    }

    // GET api/orders
    //an empty list will be displayed as no orders
    private ArrayList<OrderDTO> getOrders() {
        return new ArrayList<>();
    }

    // GET api/basket
    //null will be displayed as an empty basket
    private BasketDTO getBasket() {
        return null;
    }

    // DELETE
    // PUT
    private boolean changeBasket(Long productId, int newAmount) {
        return false;
    }
}
