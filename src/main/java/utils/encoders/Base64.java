package utils.encoders;

import static utils.arrays.Bytes.EMPTY;

import utils.arrays.Bytes;

public class Base64 {
	public static String encode(byte[] data) {
		return (data==null || data.length==0) ? ""
			: java.util.Base64.getEncoder().encodeToString(data);
	}
	
	public static byte[] decode(String data) {
		try { 
			return data==null ? EMPTY 
				: java.util.Base64.getDecoder().decode(data); 
		} catch (IllegalArgumentException e) { 
			return Bytes.EMPTY; 
		}
	}
}
