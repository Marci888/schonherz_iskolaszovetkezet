package hu.bme.aut.warehouse.controller;


import hu.bme.aut.warehouse.entity.Product;
import hu.bme.aut.warehouse.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/warehouse")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        log.info("Listing all products: {}",products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/{categoryName}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryName) {
        List<Product> products = productService.getProductsByCategory(categoryName);
        log.info("Listing products by {} Category: {}",categoryName,products);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<List<Product>> getProductsByPrefix(@PathVariable String prefix) {
        List<Product> products = productService.getProductsByPrefix(prefix);
        log.info("Listing products by {} prefix: {}",prefix,products);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        log.info("Listing products by a Category's ID");
        if (products.isEmpty()) {
            log.info("No matching ID found");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("The products from the Category: {}",products);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @GetMapping("/contains/{contain}")
    public ResponseEntity<List<Product>> getProductsByContaining(@PathVariable String cont){
        List<Product> products = productService.getProductByNameContaining(cont);
        log.info("Listing products containing {}: {}",cont,products);
        return ResponseEntity.ok(products);
    }
    @PutMapping("/updatePrice")
    public ResponseEntity<String> updateProductPriceByName(@RequestParam("Price") Double price, @RequestParam("Name") String name) {
        int updatedRows = productService.updateProductPriceByName(price, name);
        log.info("Updating {} product's price to: {}",name, price);
        if (updatedRows > 0) {
            log.info("Update completed");
            return ResponseEntity.ok("Product price updated successfully.");
        } else {
            log.info("Product not found");
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.info("Getting a specific product from {} ID",id);
        Optional<Product> product = productService.getProductById(id);
        if(product.isEmpty())
            log.info("No product with that ID");
        else
            log.info("Got {} product",product);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}