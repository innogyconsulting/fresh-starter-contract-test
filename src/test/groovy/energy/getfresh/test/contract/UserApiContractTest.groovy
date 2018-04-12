/*
 * Copyright 2018 https://getfresh.energy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package energy.getfresh.test.contract

import energy.getfresh.test.contract.testapp.User
import energy.getfresh.test.contract.testapp.UserApiApplication
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
 * API contract test for test application exposing {@link User}
 * as REST resource.
 *
 * @author morisil
 */
@RunWith(SpringRunner)
@SpringBootTest(
    classes = UserApiApplication,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class UserApiContractTest {

  @Test
  void 'POST /users with valid user data: should write user in DB'() {
    given()
        .contentType('application/json')
        .body('''
        {
          "name": "John Smith"
        }
        ''')

        .when()
        .post('/users')

        .then()
        .statusCode(201) // created
        .body(api.trackPersistedEntity(User))
        .body(jsonEquals("""
        {
          "name" : "John Smith",
          "_links" : {
            "self" : {
              "href" : "http://localhost:${api.port}/users/${api.persistedEntity.id}"
            },
            "user" : {
              "href" : "http://localhost:${api.port}/users/${api.persistedEntity.id}"
            }
          }
        }
        """))

    // then
    User user = api.getPersistedEntity()
    assertThat(user).isNotNull()
    assertThat(user.name).isEqualTo('John Smith')
  }

  @Test
  void 'POST /users with invalid JSON: should return error'() {
    given()
        .contentType('application/json')
        .body('''
        {
          "name": "Mistake
        }
        ''')

        .when()
        .post('/users')

        .then()
        .statusCode(400)
  }

  @Test
  void 'GET /users for non-existent users: should return empty result'() {
    given()
        .contentType('application/json')

        .when()
        .get('/users')

        .then()
        .statusCode(200)
        .body(jsonEquals("""
        {
          "_embedded" : {
            "users" : [ ]
          },
          "_links" : {
            "self" : {
              "href" : "http://localhost:${api.port}/users{?page,size,sort}",
              "templated" : true
            },
            "profile" : {
              "href" : "http://localhost:${api.port}/profile/users"
            }
          },
          "page" : {
            "size" : 20,
            "totalElements" : 0,
            "totalPages" : 0,
            "number" : 0
          }
        }
        """))
  }

  @Test
  void 'GET /users for 2 existent users: should return 2 element result'() {
    //given
    User user1 = api.populate(new User(
        name: 'John Smith'
    ))
    User user2 = api.populate(new User(
        name: 'Jan Musterman'
    ))

    given()
        .contentType('application/json')

        .when()
        .get('/users')

        .then()
        .statusCode(200)
        .body(jsonEquals("""
        {
          "_embedded" : {
            "users" : [ {
              "name" : "John Smith",
              "_links" : {
                "self" : {
                  "href" : "http://localhost:${api.port}/users/${user1.id}"
                },
                "user" : {
                  "href" : "http://localhost:${api.port}/users/${user1.id}"
                }
              }
            },
            {
              "name" : "Jan Musterman",
              "_links" : {
                "self" : {
                  "href" : "http://localhost:${api.port}/users/${user2.id}"
                },
                "user" : {
                  "href" : "http://localhost:${api.port}/users/${user2.id}"
                }
              }
            } ]
          },
          "_links" : {
            "self" : {
              "href" : "http://localhost:${api.port}/users{?page,size,sort}",
              "templated" : true
            },
            "profile" : {
              "href" : "http://localhost:${api.port}/profile/users"
            }
          },
          "page" : {
            "size" : 20,
            "totalElements" : 2,
            "totalPages" : 1,
            "number" : 0
          }
        }
        """))
  }

  @Test
  void 'GET /users for 2 existent users and 1 element per page: should return 1 element with link to the next page'() {
    //given
    User user1 = api.populate(new User(
        name: 'John Smith'
    ))
    api.populate(new User(
        name: 'Jan Musterman'
    ))

    given()
        .contentType('application/json')

        .when()
        .get('/users?size=1')

        .then()
        .statusCode(200)
        .body(jsonEquals("""
        {
          "_embedded" : {
            "users" : [ {
              "name" : "John Smith",
              "_links" : {
                "self" : {
                  "href" : "http://localhost:${api.port}/users/${user1.id}"
                },
                "user" : {
                  "href" : "http://localhost:${api.port}/users/${user1.id}"
                }
              }
            } ]
          },
          "_links" : {
            "first" : {
              "href" : "http://localhost:${api.port}/users?page=0&size=1"
            },
            "self" : {
              "href" : "http://localhost:${api.port}/users{&sort}",
              "templated" : true
            },
            "next" : {
              "href" : "http://localhost:${api.port}/users?page=1&size=1"
            },
            "last" : {
              "href" : "http://localhost:${api.port}/users?page=1&size=1"
            },
            "profile" : {
              "href" : "http://localhost:${api.port}/profile/users"
            }
          },
          "page" : {
            "size" : 1,
            "totalElements" : 2,
            "totalPages" : 2,
            "number" : 0
          }
        }
        """))
  }

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

  @Test
  void 'GET /users/{id} for non-existent user id: should return 404'() {
    given()
        .contentType('application/json')

        .when()
        .get('/users/666')

        .then()
        .statusCode(404)
  }

  @Test
  void 'DELETE /users/{id} for existent user id: should delete'() {
    //given
    User user = api.populate(new User(
        name: 'John Smith'
    ))

    given()
        .contentType('application/json')

        .when()
        .delete("/users/${user.id}")

        .then()
        .statusCode(204)
  }

  @Test
  void 'DELETE /users/{id} for non-existent user id: should return 404'() {
    given()
        .contentType('application/json')

        .when()
        .delete('/users/666')

        .then()
        .statusCode(404)
  }

  @Inject
  @Rule
  public ApiContext api

}
