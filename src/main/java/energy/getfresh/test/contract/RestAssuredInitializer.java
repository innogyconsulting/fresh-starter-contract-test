
package energy.getfresh.test.contract;

import io.restassured.RestAssured;
import io.restassured.config.MatcherConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * Initializes context of {@link BelcantoTest}s. A lazy singleton which is initialized only once on the startup
 * of shared test container.
 *
 * @author morisil
 */
@Component
@Lazy
// thanks to @Lazy it will be created very late in the container lifecycle, after establishing HTTP port
public class RestAssuredInitializer {

  private final int port;

  @Inject
  public RestAssuredInitializer(@LocalServerPort int port) {
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
