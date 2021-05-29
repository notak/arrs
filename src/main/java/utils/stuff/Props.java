package utils.stuff;

import static java.util.Optional.ofNullable;

import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public class Props {
	/* Static versions */

	/** Optionally get the property referenced by key */
	public static Optional<String> prop(Properties props, String key) {
		return ofNullable(props.getProperty(key));
	}
	
	/** Get the String property referenced by key, or default if not present */
	public static String prop(Properties props, String key, String def) {
		return props.getProperty(key, def);
	}
	
	/** Get the int property referenced by key, or default if the key is not 
	 * present or the mapped value can't be converted into an integer */
	public static int prop(Properties props, String key, int def) {
		return prop(props, key).flatMap(Numbers::parseInt).orElse(def);
	}

	/** If the property referenced by key is "false", return false. 
	 * If it is present but not "false" (eg. "true") return true.
	 * If it is absent, return def */
	public static boolean prop(Properties props, String key, boolean def) {
		return prop(props, key).map(s->!"false".equals(s)).orElse(def);
	}
	
	public final Properties props;

	public Props(Properties p) {
		props = p;
	}
	
	public Props(String path) throws IOException {
		this(new Properties());
		props.load(new FileReader(path));
	}

	/** Optionally get the property referenced by key */
	public Optional<String> prop(String key) { 
		return prop(props, key); 
	}
	
	/** Get the String property referenced by key, or default if not present */
	public String prop(String key, String def) { 
		return prop(props, key, def); 
	}
	
	/** Get the int property referenced by key, or default if the key is not 
	 * present or the mapped value can't be converted into an integer */
	public int prop(String key, int def) {
		return prop(props, key, def);
	}
	
	/** If the property referenced by key is "false", return false. 
	 * If it is present but not "false" (eg. "true") return true.
	 * If it is absent, return def */
	public boolean prop(String key, boolean def) {
		return prop(props, key, def);
	}
}
