package com.example.pact.consumer.controller;

import com.example.pact.consumer.client.ProductServiceClient;
import com.example.pact.consumer.model.ProductCatalogue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductCatalogueController {

    private final ProductServiceClient client;

    @Autowired
    public ProductCatalogueController(final ProductServiceClient client) {
        this.client = client;
    }

    @GetMapping("/catalogue")
    public ProductCatalogue catalogue() {
        return new ProductCatalogue("Default Catalogue", client.list());
    }
}
