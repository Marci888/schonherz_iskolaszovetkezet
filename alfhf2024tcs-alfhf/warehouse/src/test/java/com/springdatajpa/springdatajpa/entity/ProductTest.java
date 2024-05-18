package com.springdatajpa.springdatajpa.entity;

import com.springdatajpa.springdatajpa.repository.CategoryRepository;
import com.springdatajpa.springdatajpa.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ProductTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Test
    public void saveProduct(){
        Category category = Category.builder()
                .name("hea")
                .build();
        categoryRepository.save(category);

        Product product = Product.builder()
                .price(20.2)
                .name("Hihoo")
                .category(category)
                .build();
        productRepository.save(product);
    }
    @Test
    public void printAllProduct(){
        List<Product> productList = productRepository.findAll();
        System.out.println("productList = " + productList);
    }
}