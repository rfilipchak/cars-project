package com.playtika.cars_proposition_project.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class CarPropositionServiceExceptionHandler {

    @Autowired
    private ObjectMapper mapper;

    @ExceptionHandler({HttpClientErrorException.class})
    @ResponseBody
    public ErrorResponse handleRemoteRequest(HttpClientErrorException e) throws Exception {
        return mapper.readValue(e.getResponseBodyAsString(), ErrorResponse.class);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private int code;
        private String message;
        private String description;
    }
}
