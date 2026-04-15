// API Client - Handles all HTTP requests to the backend
class ApiClient {
  constructor(config) {
    this.baseUrl = config.BASE_URL;
    this.connectTimeout = config.CONNECT_TIMEOUT;
    this.readTimeout = config.READ_TIMEOUT;
    this.maxRetries = config.MAX_RETRIES;
    this.retryDelay = config.RETRY_DELAY;
  }

  /**
   * Make an HTTP request with retry logic
   */
  async request(method, endpoint, data = null, options = {}) {
    const url = this.baseUrl + endpoint;
    let lastError = null;
    let attempt = 0;

    while (attempt < this.maxRetries) {
      try {
        attempt++;
        const fetchOptions = {
          method: method,
          headers: {
            'Content-Type': 'application/json',
            ...options.headers,
          },
        };

        // Add auth token if available
        const token = localStorage.getItem(ApiConfig.STORAGE_KEYS.AUTH_TOKEN);
        if (token) {
          fetchOptions.headers['Authorization'] = `Bearer ${token}`;
        }

        // Add body for non-GET requests
        if (data && method !== 'GET') {
          fetchOptions.body = JSON.stringify(data);
        }

        // Create abort controller for timeout
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), this.readTimeout);
        fetchOptions.signal = controller.signal;

        // Make request
        const response = await fetch(url, fetchOptions);
        clearTimeout(timeoutId);

        // Handle response
        let responseData = null;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
          responseData = await response.json();
        } else {
          responseData = await response.text();
        }

        if (!response.ok) {
          throw new ApiException(
            responseData?.message || `HTTP ${response.status}`,
            response.status,
            responseData
          );
        }

        return responseData;
      } catch (error) {
        lastError = error;

        // If it's an ApiException or 4xx error, don't retry
        if (error instanceof ApiException && error.statusCode >= 400 && error.statusCode < 500) {
          throw error;
        }

        // For other errors, retry with exponential backoff
        if (attempt < this.maxRetries) {
          const delayMs = this.retryDelay * Math.pow(2, attempt - 1);
          await new Promise(resolve => setTimeout(resolve, delayMs));
        }
      }
    }

    // All retries exhausted
    if (lastError instanceof ApiException) {
      throw lastError;
    }
    throw new ApiException(lastError.message || 'Network error', 0, lastError);
  }

  // HTTP Methods
  async get(endpoint) {
    return this.request('GET', endpoint);
  }

  async post(endpoint, data) {
    return this.request('POST', endpoint, data);
  }

  async put(endpoint, data) {
    return this.request('PUT', endpoint, data);
  }

  async delete(endpoint) {
    return this.request('DELETE', endpoint);
  }
}

// Custom Exception class
class ApiException extends Error {
  constructor(message, statusCode = 0, responseData = null) {
    super(message);
    this.name = 'ApiException';
    this.statusCode = statusCode;
    this.responseData = responseData;
  }

  getStatusCode() {
    return this.statusCode;
  }
}

// Create global instance
const apiClient = new ApiClient(ApiConfig);
