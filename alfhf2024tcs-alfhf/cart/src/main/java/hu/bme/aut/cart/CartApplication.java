package hu.bme.aut.cart;

import hu.bme.aut.cart.init.DataInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@RequiredArgsConstructor
@SpringBootApplication
public class CartApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
