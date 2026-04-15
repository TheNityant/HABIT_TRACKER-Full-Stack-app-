// =============================================================================
// FILE        : ApiClient.java
// DESCRIPTION : RESTful HTTP client for backend communication with retry logic & error handling
// =============================================================================

import java.net.http.*;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class ApiClient {
    
    private static final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(ApiConfig.CONNECT_TIMEOUT_SECONDS))
        .version(HttpClient.Version.HTTP_2)
        .build();
    
    private static String authToken = null;  // Store JWT token when user logs in
    
    /**
     * Set authentication token (called after successful login)
     */
    public static void setAuthToken(String token) {
        authToken = token;
    }
    
    /**
     * Clear authentication token (called on logout)
     */
    public static void clearAuthToken() {
        authToken = null;
    }
    
    /**
     * GET request with retry logic
     */
    public static HttpResponse<String> get(String endpoint) throws ApiException {
        return executeWithRetry("GET", endpoint, null);
    }
    
    /**
     * POST request with JSON body and retry logic
     */
    public static HttpResponse<String> post(String endpoint, String jsonBody) throws ApiException {
        return executeWithRetry("POST", endpoint, jsonBody);
    }
    
    /**
     * PUT request with JSON body and retry logic
     */
    public static HttpResponse<String> put(String endpoint, String jsonBody) throws ApiException {
        return executeWithRetry("PUT", endpoint, jsonBody);
    }
    
    /**
     * DELETE request with retry logic
     */
    public static HttpResponse<String> delete(String endpoint) throws ApiException {
        return executeWithRetry("DELETE", endpoint, null);
    }
    
    /**
     * Execute HTTP request with automatic retry on failure
     */
    private static HttpResponse<String> executeWithRetry(String method, String endpoint, String jsonBody) throws ApiException {
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < ApiConfig.MAX_RETRIES) {
            try {
                return executeRequest(method, endpoint, jsonBody);
            } catch (ApiException e) {
                lastException = e;
                attempt++;
                
                // Only retry for connection errors, not for 4xx client errors
                if (e.getStatusCode() >= 400 && e.getStatusCode() < 500) {
                    throw e;  // Don't retry client errors
                }
                
                if (attempt < ApiConfig.MAX_RETRIES) {
                    try {
                        Thread.sleep(ApiConfig.RETRY_DELAY_MS * attempt);  // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new ApiException("Request interrupted", 0, ie);
                    }
                }
            }
        }
        
        throw new ApiException("Failed after " + ApiConfig.MAX_RETRIES + " attempts", 0, lastException);
    }
    
    /**
     * Execute the actual HTTP request
     */
    private static HttpResponse<String> executeRequest(String method, String endpoint, String jsonBody) throws ApiException {
        try {
            String fullUrl = ApiConfig.BACKEND_URL + endpoint;
            
            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .timeout(Duration.ofSeconds(ApiConfig.READ_TIMEOUT_SECONDS))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");
            
            // Add authentication token if available
            if (authToken != null && !authToken.isEmpty()) {
                requestBuilder.header(ApiConfig.AUTH_HEADER, ApiConfig.BEARER_PREFIX + authToken);
            }
            
            // Set HTTP method and body
            switch (method.toUpperCase()) {
                case "GET":
                    requestBuilder.GET();
                    break;
                case "POST":
                    if (jsonBody != null) {
                        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonBody));
                    } else {
                        requestBuilder.POST(HttpRequest.BodyPublishers.ofString(""));
                    }
                    break;
                case "PUT":
                    if (jsonBody != null) {
                        requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
                    } else {
                        requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(""));
                    }
                    break;
                case "DELETE":
                    requestBuilder.DELETE();
                    break;
                default:
                    throw new ApiException("Unsupported HTTP method: " + method, 0);
            }
            
            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            // Check for HTTP error status codes
            if (response.statusCode() >= 400) {
                throw new ApiException(
                    "HTTP " + response.statusCode() + ": " + response.body(),
                    response.statusCode()
                );
            }
            
            return response;
            
        } catch (HttpConnectTimeoutException e) {
            throw new ApiException("Connection timeout: Backend server may be down", 0, e);
        } catch (HttpTimeoutException e) {
            throw new ApiException("Request timeout: " + e.getMessage(), 0, e);
        } catch (java.net.ConnectException e) {
            throw new ApiException("Connection refused: Cannot reach backend at " + ApiConfig.BACKEND_URL, 0, e);
        } catch (Exception e) {
            throw new ApiException("Request failed: " + e.getMessage(), 0, e);
        }
    }
}
