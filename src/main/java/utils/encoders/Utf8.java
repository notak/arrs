package utils.encoders;

import static utils.arrays.Bytes.EMPTY;

import java.nio.charset.StandardCharsets;

import utils.arrays.Bytes;

public class Utf8 {
	public static String decode(byte[] data) {
		return (data==null || data.length==0) ? ""
			: new String(data, StandardCharsets.UTF_8);
	}
	
	public static byte[] encode(String data) {
		return data==null ? EMPTY 
			: data.getBytes(StandardCharsets.UTF_8); 
	}
}
