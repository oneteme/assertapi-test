# assertapi-test
...
## Status

[![Java CI with Maven](https://github.com/oneteme/assertapi-test/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/oneteme/assertapi-test/actions/workflows/maven-publish.yml)

## MAVEN Integration

```xml
<dependency>
  <groupId>io.github.oneteme.assertapi</groupId>
  <artifactId>assertapi-test</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Usage

```java
class IntegrationTest {
  
  private ServerConfig dist;
  private int localPort;
  
  private ApiAssertions assertions;
  
  @BeforeAll
  void setUp() {
    assertion = new ApiAssertionsFactory()
        .comparing(dist, localServer(localPort, dist.getAuth())) //same auth.
        .using(new JunitResponseComparator())
        .build();
  }
    
  
  @ParameterizedTest(name="{0}")
  @MethodSource("cases")
  void test(ApiRequest query) throws Throwable {
      assertions.assertApi(query);
  }
  
  private static Stream<? extends Arguments> cases() throws URISyntaxException {
    return fromRepository("${test-repo-provider}").map(Arguments::of);
  }
}

```
