package hu.bme.aut.api.controller;
import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ProductDTO;
import hu.bme.aut.api.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        log.info("Request to retrieve all products ");
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getAllProducts();
            ApiResponse<List<ProductDTO>> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<List<ProductDTO>> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable String categoryName) {
        log.info("Request to retrieve products by {} category ", categoryName);
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getProductsByCategory(categoryName);
            ApiResponse<List<ProductDTO>> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<List<ProductDTO>> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        log.info("Request to retrieve product by {} ID ", id);
        try {
            CompletableFuture<ApiResponse<ProductDTO>> future = productService.getProductById(id);
            ApiResponse<ProductDTO> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving product {}", e.getMessage());
            ApiResponse<ProductDTO> errorResponse = ApiResponse.<ProductDTO>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/prefix/{prefix}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByPrefix(@PathVariable String prefix) {
        log.info("Request to retrieve products by {} prefix ", prefix);
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getProductsByPrefix(prefix);
            ApiResponse<List<ProductDTO>> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<List<ProductDTO>> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategoryId(@PathVariable Long categoryId) {
        log.info("Request to retrieve products by {} categoryID ", categoryId);
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getProductsByCategoryId(categoryId);
            ApiResponse<List<ProductDTO>> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<List<ProductDTO>> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/contains/{contain}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByContaining(@PathVariable String cont) {
        log.info("Request to retrieve products by containing {} ", cont);
        try {
            CompletableFuture<ApiResponse<List<ProductDTO>>> future = productService.getProductByNameContaining(cont);
            ApiResponse<List<ProductDTO>> response = future.get();
            return ResponseEntity.ok(response);
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            log.error("Error retrieving products {}", e.getMessage());
            ApiResponse<List<ProductDTO>> errorResponse = ApiResponse.<List<ProductDTO>>builder()
                    .success(false)
                    .errorMessage("Internal server error")
                    .errorCode("1500")
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/updatePrice")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProductPriceByName(@RequestParam("Price") Double price,
                                                                            @RequestParam("Name") String name) {
            log.info("Updating {} product's price to {} ",name, price);
            try {
                CompletableFuture<ApiResponse<ProductDTO>> future = productService.updateProductPriceByName(price,name);
                ApiResponse<ProductDTO> response = future.get();
                return ResponseEntity.ok(response);
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                log.error("Error retrieving product {}", e.getMessage());
                ApiResponse<ProductDTO> errorResponse = ApiResponse.<ProductDTO>builder()
                        .success(false)
                        .errorMessage("Internal server error")
                        .errorCode("1500")
                        .data(null)
                        .build();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
    }
}