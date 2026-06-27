package com.hotel.backend_hotel.common.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/graphql")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET","POST","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/graphiql/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST");
    }

}
