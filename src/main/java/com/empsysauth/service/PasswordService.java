package com.empsysauth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for password encoding and verification using BCrypt.
 */
@Service
public class PasswordService {

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	/**
	 * Encodes a plain text password using BCrypt.
	 * 
	 * @param plainPassword The plain text password to encode
	 * @return BCrypt hashed password
	 */
	public String encodePassword(String plainPassword) {
		return passwordEncoder.encode(plainPassword);
	}

	/**
	 * Verifies if a plain text password matches a BCrypt hash.
	 * 
	 * @param plainPassword The plain text password to verify
	 * @param hashedPassword The BCrypt hashed password
	 * @return true if the password matches, false otherwise
	 */
	public boolean matches(String plainPassword, String hashedPassword) {
		return passwordEncoder.matches(plainPassword, hashedPassword);
	}
}


