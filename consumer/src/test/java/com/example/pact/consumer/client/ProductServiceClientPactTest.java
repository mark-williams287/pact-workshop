package com.example.pact.consumer.client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.example.pact.consumer.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "ProductService", pactVersion = PactSpecVersion.V4)
class ProductServiceClientPactTest {

    @Autowired
    private ProductServiceClient client;

    @BeforeEach
    void beforeEach(MockServer mockServer) {
        var restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(mockServer.getUrl()));
        ReflectionTestUtils.setField(client, "restTemplate", restTemplate);
    }

    @Pact(consumer = "ProductCatalogueService", provider = "ProductService")
    V4Pact allProducts(PactDslWithProvider builder) {
        return builder
                .uponReceiving("list all products")
                .path("/product")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .body("""
                        [
                            {
                                "id": 9,
                                "type": "CREDIT_CARD",
                                "name": "GEM Visa",
                                "version": "v2"
                            },
                            {
                                "id": 10,
                                "type": "CREDIT_CARD",
                                "name": "28 Degrees",
                                "version": "v1"
                            }
                        ]
                        """)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "allProducts")
    void testAllProducts() {
        var response = client.list();

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(Set.of(9L, 10L), response.stream().map(Product::id).collect(Collectors.toSet()));
    }
}
