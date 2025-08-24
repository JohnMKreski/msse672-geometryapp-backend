package org.msse672.geometryapp.config;

import org.msse672.geometryapp.auth.config.AuthSocketProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Enables binding of AuthSocketProperties from application*.properties.
 */
@Configuration
@EnableConfigurationProperties(AuthSocketProperties.class)
public class AuthConfig { }
