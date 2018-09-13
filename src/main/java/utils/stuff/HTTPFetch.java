package utils.stuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import utils.stuff.Fns.LogFn;

public class HTTPFetch {
	private HttpURLConnection conn;
	public File cache;
	
	public HTTPFetch(String url) 
	throws MalformedURLException, IOException {
		this(url, (File)null);
	}
	
	public HTTPFetch(String url, String cacheName) {
			this(url, 
				cacheName!=null ? new File("../cache/" + cacheName) : null
			);
	}
	
	public HTTPFetch(String url, File cache) {
		try {
			conn = (HttpURLConnection)
				new URL(url).openConnection();
			this.cache = cache;
		} catch (Exception e) { throw new Error(e); }
	}
	
	public boolean cached() { 
		return cache!=null && cache.exists(); 
	}
	
	public HTTPFetch method(String method) throws ProtocolException {
		conn.setRequestMethod(method);
		return this;
	}
	public HTTPFetch body(String body) throws IOException {
		byte[] bytes = body.getBytes();
		header("content-length", "" + bytes.length);
		conn.setDoOutput(true);
		conn.getOutputStream().write(bytes);
		return this;
	}

	public HTTPFetch auth(String user, String pass) {
		try {
			byte[] bytes = (user + ":" + pass).getBytes("UTF8");
			String auth = "Basic " + Base64.getEncoder().encodeToString(bytes);
			header("Authorization", auth);
			return this;
		} catch (UnsupportedEncodingException e) { throw new Error(e); }
	}
	
	public HTTPFetch followRedirects(boolean set) {
		conn.setInstanceFollowRedirects(set);
		return this;
	}
	
	public boolean isRedirect() throws IOException {
		return responseCode() == 301 || responseCode() == 302;
	}
	
	public boolean ok() throws IOException {
		return cached() || conn.getResponseCode() == HttpURLConnection.HTTP_OK;
	}
	
	public HTTPFetch header(String key, String value) {
		conn.setRequestProperty(key, value);
		return this;
	}
	
	public int responseCode() throws IOException {
		return conn.getResponseCode();
	}
	
	public String header(String key) throws IOException {
		return conn.getHeaderField(key);
	}
	
	private void tryPopulateCache() throws IOException {
		if (cache!=null && !cached()) {
			Path tmp = Files.createTempFile("httpcache", ".tmp");
			Files.copy(conn.getInputStream(), tmp, 
				StandardCopyOption.REPLACE_EXISTING);
			Files.move(tmp, cache.toPath());
		}
	}
	
	public Stream<String> body(LogFn log) throws IOException {
		tryPopulateCache();
		return cached() ? Files.lines(cache.toPath())
			: UTF8File.lines(conn.getInputStream(), log);
	}
	
	public static Stream<String> readGzip(InputStream is) 
	throws IOException {
		BufferedReader br = new BufferedReader(
			new InputStreamReader(new GZIPInputStream(is))
		);

		return br.lines().onClose(()->{
			try { br.close(); } 
			catch (IOException e) { throw new Error(e);}
		});
	}
	
	public Stream<String> gzippedBody() throws IOException {
		tryPopulateCache();
		InputStream is = cached() ? new FileInputStream(cache) 
			: conn.getInputStream();
		return readGzip(is);
	}
}