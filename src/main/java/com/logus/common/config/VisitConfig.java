package com.logus.common.config;

import com.logus.common.filter.VisitFilter;
import com.logus.common.service.VisitLogService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VisitConfig {

    @Bean
    public FilterRegistrationBean<VisitFilter> visitorTrackingFilter(VisitLogService visitorTrackingService) {
        FilterRegistrationBean<VisitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new VisitFilter(visitorTrackingService));

        // 특정 URL 패턴에만 적용
        registrationBean.addUrlPatterns("/blog-info/*");

        return registrationBean;
    }

}
