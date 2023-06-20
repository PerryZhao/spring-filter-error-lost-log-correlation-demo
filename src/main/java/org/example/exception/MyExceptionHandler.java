package org.example.exception;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.filter.ServerHttpObservationFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class MyExceptionHandler {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    private static final String CURRENT_OBSERVATION_ATTRIBUTE = ServerHttpObservationFilter.class.getName() + ".observation";

    @Autowired
    private ObservationRegistry observationRegistry;

    private Observation createOrFetchObservation(HttpServletRequest request, HttpServletResponse response) {
        Observation observation = (Observation) request.getAttribute(CURRENT_OBSERVATION_ATTRIBUTE);
        if (null == observation) {
            ServerRequestObservationContext context = ServerHttpObservationFilter.findObservationContext(request)
                .orElseGet(() -> new ServerRequestObservationContext(request, response));
            observation = Observation.createNotStarted("MessageSourceExceptionHandler", () -> context, observationRegistry).start();
        }
        return observation;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<String> handler(Throwable ex, HttpServletRequest request, HttpServletResponse response) {
        log.warn("Exception occurred.");

        if (atomicInteger.getAndAdd(1) % 2 == 0) {
            return new ResponseEntity<String>("My Test", HttpStatus.BAD_REQUEST);
        }

        // TODO:: What happens if openScope after observation stopped?
        // TODO:: If I want to get the traceId here, what should I do? Is the following code correct?
        Observation observation = createOrFetchObservation(request, response);
        return Observation.tryScoped(observation, () -> {
            log.warn("Scoped.");
            return new ResponseEntity<String>("Open Scope.", HttpStatus.BAD_REQUEST);
        });
    }
}
