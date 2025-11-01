package com.empsysauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

	@Value("${jwt.private-key-path:private_key.pem}")
	private String privateKeyPath;

	@Value("${jwt.public-key-path:public_key.pem}")
	private String publicKeyPath;

	private PrivateKey privateKey;
	private PublicKey publicKey;
	private JwtParser jwtParser;

	@PostConstruct
	public void init() {
		try {
			// Get root directory (project root)
			String rootPath = System.getProperty("user.dir");
			
			// Check if keys exist in root folder
			File privateKeyFile = new File(rootPath, "private_key.pem");
			File publicKeyFile = new File(rootPath, "public_key.pem");
			
			// Generate keys if they don't exist
			if (!privateKeyFile.exists() || !publicKeyFile.exists()) {
				RsaKeyGenerator.generateKeys(rootPath);
			}
			
			// Use root folder paths
			privateKeyPath = Paths.get(rootPath, "private_key.pem").toString();
			publicKeyPath = Paths.get(rootPath, "public_key.pem").toString();
			
			// Load private key
			privateKey = loadPrivateKey(privateKeyPath);
			
			// Load public key
			publicKey = loadPublicKey(publicKeyPath);
			
			// Initialize JWT parser with public key
			jwtParser = Jwts.parserBuilder()
					.setSigningKey(publicKey)
					.build();
			
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize RSA keys: " + e.getMessage(), e);
		}
	}

	private PrivateKey loadPrivateKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String keyContent = new String(Files.readAllBytes(Paths.get(path)));
		keyContent = keyContent.replace("-----BEGIN PRIVATE KEY-----", "")
				.replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		
		byte[] keyBytes = Base64.getDecoder().decode(keyContent);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(spec);
	}

	private PublicKey loadPublicKey(String path) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		String keyContent = new String(Files.readAllBytes(Paths.get(path)));
		keyContent = keyContent.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");
		
		byte[] keyBytes = Base64.getDecoder().decode(keyContent);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(spec);
	}

	public String generateToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
				.signWith(privateKey)
				.compact();
	}

	public String extractUsername(String token) {
		Claims claims = jwtParser.parseClaimsJws(token).getBody();
		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		try {
			jwtParser.parseClaimsJws(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Get the public key in PEM format as a string.
	 * 
	 * @return Public key in PEM format
	 */
	public String getPublicKeyPem() {
		try {
			String keyContent = new String(Files.readAllBytes(Paths.get(publicKeyPath)));
			return keyContent.trim();
		} catch (IOException e) {
			throw new RuntimeException("Failed to read public key: " + e.getMessage(), e);
		}
	}

	/**
	 * Get the public key object.
	 * 
	 * @return PublicKey object
	 */
	public PublicKey getPublicKey() {
		return publicKey;
	}
}
