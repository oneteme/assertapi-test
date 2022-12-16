# Api-Test

Integration pom.xml

```
<dependency>
  <groupId>org.usf.assertapi</groupId>
  <artifactId>assertapi-test</artifactId>
  <version>${version}</version>
  <scope>test</scope>
</dependency>
```

Test class

```
class IntegrationTest {
  
  private ServerConfig dist;
  private int localPort;
  
  private ApiAssertions assertions;
  
  @BeforeAll
  void setUp() {
    assertions = new ApiAssertionsFactory()
        .comparing(dist, localServer(localPort, dist.getAuth())) // same auth.
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