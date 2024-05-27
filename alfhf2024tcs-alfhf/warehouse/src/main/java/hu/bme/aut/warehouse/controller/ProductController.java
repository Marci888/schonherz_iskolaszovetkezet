package hu.bme.aut.warehouse.controller;


import hu.bme.aut.warehouse.dto.ProductDTO;
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
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        //log.info("Listing all products: {}",products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/category/{categoryName}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String categoryName) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryName);
        log.info("Listing products by {} Category: {}",categoryName,products);
        return ResponseEntity.ok(products);
    }
    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<List<ProductDTO>> getProductsByPrefix(@PathVariable String prefix) {
        List<ProductDTO> products = productService.getProductsByPrefix(prefix);
        log.info("Listing products by {} prefix: {}",prefix,products);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/contains/{contain}")
    public ResponseEntity<List<ProductDTO>> getProductsByContaining(@PathVariable String cont){
        List<ProductDTO> products = productService.getProductByNameContaining(cont);
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
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        log.info("Getting product by {} ID",id);
        return ResponseEntity.ok(product);
    }
}