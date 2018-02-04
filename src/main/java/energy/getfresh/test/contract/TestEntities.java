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

import energy.getfresh.test.contract.id.EntityIdResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.metamodel.EntityType;
import java.util.LinkedList;

/**
 * Manages JPA entities created on behalf of each test.
 *
 * @author morisil
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TestEntities {

  private final EntityManager entityManager;

  private final EntityIdResolver entityIdResolver;

  private final LinkedList<Object> entities = new LinkedList<>();

  @Inject
  public TestEntities(
      EntityManager entityManager,
      EntityIdResolver entityIdResolver
  ) {
    this.entityManager = entityManager;
    this.entityIdResolver = entityIdResolver;
  }

  @Transactional
  public void persist(Object entity) {
    entityManager.persist(entity);
    entities.addFirst(entity);
  }

  public void trackExistingEntity(Class<?> entityClass, String stringId) {
    Class<?> idClass = getIdClass(entityClass);
    Object id = entityIdResolver.resolve(stringId, idClass);
    Object entity = entityManager.find(entityClass, id);
    entities.addFirst(entity);
  }

  public Object getLatestEntity() {
    return entities.getFirst();
  }

  @Transactional
  public void clean() {
    for (Object entity : entities) {
      entity = entityManager.merge(entity);
      entityManager.remove(entity);
    }
  }

  private Class<?> getIdClass(Class<?> entityClass) {
    EntityType<?> entityType = entityManager.getMetamodel().entity(entityClass);
    return entityType.getIdType().getJavaType();
  }

}
