// Spring configuration for registering the socket authentication filter
package org.msse672.geometryapp.config;

import org.msse672.geometryapp.auth.config.AuthSocketProperties;
import org.msse672.geometryapp.auth.socket.SocketAuthClient;
import org.msse672.geometryapp.auth.socket.SocketAuthFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Registers the SocketAuthFilter with highest precedence in the servlet filter chain.
 * Enables AuthSocketProperties for configuration.
 */
@Configuration
@EnableConfigurationProperties(AuthSocketProperties.class)
public class AuthFilterConfig {

    /**
     * Creates the SocketAuthFilter bean.
     * @param props Configuration properties for socket authentication
     * @param client Socket client for token validation
     * @return SocketAuthFilter instance
     */
    @Bean
    public SocketAuthFilter socketAuthFilter(AuthSocketProperties props, @Qualifier("defaultSocketAuthClient") SocketAuthClient client) {
        return new SocketAuthFilter(props, client);
    }

    /**
     * Registers the SocketAuthFilter with the servlet context.
     * Sets filter order to highest precedence and applies to all URL patterns.
     * The filter itself decides which requests to process.
     * @param filter The SocketAuthFilter bean
     * @return FilterRegistrationBean for SocketAuthFilter
     */
    @Bean
    public FilterRegistrationBean<SocketAuthFilter> socketAuthFilterRegistration(SocketAuthFilter filter) {
        FilterRegistrationBean<SocketAuthFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/*"); // All requests, filter decides applicability
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE); // Run before other filters
        return reg;
    }
}