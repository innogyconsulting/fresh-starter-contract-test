
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

import io.restassured.RestAssured;
import io.restassured.config.MatcherConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Initializes {@code RestAssured} optimal defaults. It should
 * be done only once, but very late on container initialization
 * (only when the {@link LocalServerPort} is already available), which
 * is ensured with {@link Lazy} and the fact that {@link ApiContext}
 * depends on this component.
 *
 * @author morisil
 */
@Component
@Lazy
public class RestAssuredInitializer {

  private final int port;

  @Inject
  public RestAssuredInitializer(@Value("${local.server.port}") int port) {
    this.port = port;
  }

  @PostConstruct
  public void initialize() {
    RestAssured.port = port;
    RestAssured.config = RestAssuredConfig
        .config()
        .matcherConfig(
            MatcherConfig
                .matcherConfig()
                .errorDescriptionType(MatcherConfig.ErrorDescriptionType.HAMCREST)
        );
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL);
  }

}
