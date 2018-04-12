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

package energy.getfresh.test.contract.id;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of the {@link EntityIdResolver}.
 *
 * @author morisil
 */
public class EntityIdResolverTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void resolve_numericStringForExistingConverter_shouldReturnConvertedValue() {
    // given
    EntityIdResolver resolver = new EntityIdResolver(
        ImmutableMap.of(Long.class, new LongEntityIdConverter())
    );
    String id = "42";

    // when
    Long converted = resolver.resolve(id, Long.class);

    // then
    assertThat(converted).isEqualTo(42L);
  }

  @Test
  public void resolve_numericStringForNonExistingConverter_shouldThrowException() {
    // given
    EntityIdResolver resolver = new EntityIdResolver(Collections.emptyMap());
    String id = "42";
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Entity @Id type not supported: java.lang.Long");

    // when
    resolver.resolve(id, Long.class);

    // then exception should be thrown
  }

}
