package kim.crossorigin.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

/**
 * 两种方法任选其一
 */
@Configuration(proxyBeanMethods = false)
public class CorsConfig {

    /**
     * @see CorsConfiguration#applyPermitDefaultValues()
     * <ul>
     * <li>Allow all origins.</li>
     * <li>Allow "simple" methods {@code GET}, {@code HEAD} and {@code POST}.</li>
     * <li>Allow all headers.</li>
     * <li>Set max age to 1800 seconds (30 minutes).</li>
     * </ul>
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();
        config.setAllowedMethods(Collections.unmodifiableList(Arrays.asList("*")));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * Same as following
     *
     * @see WebConfig#addCorsMappings(CorsRegistry)
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowedMethods("GET", "HEAD", "POST")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

}
