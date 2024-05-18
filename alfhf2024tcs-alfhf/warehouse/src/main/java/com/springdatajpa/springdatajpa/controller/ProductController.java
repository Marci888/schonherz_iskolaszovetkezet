package com.springdatajpa.springdatajpa.controller;

import com.springdatajpa.springdatajpa.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springdatajpa.springdatajpa.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/warehouse")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<List<Product>> getProductsByPrefix(@PathVariable String prefix) {
        List<Product> products = productService.getProductsByPrefix(prefix);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/contains/{contain}")
    public ResponseEntity<List<Product>> getProductsByContaining(@PathVariable String cont){
        List<Product> products = productService.getProductByNameContaining(cont);
        return ResponseEntity.ok(products);
    }
    @PutMapping("/updatePrice")
    public ResponseEntity<String> updateProductPriceByName(@RequestParam Double price, @RequestParam String name) {
        int updatedRows = productService.updateProductPriceByName(price, name);
        if (updatedRows > 0) {
            return ResponseEntity.ok("Product price updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
