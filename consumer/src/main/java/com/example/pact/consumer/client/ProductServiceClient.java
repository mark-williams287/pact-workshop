package com.example.pact.consumer.client;

import com.example.pact.consumer.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

@Component
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Autowired
    public ProductServiceClient(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Collection<Product> list() {
        return restTemplate.exchange(
                "/product",
                HttpMethod.GET,
                new HttpEntity<Void>(new HttpHeaders()),
                new ParameterizedTypeReference<Collection<Product>>() {}
        ).getBody();
    }

    public Product get(final long id) {
        return restTemplate.getForObject("/product/{id}", Product.class, id);
    }
}
