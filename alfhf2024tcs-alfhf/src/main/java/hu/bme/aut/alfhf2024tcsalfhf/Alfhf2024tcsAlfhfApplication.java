package hu.bme.aut.alfhf2024tcsalfhf;

import hu.bme.aut.api.ApiApplication;
import hu.bme.aut.cart.CartApplication;
import hu.bme.aut.core.CoreApplication;
import hu.bme.aut.warehouse.WarehouseApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;


@RequiredArgsConstructor
@SpringBootApplication
public class Alfhf2024tcsAlfhfApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext coreContext = new SpringApplicationBuilder(CoreApplication.class)
                .profiles("core")
                .run(args);

        ConfigurableApplicationContext cartContext = new SpringApplicationBuilder(CartApplication.class)
                .profiles("cart")
                .run(args);

        ConfigurableApplicationContext warehouseContext = new SpringApplicationBuilder(WarehouseApplication.class)
                .profiles("warehouse")
                .run(args);

        ConfigurableApplicationContext apiContext = new SpringApplicationBuilder(ApiApplication.class)
                .profiles("api")
                .run(args);

        SpringApplication.run(Alfhf2024tcsAlfhfApplication.class, args);
    }
}