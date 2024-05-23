package hu.bme.aut.api.controller;
import hu.bme.aut.warehouse.entity.Product;
import lombok.extern.slf4j.Slf4j;
import hu.bme.aut.warehouse.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("/api/warehouse")
public class ProductController {
    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        log.info("Request to retrieve allproducts ");
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getAllProducts();
            ApiResponse<BasketDTO> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<BasketDTO> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
    public ResponseEntity<String> updateProductPriceByName(@RequestParam Double price, @RequestParam String name) {
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