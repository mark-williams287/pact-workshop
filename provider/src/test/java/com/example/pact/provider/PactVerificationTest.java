package com.example.pact.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.pact.provider.controller.ProductController;
import com.example.pact.provider.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("ProductService")
@PactFolder("pacts")
class PactVerificationTest {

    @Autowired
    private ProductController controller;

    @LocalServerPort
    private int port;

    @BeforeEach
    void beforeEach(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State(value = "products exist", action = StateChangeAction.SETUP)
    void productsExist() {
        ReflectionTestUtils.setField(
                controller,
                "products",
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
                )
        );
    }

    @State(value = "no products exist", action = StateChangeAction.SETUP)
    void noProductsExist() {
        ReflectionTestUtils.setField(
                controller,
                "products",
                Collections.emptyList()
        );
    }

    @State(value = "product with id 10 exists", action = StateChangeAction.SETUP)
    void product10Exists() {
        ReflectionTestUtils.setField(
                controller,
                "products",
                Collections.singletonList(
                        new Product(
                                10L,
                                "28 Degrees",
                                "CREDIT_CARD",
                                "v1"
                        )
                )
        );
    }

    @State(value = "product with id 10 does not exist", action = StateChangeAction.SETUP)
    void product10DoesNotExist() {
        ReflectionTestUtils.setField(
                controller,
                "products",
                Collections.emptyList()
        );
    }
}
