package co.com.testapp.testapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${tokenization.api.key:tk_live_secure_tokenization_key_2025}")
    private String tokenizationApiKey;

    @Value("${customer.api.key:cs_live_secure_customer_key_2025}")
    private String customerApiKey;

    @Value("${products.api.key:pd_live_secure_products_key_2025}")
    private String productsApiKey;

    @Value("${orders.api.key:or_live_secure_orders_key_2025}")
    private String ordersApiKey;

    @Value("${payments.api.key:py_live_secure_payments_key_2025}")
    private String paymentsApiKey;

    @Value("${audit.api.key:py_live_secure_audit_key_2025}")
    private String auditApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip authentication for ping endpoint
        if (requestPath.equals("/ping")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null && isValidApiKey(apiKey, requestPath)) {
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken("api-user", null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidApiKey(String apiKey, String requestPath) {
        if (requestPath.startsWith("/api/v1/tokenization")) {
            return tokenizationApiKey.equals(apiKey);
        } else if (requestPath.startsWith("/api/v1/customers")) {
            return customerApiKey.equals(apiKey);
        } else if (requestPath.startsWith("/api/v1/products")) {
            return productsApiKey.equals(apiKey);
        } else if (requestPath.startsWith("/api/v1/orders")) {
            return ordersApiKey.equals(apiKey);
        } else if (requestPath.startsWith("/api/v1/payments")) {
            return paymentsApiKey.equals(apiKey);
        } else if (requestPath.startsWith("/api/v1/audit")) {
            return auditApiKey.equals(apiKey);
        }
        return false;
    }

}

