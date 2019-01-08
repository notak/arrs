package utils.stuff;

import java.io.Closeable;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Support {
	public static <R> Optional<R> ifNotThen(boolean test, Optional<R> onFail) {
		return test ? Optional.empty() : onFail;
	}

	public static final Charset UTF8 = Charset.forName("UTF-8");
	public static final MessageDigest md5Encoder;
	static {
		try {
			md5Encoder= MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new Error("Couldn't create MD5 encoder");
		}
	}
	
	public static final <V> boolean eq(V a, V b) {
		return Objects.equals(a, b);
	}
	
	public static final <V, C>
	boolean pairedEq(V a, V b, C c, C d) {
		return eq(a, b) && eq(c, d);
	}
	
	public static final <V, C, R> boolean
	pairedEq(V a, V b, C c, C d, R e, R f) {
		return pairedEq(a, b, c, d) && eq(e, f);
	}

	public static <T> T orElse(T value, T def) { 
		return value==null ? def : value; 
	}
	
	public static String orElse(String value) { 
		return orElse(value, ""); 
	}
	
	public static <T> T orElse(T value, Supplier<T> def) { 
		return value==null ? def.get() : value; 
	}

	public static <T,V, R> Optional<R> mapBoth(
			Optional<T> a, Optional<V> b, BiFunction<T, V, R> ok
	) {
		return a.flatMap(ar->b.map(br->ok.apply(ar,br)));
	}

	public static <T> boolean notNull(T in) { return in!=null; }

	public static <T> T ifNull(T in, Supplier<T> otherwise) { 
		return in!=null ? in : otherwise.get(); 
	}

	public static boolean booleanOrFalse(String in) {
		return "true".equals(in);
	}
	
	public static boolean isY(String in) {
		return "Y".equals(in);
	}
	
	public static boolean trySleep(int millis) {
		try { Thread.sleep(millis); return true; } 
		catch (InterruptedException i) { return false; }
	}

	public static <R> CompletableFuture<R> future(R val) {
		CompletableFuture<R> out = new CompletableFuture<>();
		out.complete(val);
		return out;
	}

	public static <R> Optional<R> 
	ifNotError(CompletableFuture<R> val) {
		try { return Optional.of(val.get()); }
		catch (Exception e) { return Optional.empty(); }
	}

	public static <R> Optional<R> 
	ifNotError(CompletableFuture<R> val, long timeout) {
		try { return Optional.of(val.get(timeout, TimeUnit.MILLISECONDS)); }
		catch (Exception e) { return Optional.empty(); }
	}

	public static <R> Optional<R> 
	flatmapNotError(CompletableFuture<Optional<R>> val) {
		return ifNotError(val).flatMap(r->r);
	}

	public static <R> Optional<R> 
	flatmapNotError(CompletableFuture<Optional<R>> val, long timeout) {
		return ifNotError(val, timeout).flatMap(r->r);
	}
	
	public static void silentClose(Closeable... cs) {
		try { 
			for(var c: cs) if (c!=null) c.close(); 
		} catch (Exception e) { throw new Error(e); }
	}
}
