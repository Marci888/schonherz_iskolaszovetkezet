package com.springdatajpa.springdatajpa.service;

import com.springdatajpa.springdatajpa.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.springdatajpa.springdatajpa.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }
    public List<Product> getProductsByPrefix(String prefix) {
        return productRepository.findByNameStartingWith(prefix);
    }
}