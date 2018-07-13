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

package energy.getfresh.test.contract;

import energy.getfresh.test.contract.db.DbCleaner;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Rule;
import org.junit.rules.ExternalResource;
import org.junit.runners.model.Statement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.regex.Pattern;

/**
 * JUnit {@link Rule} managing context of each API contract test case separately.
 *
 * @author morisil
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // prototype will ensure that we are always injecting a new instance
public class ApiContext extends ExternalResource {

  private RequestSpecification requestSpecification;

  private final int port;

  private final DbCleaner dbCleaner;

  @Inject
  public ApiContext(
      @Value("${local.server.port}") int port,
      RestAssuredInitializer initializer, // not used, just ensures dependency order
      DbCleaner dbCleaner
  ) {
    this.port = port;
    this.dbCleaner = dbCleaner;
  }

  public int getPort() {
    return port;
  }

  public <T> T populate(T entity) {
    testEntities.persist(entity);
    return entity;
  }

  public Matcher<String> trackPersistedEntity(Class<?> entityClass) {
    return new TypeSafeDiagnosingMatcher<String>() {
      @Override
      protected boolean matchesSafely(String item, Description mismatchDescription) {
        String link = JsonPath.with(item).getString("_links.self.href");
        String id = link.substring(link.lastIndexOf('/') + 1);
        testEntities.trackExistingEntity(entityClass, id);
        return true;
      }
      @Override
      public void describeTo(Description description) { /* never used */ }
    };
  }

  @Override
  protected void after() {
    dbCleaner.truncateAllTables();
  }



  @Inject
  public ApiTester(
      @Value("${fresh.contractTest.apiUrlBase}") String apiUrlBase,
      DbCleaner dbCleaner) {

    this.apiUrlBase = apiUrlBase;
    this.dbCleaner = dbCleaner;
  }

  @Override
  public Statement apply(Statement base, org.junit.runner.Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          doEvaluate(base, description);
        } finally {
          dbCleaner.truncateAllData();
        }
      }
    };
  }

  private void doEvaluate(Statement base, org.junit.runner.Description description) throws Throwable {
    JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    OperationPreprocessor apiUrlReplacer = replacePattern(LOCAL_HOST_PATTERN, apiUrlBase);
    requestSpecification = new RequestSpecBuilder().addFilter(
        RestAssuredRestDocumentation
            .documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(
                apiUrlReplacer,
                // TODO "Host" header was not removed
                removeHeaders("Host", "Content-Length"),
                new RequestUriReplacingOperationPreprocessor(
                    LOCAL_HOST_PATTERN, apiUrlBase
                ),
                prettyPrint()
            )
            .withResponseDefaults(
                apiUrlReplacer,
                new HeaderContentReplacingOperationPreprocessor(
                    "Location", LOCAL_HOST_PATTERN, apiUrlBase
                ),
                prettyPrint()
            )
    ).build();
    restDocumentation.apply(base, description).evaluate();
  }

  @Transactional(readOnly = true)
  public void thenInDb(Runnable call) {
    call.run();
  }

}
