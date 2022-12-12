package com.example.pact.consumer.client;

import com.example.pact.consumer.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class ProductServiceClientMockServerIT {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductServiceClient client;

    @Test
    void fetchProducts() {
        var downstream = MockRestServiceServer.createServer(restTemplate);
        downstream.expect(
                        once(),
                        requestTo("http://localhost:8181/product")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_JSON)
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
                );

        var response = client.list();
        assertEquals(2, response.size());
        assertEquals(Set.of(9L, 10L), response.stream().map(Product::id).collect(Collectors.toSet()));
    }

    @Test
    void getProductById() {
        var downstream = MockRestServiceServer.createServer(restTemplate);
        downstream.expect(
                        once(),
                        requestTo("http://localhost:8181/product/10")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(
                        withSuccess()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("""
                                        {
                                            "id": 10,
                                            "type": "CREDIT_CARD",
                                            "name": "28 Degrees",
                                            "version": "v1"
                                        }
                                        """
                                )
                );

        assertEquals(new Product(10L, "28 Degrees", "CREDIT_CARD", "v1"), client.get(10));
    }
}
