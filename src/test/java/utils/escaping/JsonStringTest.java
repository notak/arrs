package utils.escaping;

import static org.junit.jupiter.api.Assertions.*;
import static utils.escaping.JsonString.escape;
import static utils.escaping.JsonString.unescape;

import org.junit.jupiter.api.Test;

class JsonStringTest {

	@Test
    public void testEscapeJson() {
        assertNull(escape(null));

        assertEquals("He didn't say, \\\"stop!\\\"", 
        	escape("He didn't say, \"stop!\""));

        final String expected = 
        	"\\\"foo\\\" isn't \\\"bar\\\". specials: \\b\\r\\n\\f\\t\\\\\\/";
        final String input = 
        	"\"foo\" isn't \"bar\". specials: \b\r\n\f\t\\/";

        assertEquals(expected, escape(input));
    }

    @Test
    public void testUnescapeJson() {
        final String jsonString =
                "{\"age\":100,"
                + "\"name\":\"kyong.com\n\","
                + "\"messages\":[\"msg 1\",\"msg 2\",\"msg 3\"]"
                + "}";

        assertEquals("", unescape(""));
        assertEquals(" ", unescape(" "));
        assertEquals("a:b", unescape("a:b"));
        assertEquals(jsonString, unescape(jsonString));
    }
}
