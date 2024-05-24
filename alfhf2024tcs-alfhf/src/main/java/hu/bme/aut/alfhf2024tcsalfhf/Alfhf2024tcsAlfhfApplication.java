package hu.bme.aut.alfhf2024tcsalfhf;

import hu.bme.aut.core.CoreApplication;
import hu.bme.aut.cart.CartApplication;
import hu.bme.aut.warehouse.WarehouseApplication;
import hu.bme.aut.api.ApiApplication;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiredArgsConstructor
@SpringBootApplication
public class Alfhf2024tcsAlfhfApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Alfhf2024tcsAlfhfApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Future<ConfigurableApplicationContext> coreFuture = executorService.submit(() -> SpringApplication.run(CoreApplication.class, "--spring.config.name=core", "--server.port=8082"));
        Future<ConfigurableApplicationContext> cartFuture = executorService.submit(() -> SpringApplication.run(CartApplication.class, "--spring.config.name=cart", "--server.port=8081"));
        Future<ConfigurableApplicationContext> warehouseFuture = executorService.submit(() -> SpringApplication.run(WarehouseApplication.class, "--spring.config.name=warehouse", "--server.port=8083"));
        Future<ConfigurableApplicationContext> apiFuture = executorService.submit(() -> SpringApplication.run(ApiApplication.class, "--spring.config.name=api", "--server.port=8084"));

        ConfigurableApplicationContext coreContext = coreFuture.get();
        ConfigurableApplicationContext cartContext = cartFuture.get();
        ConfigurableApplicationContext warehouseContext = warehouseFuture.get();
        ConfigurableApplicationContext apiContext = apiFuture.get();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (coreContext != null) coreContext.close();
            if (cartContext != null) cartContext.close();
            if (warehouseContext != null) warehouseContext.close();
            if (apiContext != null) apiContext.close();
        }));
    }
}