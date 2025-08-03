package me.statuxia.shulkerapi.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpHandler {

    private final RestTemplate restTemplate;

    @Autowired
    public HttpHandler(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> HttpResponse<T> get(String uri, HttpEntity<?> entity, Class<T> responseClass) {
        return exchange(uri, HttpMethod.GET, entity, responseClass);
    }

    public <T> HttpResponse<T> post(String uri, HttpEntity<?> entity, Class<T> responseClass) {
        return exchange(uri, HttpMethod.POST, entity, responseClass);
    }

    public <T> HttpResponse<T> put(String uri, HttpEntity<?> entity, Class<T> responseClass) {
        return exchange(uri, HttpMethod.PUT, entity, responseClass);
    }

    public <T> HttpResponse<T> delete(String uri, HttpEntity<?> entity, Class<T> responseClass) {
        return exchange(uri, HttpMethod.DELETE, entity, responseClass);
    }

    public <T> HttpResponse<T> exchange(String uri, HttpMethod method, HttpEntity<?> entity, Class<T> responseClass) {
        try {
            final ResponseEntity<T> responseEntity = restTemplate.exchange(uri, method, entity, responseClass);
            return new HttpResponse<>(responseEntity.getBody(), responseEntity.getStatusCode());
        } catch (Exception e) {
            return new HttpResponse<>(e);
        }
    }

    public static class HttpResponse<T> {
        @Nullable
        protected final T body;
        @Nullable
        protected final HttpStatusCode code;
        @Nullable
        protected final Throwable exception;

        public HttpResponse(@Nullable T body, @Nullable HttpStatusCode code) {
            this.body = body;
            this.code = code;
            this.exception = null;
        }

        public HttpResponse(@Nullable Throwable exception) {
            this.body = null;
            this.code = null;
            this.exception = exception;
        }

        @Nullable
        public T getBody() {
            return body;
        }

        @Nullable
        public HttpStatusCode getCode() {
            return code;
        }

        @Nullable
        public Throwable getException() {
            return exception;
        }
    }
}
