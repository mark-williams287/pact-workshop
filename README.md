## Pact Consumer (product catalogue service)
**ProductCatalogueController** exposes an endpoint which provides a "catalogue" of products. Calls the **ProductServiceClient**.

**ProductServiceClient** interacts with the provider (product service) to acquire a collection of products, or a single product.

Mocked integration tests, **ProductServiceClientMockBeanIT** and **ProductServiceClientMockServerIT** both show ways of mocking an integration with a provider service.

## Pact Provider (product service)
**ProductController** exposes endpoints which provides either a list of products, or a single product referenced by id.
