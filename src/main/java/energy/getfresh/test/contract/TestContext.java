
package energy.getfresh.test.contract;

import org.junit.rules.ExternalResource;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.LinkedList;

/**
 * Test context rule with separate instance created on behalf of each {@link BelcantoTest}s.
 *
 * @author morisil
 */
@Component
@Scope("prototype")
public class TestContext extends ExternalResource {

  private final int port;

  private final EntityManager entityManager;

  private final LinkedList<Object> entities = new LinkedList<>();

  @Inject
  public TestContext(
      @LocalServerPort int port,
      EntityManager entityManager
  ) {
    this.port = port;
    this.entityManager = entityManager;
  }

  @Override
  @Transactional
  protected void after() {
    for (Object entity : entities) {
      entityManager.remove(entity);
    }
  }

  @Transactional
  public <T> T populate(T entity) {
    entityManager.persist(entity);
    entities.addFirst(entity);
    return entity;
  }

  public int getPort() {
    return port;
  }

}
