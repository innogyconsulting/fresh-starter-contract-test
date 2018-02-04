# fresh-starter-contract-test
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

For more examples see ......

# Features

* BDD style with source code following natural language
* API test cases can be shared as common language between all the roles in the team (dev, qa, product)
* Test case description in natural language
* full container test instead of mock mvc
* full JSON in assertions
  * access to all the variables
* All the features of RestAssured TODO link library
* Easy population of DB state before the test thanks to groovy default constructors
* Auto-cleanup of populated entities after each test
* DB population and cleanup does not interfere with transaction spawned by API call
* minimal dependency on ApiContext test rule
  * separate instance of ApiContext injected before each test, compatible with concurrent test execution
* Informative error messages
  * HTTP request/response trace only on error
  * JSON 