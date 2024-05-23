package  hu.bme.aut.api.service;

import hu.bme.aut.api.dto.ApiResponse;
import hu.bme.aut.api.dto.BasketDTO;
import hu.bme.aut.api.dto.ProductDTO;
import hu.bme.aut.api.dto.ErrorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final RestTemplate restTemplate;
    private final ModelMapper modelMapper;

    @Value("${warehouse.service.url}")
    private String warehouseServiceBaseUrl;



    @Async
    public CompletableFuture<ApiResponse<List<ProductDTO>>> getAllProducts() {

        String url = warehouseServiceBaseUrl;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody,List<ProductDTO>.class);
                return CompletableFuture.completedFuture(new ApiResponse<>(true,null,null,productDTOS));
            }
            else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve products: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error during products retrieval: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }

    @Async
    public CompletableFuture<Apiresponse<List<Product>>> getProductsByCategory(String categoryName) {

        String url = warehouseServiceBaseUrl + categoryName;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody,List<ProductDTO>.class);
                return CompletableFuture.completedFuture(new ApiResponse<>(true,null,null,productDTOS));
            }
            else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve products: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error during products retrieval: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }

    }
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    public List<Product> getProductsByPrefix(String prefix) {
        return productRepository.findByNameStartingWith(prefix);
    }
    public List<Product> getProductsByCategoryId(Long id){
        return  productRepository.findByCategoryId(id);
    }
    public List<Product> getProductByNameContaining(String name){
        return productRepository.findByNameContaining(name);
    }
    public int updateProductPriceByName(Double price,String name){
        return productRepository.updateProductPriceByName(price,name);
    }

}