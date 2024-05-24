package hu.bme.aut.core;

import hu.bme.aut.core.init.DataInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class CoreApplication implements CommandLineRunner {
    private final DataInitializer dataInitializer;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        dataInitializer.run();
    }
}