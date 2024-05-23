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

        String url = warehouseServiceBaseUrl + "/products";
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody(),List.class);
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
    public CompletableFuture<ApiResponse<List<ProductDTO>>> getProductsByCategory(String categoryName) {

        String url = warehouseServiceBaseUrl + "/products/" + categoryName;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody(),List.class);
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
    public CompletableFuture<ApiResponse<ProductDTO>> getProductById(Long id) {
        String url = warehouseServiceBaseUrl + "/products/" + id.toString();
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                ProductDTO productDTOS = modelMapper.map(response.getBody(),ProductDTO.class);
                return CompletableFuture.completedFuture(new ApiResponse<>(true,null,null,productDTOS));
            }
            else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve product: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error during products retrieval: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }
    @Async
    public CompletableFuture<ApiResponse<List<ProductDTO>>> getProductsByPrefix(String prefix) {
        String url = warehouseServiceBaseUrl + "/prefix/" + prefix;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody(),List.class);
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
    public CompletableFuture<ApiResponse<List<ProductDTO>>> getProductsByCategoryId(Long id){
        String url = warehouseServiceBaseUrl + "/category/" + id;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody(),List.class);
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
    public CompletableFuture<ApiResponse<List<ProductDTO>>> getProductByNameContaining(String name){
        String url = warehouseServiceBaseUrl + "/contains/" + name;
        try{
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                List<ProductDTO> productDTOS = modelMapper.map(response.getBody(),List.class);
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
    public CompletableFuture<ApiResponse<ProductDTO>>  updateProductPriceByName(Double price,String name){
        String url = warehouseServiceBaseUrl + "/updatePrice";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Name",name);
        headers.set("Price",price.toString());
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()){
                ProductDTO productDTOS = modelMapper.map(response.getBody(),ProductDTO.class);
                log.info("Product updated");
                return CompletableFuture.completedFuture(new ApiResponse<>(true,null,null,productDTOS));
            }
            else {
                ErrorResponseDTO error = modelMapper.map(response.getBody(), ErrorResponseDTO.class);
                log.warn("Failed to retrieve product: {}", error.getErrorMessage());
                return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
            }
        } catch (HttpClientErrorException ex) {
            ErrorResponseDTO error = modelMapper.map(ex.getResponseBodyAsString(), ErrorResponseDTO.class);
            log.error("HTTP error during products retrieval: {}", error.getErrorMessage());
            return CompletableFuture.completedFuture(new ApiResponse<>(false, error.getErrorMessage(), error.getErrorCode(), null));
        }
    }

}