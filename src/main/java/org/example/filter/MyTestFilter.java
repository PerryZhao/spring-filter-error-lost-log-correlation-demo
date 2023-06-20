package org.example.filter;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

@Slf4j
public class MyTestFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("My filter");
        throw new IllegalArgumentException("Filter error.");
    }

}
