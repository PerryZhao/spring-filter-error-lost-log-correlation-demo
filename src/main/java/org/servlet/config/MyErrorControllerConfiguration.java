package org.servlet.config;

import java.util.stream.Collectors;

import org.example.filter.MyTestFilter;
import org.servlet.error.MyErrorController;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
public class MyErrorControllerConfiguration {

    private final ServerProperties serverProperties;

    public MyErrorControllerConfiguration(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public MyErrorController octopusErrorController(ErrorAttributes errorAttributes,
                                                    ObjectProvider<ErrorViewResolver> errorViewResolvers) {
        return new MyErrorController(errorAttributes, this.serverProperties.getError(),
            errorViewResolvers.orderedStream().collect(Collectors.toList()));
    }

    @Bean
    public FilterRegistrationBean requestRequestProcessingTimeFilter() {
        MyTestFilter myTestFilter = new MyTestFilter();
        final FilterRegistrationBean reg = new FilterRegistrationBean(myTestFilter);
        reg.addUrlPatterns("/*");
        reg.setOrder(300); //defines filter execution order
        return reg;
    }
}