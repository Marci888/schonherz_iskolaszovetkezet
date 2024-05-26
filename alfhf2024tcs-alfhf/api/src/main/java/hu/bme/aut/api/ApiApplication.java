package hu.bme.aut.api;

import hu.bme.aut.api.frontend.MyWindow;
import io.qt.widgets.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

    public static void main(String[] args) {

//        SpringApplication.run(ApiApplication.class, args);
        QApplication.initialize(new String[]{});
        MyWindow window = new MyWindow();
        window.show();
        QApplication.exec();
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
