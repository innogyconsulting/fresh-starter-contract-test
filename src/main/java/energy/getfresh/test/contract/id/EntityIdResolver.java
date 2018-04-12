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

import javax.inject.Inject;
import java.util.Map;

/**
 * Resolver of entity id for given textual {@code id} and desired {@code idClass}.
 *
 * @author morisil
 */
@Service
public class EntityIdResolver {

  private final Map<?, EntityIdConverter<?>> converterMap;

  @Inject
  public EntityIdResolver(
      Map<?, EntityIdConverter<?>> converterMap
  ) {
    this.converterMap = converterMap;
  }

  public <T> T resolve(String id, Class<T> idClass) {
    return getConverter(idClass).convert(id);
  }

  @SuppressWarnings("unchecked")
  private <T> EntityIdConverter<T> getConverter(Class<T> idClass) {
    EntityIdConverter converter = converterMap.get(idClass);
    if (converter == null) {
      throw new IllegalArgumentException(
          "Entity @Id type not supported: " + idClass.getName()
      );
    }
    return converter;
  }
}
