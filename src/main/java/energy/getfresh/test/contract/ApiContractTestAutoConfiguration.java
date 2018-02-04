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

import com.google.common.collect.ImmutableMap;
import energy.getfresh.test.contract.id.EntityIdConverter;
import org.junit.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * Spring boot auto-configuration of this library. Will provide
 * configured {@link ApiContext} to be injected as JUnit {@link Rule}
 * into your integration tests.
 *
 * @author morisil
 */
@Configuration
@ComponentScan
public class ApiContractTestAutoConfiguration {

  @Bean
  @Singleton
  @Named("entityIdConverterMap")
  public Map<Class<?>, EntityIdConverter<?>> entityIdConverterMap(
      List<EntityIdConverter<?>> entityIdConverters
  ) {
    return entityIdConverters.stream()
        .collect(ImmutableMap.toImmutableMap(
            EntityIdConverter::getIdClass,
            converter -> converter)
        );
  }

}
