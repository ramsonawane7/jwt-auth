package com.empsysauth.util;

/**
 * Utility class to generate RSA key pair.
 * Run this main method to generate private_key.pem and public_key.pem in the project root.
 */
public class GenerateRsaKeys {
	
	public static void main(String[] args) {
		try {
			String rootPath = System.getProperty("user.dir");
			System.out.println("Generating RSA keys in: " + rootPath);
			RsaKeyGenerator.generateKeys(rootPath);
			System.out.println("Keys generated successfully!");
		} catch (Exception e) {
			System.err.println("Error generating keys: " + e.getMessage());
			e.printStackTrace();
		}
	}
}


