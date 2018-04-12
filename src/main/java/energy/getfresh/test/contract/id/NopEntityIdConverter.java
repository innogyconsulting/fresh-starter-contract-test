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

import org.springframework.stereotype.Service;

import javax.inject.Singleton;

/**
 * Converts {@code id} to the same {@code id}.
 *
 * @author morisil
 */
@Service
@Singleton
public class NopEntityIdConverter implements EntityIdConverter<String> {

  @Override
  public Class<String> getIdClass() {
    return String.class;
  }

  @Override
  public String convert(String id) {
    return id;
  }

}
