package org.servlet.error;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.example.exception.MyRuntimeException;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.NoHandlerFoundException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class MyErrorController extends AbstractErrorController {

    private final ErrorProperties errorProperties;

    public MyErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        this(errorAttributes, errorProperties, Collections.emptyList());
    }

    public MyErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
                             List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, errorViewResolvers);
        Assert.notNull(errorProperties, "ErrorProperties must not be null");
        this.errorProperties = errorProperties;
    }

    @RequestMapping
    public ResponseEntity<Map<String, Object>> handler(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        Map<String, Object> body = getErrorAttributes(request, getErrorAttributeOptions(request, MediaType.ALL));

        String requestInfo = String.format("Handled by error controller. Method:%s, RequestURI:%s, QueryString:%s, PathInfo:%s, RemoteAddr:%s",
            request.getMethod(), body.get("path"), request.getQueryString(), request.getPathInfo(), request.getRemoteAddr());

        if (status == HttpStatus.NO_CONTENT || status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
            log.warn(requestInfo);
            return new ResponseEntity<>(status);
        }

        Object ex;
        if (status == HttpStatus.NOT_FOUND) {
            ex = new NoHandlerFoundException(request.getMethod(), String.valueOf(body.get("path")), new HttpHeaders());
        } else {
            ex = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        }
        throw new MyRuntimeException((Throwable) ex);
    }

    protected ErrorAttributeOptions getErrorAttributeOptions(HttpServletRequest request, MediaType mediaType) {
        ErrorAttributeOptions options = ErrorAttributeOptions.defaults();
        if (this.errorProperties.isIncludeException()) {
            options = options.including(ErrorAttributeOptions.Include.EXCEPTION);
        }
        return options;
    }
}
