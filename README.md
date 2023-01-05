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
  
  private ServerConfig stableRelease;
  private int localPort;
  
  private ApiAssertion assertion;
  
  @BeforeAll
  void setUp() {
    assertion = new ApiAssertionsFactory()
        .comparing(stableRelease, localServer(localPort)) //run api on stable and latest server
        .using(new JunitResponseComparator()) //overrid default comparator by using JunitResponseComparator class
        .build();
  }
  
  @ParameterizedTest(name="{0}") // using JUnit 5
  @MethodSource("cases")
  void test(ApiRequest query) throws Throwable {
      assertion.assertApi(query); // compare results each other
  }
  
  private static Stream<? extends Arguments> cases() throws URISyntaxException {
    return TestCaseProviderBuilder().build()
        .fromRepository(IntegrationTest.class) //supply API TestCases from local ressources
        .map(Arguments::of);
  }
}
```

### Register custom Content comparator

```java
  TestCaseProviderBuilder().build()
      .registerComaparator("EXCEL", ExcelComparator.class) // ExcelComparator must implements ContentComparator 
      .fromRepository(IntegrationTest.class)
      .map(Arguments::of);
```

### Register custom Response Transformer

```java
  TestCaseProviderBuilder().build()
      .registerTransfomer("CAMEL_SNAKE", SnakeCaseTransformer.class) // SnakeCaseTransformer must extends ResponseTransformer
      .fromRepository(IntegrationTest.class)
      .map(Arguments::of);
```



