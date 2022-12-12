package com.example.pact.provider.controller;

import com.example.pact.provider.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
public class ProductController {

    private Collection<Product> products = List.of(
            new Product(
                    9L,
                    "GEM Visa",
                    "CREDIT_CARD",
                    "v2"
            ),
            new Product(
                    10L,
                    "28 Degrees",
                    "CREDIT_CARD",
                    "v1"
            )
    );

    @GetMapping("/product")
    public ResponseEntity<Collection<Product>> list() {
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> list(@PathVariable("id") Long id) {
        return products.stream()
                .filter(x -> id == x.id())
                .findFirst()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
