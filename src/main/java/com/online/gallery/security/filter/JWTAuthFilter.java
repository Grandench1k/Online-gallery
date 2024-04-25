package com.online.gallery.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;

public interface JWTAuthFilter extends Filter {
    void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException;

    @Override
    default void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    void doFilter(ServletRequest servletRequest,
                  ServletResponse servletResponse,
                  FilterChain filterChain) throws IOException, ServletException;

    @Override
    default void destroy() {
        Filter.super.destroy();
    }
}
