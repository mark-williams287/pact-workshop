package com.example.pact.provider.model;

public record Product(
        long id,
        String name,
        String type,
        String version
) {}
