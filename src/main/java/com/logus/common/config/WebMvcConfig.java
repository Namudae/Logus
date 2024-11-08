package com.logus.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.logus.common.controller.HtmlCharacterEscapes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                //.allowCredentials(true) //주석처리함
                .allowedOriginPatterns("*") //추가한 부분
                .exposedHeaders("*");
    }


    // 이스케이프 1
//    private final ObjectMapper objectMapper;
//
//    @Bean
//    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
//        ObjectMapper copy = objectMapper.copy();
//        copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
//        return new MappingJackson2HttpMessageConverter(copy);
//    }

    //이스케이프 2 (implements WebMvcConfigurer)
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        converters.add(escapingConverter());
//    }
//
//    @Bean
//    public HttpMessageConverter escapingConverter() {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
//
//        // JavaTimeModule 등록 및 설정 추가
//        objectMapper.registerModule(new JavaTimeModule());
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        MappingJackson2HttpMessageConverter escapingConverter = new MappingJackson2HttpMessageConverter();
//        escapingConverter.setObjectMapper(objectMapper);
//
//        return escapingConverter;
//    }

}
