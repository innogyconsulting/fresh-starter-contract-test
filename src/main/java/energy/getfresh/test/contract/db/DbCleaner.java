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

package energy.getfresh.test.contract.db;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.persistence.metamodel.Type;
import java.util.List;

@Component
public class DbCleaner {

  private final EntityManager entityManager;

  private List<String> tables;

  @Inject
  public DbCleaner(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @PostConstruct
  public void initialize() {
    tables = entityManager.getMetamodel().getManagedTypes().stream()
        .map(Type::getJavaType)
        .map(this::getTable)
        .sorted()
        .collect(ImmutableList.toImmutableList());
  }

  @Transactional
  public void truncateAllTables() {
    entityManager.flush();
    entityManager.getEntityManagerFactory().getCache().evictAll();
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
    for (String table : tables) {
      entityManager.createNativeQuery("TRUNCATE TABLE " + table).executeUpdate();
    }
    entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  private String getTable(Class<?> type) {
    Table table = type.getAnnotation(Table.class);
    return (table != null)
        ? table.name().toUpperCase()
        : CaseFormat.UPPER_CAMEL
            .converterTo(CaseFormat.UPPER_UNDERSCORE)
            .convert(type.getSimpleName());
  }

}
