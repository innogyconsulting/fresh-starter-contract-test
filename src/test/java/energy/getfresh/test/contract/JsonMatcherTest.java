package energy.getfresh.test.contract;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test of the {@link JsonMatcher}.
 *
 * @author morisil
 */
public class JsonMatcherTest {

  private static final JSONCompareMode MODE = JSONCompareMode.STRICT;

  @Test
  public void matches_equalJsons_shouldMatch() {
    // given
    String expected = "{\"foo\": 42}";
    String actual = "{\"foo\": 42}";
    Matcher<String> matcher = new JsonMatcher(expected, MODE);

    // when
    boolean matches = matcher.matches(actual);

    // then
    assertThat(matches).isTrue();
  }

  @Test
  public void matches_equalJsonsWithReorderedAttributes_shouldMatch() {
    // given
    String expected = "{\"foo\": 42, \"bar\": 43}";
    String actual = "{\"bar\": 43, \"foo\": 42}";
    Matcher<String> matcher = new JsonMatcher(expected, MODE);

    // when
    boolean matches = matcher.matches(actual);

    // then
    assertThat(matches).isTrue();
  }

  @Test
  public void matches_nonEqualJsons_shouldNotMatchAndDescribeError() {
    // given
    String expected =
        "{\n" +
            "  \"foo\": {\n" +
            "    \"bar\": 42,\n" +
            "    \"buzz\": 42\n" +
            "  }\n" +
            "}\n";
    String actual =
        "{\n" +
            "  \"foo\": {\n" +
            "    \"bar\": 43\n" +
            "  }\n" +
            "}\n";
    Matcher<String> matcher = new JsonMatcher(expected, MODE);

    // when
    boolean matches = matcher.matches(actual);

    // then
    assertThat(matches).isFalse();
    Description matcherDescription = new StringDescription();
    matcher.describeTo(matcherDescription);
    assertThat(matcherDescription.toString()).isEqualTo(expected);
    Description mismatchDescription = new StringDescription();
    matcher.describeMismatch(actual, mismatchDescription);
    assertThat(mismatchDescription.toString()).isEqualTo(
        "JSONs differ: \n" +
            "foo.bar\n" +
            "Expected: 42\n" +
            "     got: 43\n" +
            " ; foo\n" +
            "Expected: buzz\n" +
            "     but none found\n" +
            "\n" +
            "Full JSON was:\n" +
            "{\n" +
            "  \"foo\": {\n" +
            "    \"bar\": 43\n" +
            "  }\n" +
            "}\n" +
            "");
  }

  @Test
  public void matches_invalidJson_shouldNotMatchAndDescribeError() {
    // given
    String expected = "{foo}";
    String actual = "{bar}";
    Matcher<String> matcher = new JsonMatcher(expected, MODE);

    // when
    boolean matches = matcher.matches(actual);

    // then
    assertThat(matches).isFalse();
    Description matcherDescription = new StringDescription();
    matcher.describeTo(matcherDescription);
    assertThat(matcherDescription.toString()).isEqualTo(expected);
    Description mismatchDescription = new StringDescription();
    matcher.describeMismatch(actual, mismatchDescription);
    assertThat(mismatchDescription.toString()).isEqualTo(
        "Invalid JSON: Expected ':' after foo at character 5 of {foo}\n" +
            "\n" +
            "Full JSON was:\n" +
            "{bar}");
  }

}
