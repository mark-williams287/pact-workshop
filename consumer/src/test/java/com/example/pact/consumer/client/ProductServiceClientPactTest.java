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
                .given("products exist")
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

    @Pact(consumer = "ProductCatalogueService", provider = "ProductService")
    V4Pact noProducts(PactDslWithProvider builder) {
        return builder
                .given("no products exist")
                .uponReceiving("list all products given no products exist")
                .path("/product")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .body("[]")
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "noProducts")
    void testNoProducts() {
        var response = client.list();

        assertNotNull(response);
        assertEquals(0, response.size());
    }

    @Pact(consumer = "ProductCatalogueService", provider = "ProductService")
    V4Pact singleProduct(PactDslWithProvider builder) {
        return builder
                .given("product with id 10 exists")
                .uponReceiving("get product with id 10 given it exists")
                .path("/product/10")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(Map.of(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .body(
                        new PactDslJsonBody()
                                .integerType("id", 10L)
                                .stringType("type", "CREDIT_CARD")
                                .stringType("name", "28 Degrees")
                                .stringType("version", "v1")
                )
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "singleProduct")
    void testSingleProduct() {
        var id = 10L;

        assertEquals(
                new Product(id, "28 Degrees", "CREDIT_CARD", "v1"),
                client.get(id)
        );
    }

    @Pact(consumer = "ProductCatalogueService", provider = "ProductService")
    V4Pact singleProductNotExists(PactDslWithProvider builder) {
        return builder
                .given("product with id 10 does not exist")
                .uponReceiving("get product with id 10 given it does not exist")
                .path("/product/10")
                .method("GET")
                .willRespondWith()
                .status(404)
                .toPact(V4Pact.class);
    }

    @Test
    @PactTestFor(pactMethod = "singleProductNotExists")
    void testSingleProductNotExists() {
        var ex = assertThrows(HttpClientErrorException.class, () -> client.get(10L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
