package sknictik.wafercodingchallenge;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.IllegalFormatConversionException;

import sknictik.wafercodingchallenge.presentation.utils.ResourceMessage;
import sknictik.wafercodingchallenge.presentation.utils.ResourceMessageFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class ResourceMessageFormatterTest {

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private ResourceMessageFormatter resourceMessageFormatter;

    @Before
    public void beforeTests() {
        // Context of the app under test.
        resourceMessageFormatter = new ResourceMessageFormatter(InstrumentationRegistry.getTargetContext());
    }

    @Test
    public void test_baseNoInlineVars() {
        final String base = "123";
        assertThat("Resource message formatter returns just base string of resource message if resource message has only base string"
                , base, is(resourceMessageFormatter.format(new ResourceMessage(base))));
    }

    @Test
    public void test_NullBase() {
        if (BuildConfig.DEBUG) {
            expectedEx.expect(NullPointerException.class);
            expectedEx.expectMessage("Message base can't be null");
        }
        assertThat("In any build config except debug resource message formatter returns" +
                        " empty string, if base string of resource message is null",
                "", is(resourceMessageFormatter.format(new ResourceMessage(null))));
    }

    @Test
    public void test_NullContext_fail() {
        if (BuildConfig.DEBUG) {
            expectedEx.expect(NullPointerException.class);
            expectedEx.expectMessage("Context can't be null");
        }
        assertThat("In any build config except debug resource message formatter " +
                        "returns empty string, if context provided inside resource message formatter is null",
                "", is(new ResourceMessageFormatter(null).format(new ResourceMessage(null))));
    }

    @Test
    public void test_WrongTypeBase() {
        if (BuildConfig.DEBUG) {
            expectedEx.expect(IllegalArgumentException.class);
            expectedEx.expectMessage("Unsupported variable format");
        }
        assertThat("In any build config except debug resource message formatter returns empty string," +
                " if base message inside resource message object is of the unsupported type",
                "", is(resourceMessageFormatter.format(new ResourceMessage(true))));
    }

    @Test
    public void test_WrongInlineVarType() {
        if (BuildConfig.DEBUG) {
            expectedEx.expect(IllegalArgumentException.class);
            expectedEx.expectMessage("Unsupported variable format");
        }
        assertThat("In any build config except debug resource message formatter" +
                        " will replace all placeholders inside base message that aren't of expected type with word \"null\"",
                "null", is(resourceMessageFormatter.format(new ResourceMessage("%s", new ResourceMessage("123")))));
    }

    @Test
    public void test_BaseWithOneStringInlineVar() {
        assertThat("Resource message formatter replaces placeholder inside base message" +
                " of ResourceMessage object with String argument provided in format() method as inline variable",
                "Test: 123", is(resourceMessageFormatter.format(new ResourceMessage("Test: %s", "123"))));
    }

    //This test should throw exception because second inline variable is not of expected type Integer
    @Test(expected = IllegalFormatConversionException.class)
    public void test_BaseWithTwoStringInlineVarWrongTypeSecondVar_exceptionFail() {
        resourceMessageFormatter.format(new ResourceMessage("Test: %s %d", "123", "123"));
    }

    @Test
    public void test_BaseWithTwoStringInlineVar() {
        final String arg1 = "123";
        final String arg2 = "321";
        assertThat("Resource message formatter replaces placeholders inside base message" +
                " of ResourceMessage object with String arguments provided in format() method as inline variables",
                "Test: " + arg1 + ' ' + arg2,
                is(resourceMessageFormatter.format(new ResourceMessage("Test: %s %s", arg1, arg2))));
    }

    @Test
    public void test_BaseWithOneStringOneResInlineVar() {
        final String resolvedRes = InstrumentationRegistry.getContext().getString(R.string.var1);
        assertThat("Resource message formatter will treat integer inline variables as string resource id " +
                        "and replace first placeholders with first provided String and second placeholder" +
                        " with string retrieved by Context from strings.xml file",
                "Test: 123 " + resolvedRes,
                is(resourceMessageFormatter.format(new ResourceMessage("Test: %s %s", "123", R.string.var1))));
    }

    @Test(expected = Resources.NotFoundException.class)
    public void test_BaseWithOneStringOneResInlineVar_failResNotResolved() {
        final int notExistingRes = -5;
        resourceMessageFormatter.format(new ResourceMessage("Test: %s %s", "123", notExistingRes));
    }

}
