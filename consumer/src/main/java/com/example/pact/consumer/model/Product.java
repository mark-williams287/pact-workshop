package com.example.pact.consumer.model;

public record Product(
        long id,
        String name,
        String type,
        String version
) {}
