/**
 * Sha256.java 
 * This is the class for calculating block hash using Sha256 algorithm
 * 
 * 01.April.2021
 * @author Tun Pyay Sone LIN
 * 
 */

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Sha256{
	
	/**
	 * This function takes a string input and calls Sha256 algorithm 
	 * @param input
	 * @return hashed string of input
	 */
	public static String getSHA(String input){		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256"); //Applies sha256 to our input, 
			byte[] hash = digest.digest(input.getBytes("UTF-8"));	        
			StringBuffer hexString = new StringBuffer(); // This will contain hash as hexidecimal
			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if(hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

