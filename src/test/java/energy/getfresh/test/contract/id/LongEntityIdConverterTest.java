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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test of the {@link LongEntityIdConverter}.
 *
 * @author morisil
 */
public class LongEntityIdConverterTest {

  @Test
  public void convert_numericString_shouldReturnAsLong() {
    // given
    LongEntityIdConverter idConverter = new LongEntityIdConverter();
    String id = "42";

    // when
    Long converted = idConverter.convert(id);

    // then
    assertThat(converted).isEqualTo(42L);
  }

}
