### Verifying via the pact broker
Bring up pact broker from root directory:
```
docker-compose build && docker-compose up -d
```

Run provider verification tests while pact broker container is running:
```
mvn verify \
    -Dpact.verifier.publishResults=true \
    -Dpact.provider.version=1.0-SNAPSHOT \
    -Dpactbroker.host=localhost \
    -Dpactbroker.port=9292 \
    -Dpactbroker.auth.username=pact-workshop \
    -Dpactbroker.auth.password=pact-workshop
```
