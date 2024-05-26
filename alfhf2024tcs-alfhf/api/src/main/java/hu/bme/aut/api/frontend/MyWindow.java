package hu.bme.aut.api.frontend;

import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.OrderDTO;
import hu.bme.aut.api.dto.ProductDTO;
import io.qt.concurrent.QtConcurrent;
import io.qt.core.QEventLoop;
import io.qt.core.QFutureWatcher;
import io.qt.widgets.*;

import java.util.ArrayList;
import java.util.List;

public class MyWindow extends QWidget {
    public MyWindow() {
        super();
        QVBoxLayout layout = new QVBoxLayout(this);

        QComboBox userComboBox = new QComboBox(this);
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
        searchResultsTable.setColumnCount(5);
        searchResultsTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In stock", "In cart"));

        totalLabel = new QLabel("Total: " + priceToString(0.0));
        cartTabLayout.addWidget(totalLabel);

        cartTable = new QTableWidget(cartTab);
        cartTabLayout.addWidget(cartTable);
        cartTable.setColumnCount(5);
        cartTable.setHorizontalHeaderLabels(List.of("Name", "Category", "Price", "In stock", "In cart"));

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
            if (currentTab.equals("searchTab")) {
                setProductSpinboxTableContents(searchResults, searchResultsTable);
            } else if (currentTab.equals("cartTab")) {
                BasketDTO basket = null; //TODO get basket
                setCartTabContents(basket);
            } else if (currentTab.equals("ordersTab")) {
                List<OrderDTO> orders = new ArrayList<>(); //TODO get orders
                setOrdersTabContents(orders);
            } else {
                //TODO this is for debug purposes
                throw new RuntimeException("refreshCurrentTab currentWidget has no match");
            }
        };

        userComboBox.currentTextChanged.connect((String s) -> {
//            QApplication.setOverrideCursor(QCursor.CursorShape.WaitCursor);
            var future = QtConcurrent.run(() -> { //refresh current tab
                //TODO change user
                refreshCurrentTab.run();
            });
            QFutureWatcher<Void> futureWatcher = new QFutureWatcher<>();
            futureWatcher.setFuture(future);
            QEventLoop eventLoop = new QEventLoop();
            futureWatcher.finished.connect(eventLoop::exit);
            futureWatcher.canceled.connect(() -> {
                //TODO this is for debug purposes
                throw new RuntimeException("userComboBox.currentTextChanged future canceled");
            });
            eventLoop.exec();
        });
        tabWidget.currentChanged.connect(refreshCurrentTab::run);
        buyButton.clicked.connect(() -> {
//            QApplication.setOverrideCursor(QCursor.CursorShape.WaitCursor);
            var future = QtConcurrent.run(() -> {
                //TODO send order
                boolean success = true;
                if (success) {
                    setCartTabContents(null); //show empty cart
                }
            });
            QFutureWatcher<Void> futureWatcher = new QFutureWatcher<>();
            futureWatcher.setFuture(future);
            QEventLoop eventLoop = new QEventLoop();
            futureWatcher.finished.connect(eventLoop::exit);
            futureWatcher.canceled.connect(() -> {
                //TODO this is for debug purposes
                throw new RuntimeException("tabWidget.currentChanged future canceled");
            });
            eventLoop.exec();
        });
        searchButton.clicked.connect(() -> {
            //TODO search
            var future = QtConcurrent.run(() -> {
                String name = nameLineEdit.text();
                String category = categoryComboBox.currentText();
                searchResults = new ArrayList<>(); //TODO query products
                setProductSpinboxTableContents(searchResults, searchResultsTable);
            });
            QFutureWatcher<Void> futureWatcher = new QFutureWatcher<>();
            futureWatcher.setFuture(future);
            QEventLoop eventLoop = new QEventLoop();
            futureWatcher.finished.connect(eventLoop::exit);
            futureWatcher.canceled.connect(() -> {
                //TODO this is for debug purposes
                throw new RuntimeException("searchButton.clicked future canceled");
            });
            eventLoop.exec();
        });

        //test
        var future = QtConcurrent.run(() -> {
            System.out.println("this is the future");
        });
        QFutureWatcher<Void> futureWatcher = new QFutureWatcher<>();
        futureWatcher.setFuture(future);
        futureWatcher.finished.connect(() -> {
            System.out.println("future completed");
        });
        futureWatcher.canceled.connect(() -> {
            System.out.println("future canceled");
        });
        QEventLoop eventLoop = new QEventLoop();
        System.out.println("Connecting future watcher to event loop");
        futureWatcher.finished.connect(eventLoop::exit);
        System.out.println("Starting event loop");
        eventLoop.exec();
        System.out.println("Finished event loop");
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
        for (int i = 0; i < products.size(); i++) {
            ProductDTO product = products.get(i);
            cartTable.setItem(i, 0, new QTableWidgetItem(product.getName()));
            cartTable.setItem(i, 1, new QTableWidgetItem(product.getCategory()));
            cartTable.setItem(i, 2, new QTableWidgetItem(product.getPrice().toString())); //TODO: currency
            cartTable.setItem(i, 3, new QTableWidgetItem(product.getQuantity().toString()));
            QSpinBox spinBox = new QSpinBox(cartTable);
            spinBox.setMaximum(product.getQuantity());
            //TODO spinBox.setValue(/*amount in cart*/)
            spinBox.valueChanged.connect(() -> {
                int value = spinBox.getValue();
                //TODO change cart
            });
            cartTable.setCellWidget(i, 5, spinBox);
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

    private static String priceToString(double price) {
        return String.valueOf(price); //TODO price to string
    }
}
