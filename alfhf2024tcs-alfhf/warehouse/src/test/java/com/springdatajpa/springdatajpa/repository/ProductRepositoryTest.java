package com.springdatajpa.springdatajpa.repository;

import com.springdatajpa.springdatajpa.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Test
    public void saveProduct() {
        Product product = Product.builder()
                .name("good")
                .price(23.54)
                .build();
        productRepository.save(product);
    }
    @Test
    public void printProductByPrefix(){
        List<Product> products =
                productRepository.findByNameStartingWith("hi");
        System.out.println("products = " + products);
    }
    @Test
    public void prindProductByNameContaining(){
        List<Product> products =
                productRepository.findByNameContaining("e");
        System.out.println("products = " + products);
    }
    @Test
    public void prindProductByCategoryName(){
        List<Product> products =
                productRepository.findByCategoryName("hea");
        System.out.println("products = " + products);
    }
    @Test
    public void updateProductByName(){
        productRepository.updateProductPriceByName(
                30.24,"Hihe"
        );
    }

}