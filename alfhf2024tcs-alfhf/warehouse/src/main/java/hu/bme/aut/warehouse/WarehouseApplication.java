package hu.bme.aut.warehouse;

import hu.bme.aut.warehouse.init.DataInitializer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@RequiredArgsConstructor
@SpringBootApplication
public class WarehouseApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(WarehouseApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {

	}
}