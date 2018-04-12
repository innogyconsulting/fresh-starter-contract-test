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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;

import java.util.Objects;

/**
 * Hamcrest {@link Matcher} operating on logical JSON structure analyzed by {@link JSONCompare}.
 *
 * @author morisil
 */
public class JsonMatcher extends TypeSafeDiagnosingMatcher<String> {

  private final String expected;

  private final JSONCompareMode mode;

  public JsonMatcher(String expected, JSONCompareMode mode) {
    this.expected = Objects.requireNonNull(expected);
    this.mode = Objects.requireNonNull(mode);
  }

  @Override
  protected boolean matchesSafely(String item, Description mismatchDescription) {
    try {
      JSONCompareResult result = JSONCompare.compareJSON(expected, item, mode);
      if (!result.failed()) {
        return true;
      }
      mismatchDescription
          .appendText("JSONs differ: \n")
          .appendText(result.getMessage());
    } catch (JSONException e) {
      mismatchDescription
          .appendText("Invalid JSON: ")
          .appendText(e.getMessage())
          .appendText("\n");
    }
    mismatchDescription
        .appendText("\n")
        .appendText("Full JSON was:\n")
        .appendText(item);
    return false;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(expected);
  }

  public static Matcher<String> jsonEquals(String expected) {
    return new JsonMatcher(expected, JSONCompareMode.STRICT);
  }

}
