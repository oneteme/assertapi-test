# Api-Test

Integration pom.xml

```
<dependency>
  <groupId>fr.enedis.teme.api</groupId>
  <artifactId>api-test</artifactId>
  <version>${version}</version>
  <scope>test</scope>
</dependency>
```

Test class

```
class IntegrationTest {
  
  private ServerConfig dist;

  private RestTemplate exTemp;
  private RestTemplate acTemp;
  
  @BeforeAll
  void setUp() {
    exTemp = build(dist);
    acTemp = build(localServer(localPort));
  }
    
  @ParameterizedTest(name="{0}")
  @MethodSource("cases")
  void test(HttpQuery query) throws Exception {
      
      assertResponseEquals(query, exTemp, acTemp);
  }
  
  private static final Stream<? extends Arguments> cases() throws IOException, URISyntaxException {

    return resouces(IntegrationTest.class.getResource(".").toURI(), ".+\\.json")
        .map(Arguments::of);
  }
}

```