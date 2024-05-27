package hu.bme.aut.warehouse.service;

import hu.bme.aut.warehouse.dto.ProductDTO;
import hu.bme.aut.warehouse.entity.Product;
import hu.bme.aut.warehouse.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> Hibernate.initialize(product.getCategory()));
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private ProductDTO convertToDTO(Product product) {
         ProductDTO productDTO = ProductDTO.builder()
                .productId(product.getId())
                .category(product.getCategory().getName())
                .price(product.getPrice())
                .name(product.getName())
                .build();
         return productDTO;
    }
    private ProductDTO convertToDTO(Optional<Product> optionalProduct) {
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            return ProductDTO.builder()
                    .productId(product.getId())
                    .category(product.getCategory().getName())
                    .price(product.getPrice())
                    .name(product.getName())
                    .build();
        } else {
            // Handle the case where the product is not present
            return null; // or throw an exception
        }
    }
    public List<ProductDTO> getProductsByCategory(String categoryName) {
        List<Product> products = productRepository.findByCategoryName(categoryName);
        products.forEach(product -> Hibernate.initialize(product.getCategory()));
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public ProductDTO getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return  convertToDTO(product);


    }
    public List<ProductDTO> getProductsByPrefix(String prefix) {
        List<Product> products = productRepository.findByNameStartingWith(prefix);
        products.forEach(product -> Hibernate.initialize(product.getCategory()));
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public List<ProductDTO> getProductByNameContaining(String name){
        List<Product> products = productRepository.findByNameContaining(name);
        products.forEach(product -> Hibernate.initialize(product.getCategory()));
        return products.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
    public int updateProductPriceByName(Double price,String name){
        return productRepository.updateProductPriceByName(price,name);
    }

}