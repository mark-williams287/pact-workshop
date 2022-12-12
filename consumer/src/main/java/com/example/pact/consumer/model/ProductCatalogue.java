package com.example.pact.consumer.model;

import java.util.Collection;

public record ProductCatalogue(
        String name,
        Collection<Product> products
) {}
