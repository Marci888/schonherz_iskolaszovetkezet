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
        categoryComboBox.addItem("electronics");
        categoryComboBox.addItem("books");
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

        Runnable refreshCurrentTab = () -> {
            QWidget currentTab = tabWidget.currentWidget();
            if (currentTab == null) {
                throw new IllegalStateException("There is no active tab.");
            }
            String currentTabName = currentTab.objectName();
            switch (currentTabName) {
                case "searchTab" -> setSearchResultsTableContents(searchResults);
                case "cartTab" -> setCartTabContents();
                case "ordersTab" -> setOrdersTabContents();
                default ->
                    //TODO this is for debug purposes
                        throw new IllegalStateException("Could not match any tab.");
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
            searchProducts(name, category);
        });
    }

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
                changeLocalBasket(product.getProductId(), value);
            });
            searchResultsTable.setCellWidget(i, 4, spinBox);
        }
    }

    private final QTableWidget cartTable;
    private final QLabel totalLabel;
    private void setCartTabContents() {
        BasketDTO cart = getRemoteBasket();
        if (cart == null) {
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
            spinBox.setValue(cart.getProducts().stream()
                    .filter(x -> x.getProductId().equals(product.getProductId()))
                    .map(ProductDTO::getQuantity)
                    .findAny().orElse(0));
            spinBox.oldValue = spinBox.getValue();
            spinBox.valueChanged.connect((Integer value) -> {
                if (changeRemoteBasket(product.getProductId(), value)) {
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
    private void searchProducts(String name, String category) {
    }

    // POST api/orders/{cardid}
    private void sendOrder() {
    }

    // GET api/orders
    //an empty list will be displayed as no orders
    private ArrayList<OrderDTO> getOrders() {
        return new ArrayList<>();
    }

//    // GET api/basket
//    //null will be displayed as an empty basket
//    private BasketDTO getBasket() {
//        return null;
//    }
//
//    // DELETE
//    // PUT
//    private boolean changeBasket(Long productId, int newAmount) {
//        return false;
//    }

    private BasketDTO getLocalBasket() {
        return null;
    }

    private void changeLocalBasket(Long productId, Integer amount) {

    }

    private BasketDTO getRemoteBasket() {
        return null;
    }

    private boolean changeRemoteBasket(Long productId, Integer amount) {
        return false;
    }
}
