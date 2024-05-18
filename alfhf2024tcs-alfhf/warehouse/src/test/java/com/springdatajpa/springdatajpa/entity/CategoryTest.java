package com.springdatajpa.springdatajpa.entity;

import com.springdatajpa.springdatajpa.repository.CategoryRepository;
import com.springdatajpa.springdatajpa.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CategoryTest {
    @Autowired
    private CategoryRepository categoryRepository;
    @Test
    public void saveProduct(){
        Category category = Category.builder()
                .name("hah")
                .build();
        categoryRepository.save(category);
    }
}