package com.springdatajpa.springdatajpa.init;
import com.springdatajpa.springdatajpa.entity.Category;
import com.springdatajpa.springdatajpa.entity.Product;
import com.springdatajpa.springdatajpa.repository.CategoryRepository;
import com.springdatajpa.springdatajpa.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public DataInitializer(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Először töröljük az összes rekordot
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Inicializáljuk az adatokat
        Category category1 = new Category();
        category1.setName("Electronics");
        categoryRepository.save(category1);

        Category category2 = new Category();
        category2.setName("Books");
        categoryRepository.save(category2);

        Product product1 = Product.builder()
                .price(20.2)
                .name("Smartphone")
                .category(category1)
                .build();
        productRepository.save(product1);

        Product product2 = new Product();
        product2.setName("Laptop");
        product2.setPrice(999.99);
        product2.setCategory(category1);
        productRepository.save(product2);

        Product product3 = new Product();
        product3.setName("Science Fiction Book");
        product3.setPrice(19.99);
        product3.setCategory(category2);
        productRepository.save(product3);

        System.out.println("Data initialization complete.");
    }
}

