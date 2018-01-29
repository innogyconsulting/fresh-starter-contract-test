package energy.getfresh.test.contract

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import javax.inject.Inject

import static energy.getfresh.test.contract.JsonMatcher.jsonEquals
import static io.restassured.RestAssured.given
import static org.assertj.core.api.Assertions.assertThat

/**
 *
 *
 * @author morisil
 */
@RunWith(SpringRunner)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = FooApp
)
class FooApiContractTest {

  @Inject @Rule public TestContext context

  @Inject FooRepository fooRepository

  @Test
  void 'POST /customers with valid customer data: should write customer in DB'() {
    String customerLink = given()
        .contentType('application/json')
        .body('''
        {
          "name": "John",
          "surname": "Smith",
          "birthDate": "1981-12-18T00:00"
        }
        ''')

        .when()
        .post('/customers')

        .then()
        .statusCode(201) // created
        .extract()
        .path('_links.self.href')

    // then
    Long customerId = BelcantoTests.extractId(customerLink)
    assertThat(fooRepository.findById(customerId))
        .isPresent()
        .hasValueSatisfying { customer ->
      assertThat(customer.name).isEqualTo('John')
      assertThat(customer.surname).isEqualTo('Smith')
      assertThat(customer.birthDate).isEqualTo('1981-12-18')
    }
  }

  @Test
  void 'POST /customers with embedded address: should save customer and address in db'() {
    String customerLink = given()
        .contentType('application/json')
        .body('''
        {
          "name": "John",
          "surname": "Smith",
          "addresses": [
            {
              "street": "Somewhere"
            }            
          ]
        }
        ''')

        .when()
        .post('/customers')

        .then()
        .statusCode(201) // created
        .extract()
        .path('_links.self.href')

    // then
    Long customerId = BelcantoTests.extractId(customerLink)
    assertThat(customerRepository.findById(customerId))
        .isPresent()
        .hasValueSatisfying { customer ->
      assertThat(customer.name).isEqualTo('John')
      assertThat(customer.surname).isEqualTo('Smith')
      assertThat(customer.addresses)
          .hasSize(1)
          .hasOnlyOneElementSatisfying { address ->
        assertThat(address.street).isEqualTo('Somewhere')
      }
    }
  }

  @Test
  void 'GET /foo/{id} for non-existent id: should return 404'() {
    given()
        .pathParam('nonExistentId', 42L)

        .when()
        .get('/customers/{nonExistentId}')

        .then()
        .statusCode(404) // not found
  }

  @Test
  void 'GET /foo/id for existing foo: should return foo data'() {
    // given
    // db state
    def foo = context.populate(
        new Foo( // convenient way of populating DB state with Groovy
            bar: 'buzz'
        )
    )
    given()

        .when()
        .get("/customers/${customer.id}")

        .then()
        .statusCode(200)
        .content(jsonEquals("""
        {
          "name" : "John",
          "surname" : "Smith",
          "addresses" : [ ],
          "appointments" : [ ],
          "birthDate" : null,
          "displayName" : "John Smith",
          "_links" : {
            "self" : {
              "href" : "http://localhost:${context.port}/customers/${customer.id}"
            },
            "customer" : {
              "href" : "http://localhost:${context.port}/customers/${customer.id}"
            },
            "specialist" : {
              "href" : "http://localhost:${context.port}/customers/${customer.id}/specialist"
            }
          }
        }
        """))
  }

}
