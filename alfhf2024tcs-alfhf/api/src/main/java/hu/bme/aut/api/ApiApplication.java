package hu.bme.aut.api;

import io.qt.widgets.QApplication;
import io.qt.widgets.QWidget;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@RequiredArgsConstructor
@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

    public static void main(String[] args) {

//        SpringApplication.run(ApiApplication.class, args);
        QApplication.initialize(new String[]{});
        QWidget window = new QWidget();
        window.show();
        QApplication.exec();
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
