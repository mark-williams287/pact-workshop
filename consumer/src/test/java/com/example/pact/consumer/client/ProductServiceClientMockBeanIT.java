package com.example.pact.consumer.client;

import com.example.pact.consumer.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith({SpringExtension.class, MockitoExtension.class})
class ProductServiceClientMockBeanIT {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private ProductServiceClient client;

    @Test
    void fetchProducts() {
        when(restTemplate.exchange(
                "/product",
                HttpMethod.GET,
                new HttpEntity<Void>(new HttpHeaders()),
                new ParameterizedTypeReference<Collection<Product>>() {}
        ))
                .thenReturn(new ResponseEntity<>(
                        List.of(
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
                        ),
                        HttpStatus.OK
                ));

        var response = client.list();
        assertEquals(2, response.size());
        assertEquals(Set.of(9L, 10L), response.stream().map(Product::id).collect(Collectors.toSet()));
    }

    @Test
    void getProductById() {
        var id = 10L;
        when(restTemplate.getForObject("/product/{id}", Product.class, id))
                .thenReturn(new Product(id, "28 Degrees", "CREDIT_CARD", "v1"));

        assertEquals(new Product(id, "28 Degrees", "CREDIT_CARD", "v1"), client.get(id));
    }
}
