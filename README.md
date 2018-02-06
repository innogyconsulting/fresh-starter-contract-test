
Spring Boot extensions for convenient testing of API contracts

# Example

```groovy
@RunWith(SpringRunner)
@SpringBootTest(
    classes = UserApiApplication,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class UserApiContractTest {

  @Test
  void 'GET /users/{id} for existent user id: should return 1 element result'() {
    //given
    User user = api.populate(new User(
        name: 'John Smith'
    ))

    given()
        .contentType('application/json')

        .when()
        .get("/users/${user.id}")

        .then()
        .statusCode(200)
        .body(jsonEquals("""
        {
          "name" : "John Smith",
          "_links" : {
            "self" : {
              "href" : "http://localhost:${api.port}/users/${user.id}"
            },
            "user" : {
              "href" : "http://localhost:${api.port}/users/${user.id}"
            }
          }
        }
        """))
  }

  @Inject
  @Rule
  public ApiContext api

}
```

See [UserApiContractTest](src/test/groovy/energy/getfresh/test/contract/UserApiContractTest.groovy)

# Features

* BDD test cases written in source code close to natural language
  * directly executable - no need for extra interpretation layers like [Cucumber](https://cucumber.io/)
  * Groovy syntactic sugar
    * `.class` can be omitted
    * `public` modifier default for class and method declarations
    * `private` modifier default for fields
    * method names of test cases can be specified as a descriptive string
    * default constructors help populating test DB with minimal syntax (code completion in IntelliJ) 
  * API test cases can be shared as common language among all the roles: dev, qa, product, client, etc.
  * All the BDD features of [RestAssured](http://rest-assured.io/) library    
* advantages of full JSON contract assertion over partial JSON path assertions
  * detection of every contract violation in evolving code base (important for Model Driven Architectures)
  * easy code diff - easy to consciously keep the API backward-compatible
  * no abstraction over JSON lowers the cost of maintenance
  * access to all the context variables from withing asserted JSON - e.g. API port in URLs, entity id
  * Informative reporting on violated contracts
    * dump of HTTP request (only on assertion errors)
    * print out of actual and expected JSON
    * differences highlighted as JSON paths  
* minimal dependency on `ApiContext` test rule
  * separate instance of `ApiContext` injected before each test
  * compatible with concurrent test execution  
* automated test lifecycle support
  * full container tests run against the whole HTTP processing stack (unlike MockMvc)
  * Auto-cleanup of populated entities after each test
  * DB population and cleanup does not interfere with transaction spawned by API call
* populating test database state
  * concise definition of data model to be populated before test
  * entities instantiated by the API itself can be tracked and assertions can be specified against them
  
# Usage

Maven dependency:

```xml
  <dependency>
    <groupId>energy.getfresh.test</groupId>
    <artifactId>fresh-starter-contract-test</artifactId>
    <version>${current.version}</version>
  </dependency>
```

You also need your Groovy sources to be compiled and picked by surefire:

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>org.codehaus.gmavenplus</groupId>
        <artifactId>gmavenplus-plugin</artifactId>
        <version>1.6</version>
        <executions>
          <execution>
            <goals>
              <goal>compileTests</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.20.1</version>
        <configuration>
          <failIfNoTests>true</failIfNoTests>
          <includes>
            <include>**/*Test*.*</include><!-- will include also groovy tests -->
          </includes>
        </configuration>
      </plugin>
    </plugins>
  </build>
```

That's it. You can inject `ApiContext` rule from now on in your tests:

```groovy
  @Inject
  @Rule
  public ApiContext api
```
