# Cross-origin resource sharing (CORS) 
参考文档： [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-cors)

[Cross-origin resource sharing](https://en.wikipedia.org/wiki/Cross-origin_resource_sharing) (CORS) is a [W3C specification](https://www.w3.org/TR/cors/) implemented by [most browsers](https://caniuse.com/#feat=cors) that lets you specify in a flexible way what kind of cross-domain requests are authorized., instead of using some less secure and less powerful approaches such as IFRAME or JSONP.

As of version 4.2, Spring MVC supports CORS. Using controller method CORS configuration with @CrossOrigin annotations in your Spring Boot application does not require any specific configuration. Global CORS configuration can be defined by registering a WebMvcConfigurer bean with a customized addCorsMappings(CorsRegistry) method, as shown in the following example:

```java
@Configuration(proxyBeanMethods = false)
public class MyConfiguration {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**");
            }
        };
    }
}
```

# 配置Cross-Origin的几种方法

参考文档：[Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-cors-controller)

## 1 注解法 @CrossOrigin 
- 可在类级别使用，方法级别可以继承（覆盖）
- 默认值
    - Allow all origins.
    - Allow all headers.
    - All HTTP methods to which the controller method is mapped.
    - allowedCredentials is not enabled by default
    - Set max age to 1800 seconds (30 minutes).
- 用于细粒度的控制
```java
@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/account")
public class AccountController {

    @CrossOrigin("https://domain2.com")
    @GetMapping("/{id}")
    public Account retrieve(@PathVariable Long id) {
        // ...
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        // ...
    }
}
```
##  2 全局配置法 (Global Configuration)
- 默认值，详见 `org.springframework.web.cors.CorsConfiguration#applyPermitDefaultValues()`
    - Allow all origins.
    - Allow all headers.
    - GET, HEAD, and POST methods.（注意：这个和注解不一样）
    - allowedCredentials is not enabled by default
    - Set max age to 1800 seconds (30 minutes).
- 一般放到  MVC Java configuration（WebMvcConfigurer的实现类）中
```java
@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("GET", "HEAD", "POST")
                .allowCredentials(false)
                .maxAge(3600);

        // Add more mappings...
    }
}
```

- 搞个单独的Configuration也是可以的

```java
@Configuration(proxyBeanMethods = false)
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedHeaders("*")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

}
```

##  3 过滤器法(CorsFilter)
###  3.1 没有使用 Spring security 的情况下

```java
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
        config.applyPermitDefaultValues(); // 注意这个简便用法
        config.setAllowedMethods(Collections.unmodifiableList(Arrays.asList("*")));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
```

###  3.2  Spring security 内置了对 CorsFilter 的支持
 [Spring Security] (https://docs.spring.io/spring-security/site/docs/current/reference/html5/#cors)

 ```java
 @EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            // by default uses a Bean by the name of corsConfigurationSource
            .cors(withDefaults())
            //...
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.applyPermitDefaultValues();
        config.setAllowedMethods(Collections.unmodifiableList(Arrays.asList("*")));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
 ```

注意一点，不要重复指定：If you are using Spring MVC’s CORS support, you can omit specifying the CorsConfigurationSource and Spring Security will leverage the CORS configuration provided to Spring MVC.