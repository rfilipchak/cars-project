package com.playtika.carshop.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class CarShopErrorDecoder implements ErrorDecoder {

    private ErrorDecoder delegate = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpHeaders responseHeaders = new HttpHeaders();
        response.headers().entrySet().stream()
                .forEach(entry -> responseHeaders.put(entry.getKey(), new ArrayList<>(entry.getValue())));

        HttpStatus statusCode = HttpStatus.valueOf(response.status());
        String statusText = response.reason();

        byte[] responseBody;
        try {
            responseBody = IOUtils.toByteArray(response.body().asInputStream());
        } catch (IOException e) {
            throw new RuntimeException("Failed to process response body.", e);
        }

        if (response.status() >= 400 && response.status() <= 499) {
            return new HttpClientErrorException(statusCode, statusText, responseHeaders, responseBody, Charset.defaultCharset());
        }

        if (response.status() >= 500 && response.status() <= 599) {
            return new HttpServerErrorException(statusCode, statusText, responseHeaders, responseBody, Charset.defaultCharset());
        }
        return delegate.decode(methodKey, response);
    }
}

