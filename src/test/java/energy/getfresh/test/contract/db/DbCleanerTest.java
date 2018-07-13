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

import com.google.common.collect.ImmutableSet;
import energy.getfresh.test.contract.db.DbCleaner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.*;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DbCleanerTest {

  @Mock
  private EntityManager entityManager;

  @Mock
  private EntityManagerFactory entityManagerFactory;

  @Mock
  private Cache cache;

  @Mock
  private Metamodel metamodel;

  @Mock
  private ManagedType<FooBar> fooBarType;

  @Mock
  private ManagedType<FooBuzz> fooBuzzType;

  @Mock
  private Query query;

  @Test
  public void truncateAllTables_onPopulatedDatabase_shouldClearEverything() {
    // given
    given(entityManager.getEntityManagerFactory()).willReturn(entityManagerFactory);
    given(entityManagerFactory.getCache()).willReturn(cache);
    given(entityManager.createNativeQuery(anyString())).willReturn(query);
    given(entityManager.getMetamodel()).willReturn(metamodel);
    given(metamodel.getManagedTypes()).willReturn(ImmutableSet.of(fooBarType, fooBuzzType));
    given(fooBarType.getJavaType()).willReturn(FooBar.class);
    given(fooBuzzType.getJavaType()).willReturn(FooBuzz.class);
    DbCleaner dbCleaner = new DbCleaner(entityManager);
    dbCleaner.initialize();

    // when
    dbCleaner.truncateAllTables();

    // then
    InOrder inOrder = inOrder(entityManager, cache);
    inOrder.verify(entityManager).flush();
    inOrder.verify(entityManager).getEntityManagerFactory();
    inOrder.verify(cache).evictAll();
    inOrder.verify(entityManager).createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE");
    inOrder.verify(entityManager).createNativeQuery("TRUNCATE TABLE FOOBUZZ");
    inOrder.verify(entityManager).createNativeQuery("TRUNCATE TABLE FOO_BAR");
    inOrder.verify(entityManager).createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE");
    verify(query, times(4)).executeUpdate();
  }

  @Entity
  private static class FooBar { }

  @Entity
  @Table(name = "foobuzz")
  private static class FooBuzz { }

}
