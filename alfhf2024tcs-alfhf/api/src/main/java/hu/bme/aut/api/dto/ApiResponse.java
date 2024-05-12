package hu.bme.aut.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic response wrapper for API responses.
 * @param <T> the type of the data in the response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String errorMessage;
    private String errorCode;
    private T data;
}