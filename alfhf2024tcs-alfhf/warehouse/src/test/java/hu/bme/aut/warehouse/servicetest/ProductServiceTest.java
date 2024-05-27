package hu.bme.aut.warehouse.servicetest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import hu.bme.aut.warehouse.dto.ProductDTO;
import hu.bme.aut.warehouse.entity.Product;
import hu.bme.aut.warehouse.entity.Category;
import hu.bme.aut.warehouse.repository.ProductRepository;
import hu.bme.aut.warehouse.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        Product product1 = new Product(1L, "Product1", 10.0, new Category(1L, "Category1"));
        Product product2 = new Product(2L, "Product2", 20.0, new Category(1L, "Category1"));
        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductDTO> products = productService.getAllProducts();

        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Product1");
        assertThat(products.get(1).getName()).isEqualTo("Product2");
    }

    @Test
    public void testGetProductsByCategory() {
        Product product = new Product(1L, "Product1", 10.0, new Category(1L, "Category1"));
        when(productRepository.findByCategoryName(anyString())).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getProductsByCategory("Category1");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Product1");
    }

    @Test
    public void testGetProductById() {
        Product product = new Product(1L, "Product1", 10.0, new Category(1L, "Category1"));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        ProductDTO foundProduct = productService.getProductById(1L);

        assertThat(foundProduct.getName()).isEqualTo("Product1");
    }

    @Test
    public void testGetProductsByPrefix() {
        Product product = new Product(1L, "Product1", 10.0, new Category(1L, "Category1"));
        when(productRepository.findByNameStartingWith(anyString())).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getProductsByPrefix("Pro");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Product1");
    }



    @Test
    public void testGetProductByNameContaining() {
        Product product = new Product(1L, "Product1", 10.0, new Category(1L, "Category1"));
        when(productRepository.findByNameContaining(anyString())).thenReturn(Arrays.asList(product));

        List<ProductDTO> products = productService.getProductByNameContaining("duct");

        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).isEqualTo("Product1");
    }

    @Test
    public void testUpdateProductPriceByName() {
        when(productRepository.updateProductPriceByName(anyDouble(), anyString())).thenReturn(1);

        int updatedCount = productService.updateProductPriceByName(15.0, "Product1");

        assertThat(updatedCount).isEqualTo(1);
        verify(productRepository, times(1)).updateProductPriceByName(15.0, "Product1");
    }
}
