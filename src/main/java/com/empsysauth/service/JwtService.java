package com.empsysauth.service;

import com.empsysauth.entity.UserCreds;
import com.empsysauth.repository.UserCredsRepository;
import com.empsysauth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);
	
	@Autowired
	private UserCredsRepository repo;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public String authenticate(String username, String password) {
		logger.debug("Attempting authentication for username: {}", username);
		
		UserCreds user = repo.findByUsername(username);
		
		if (user == null) {
			logger.warn("Authentication failed: User '{}' not found", username);
			throw new RuntimeException("Invalid Credentials!");
		}
		
		String storedPasswordHash = user.getPasswordHash();
		logger.debug("User found. Password hash stored (first 20 chars): {}", 
			storedPasswordHash != null && storedPasswordHash.length() > 20 
				? storedPasswordHash.substring(0, 20) + "..." 
				: storedPasswordHash);
		
		// Check if password is BCrypt hashed (starts with $2a$, $2b$, or $2y$)
		boolean isBcryptHash = storedPasswordHash != null && 
			(storedPasswordHash.startsWith("$2a$") || 
			 storedPasswordHash.startsWith("$2b$") || 
			 storedPasswordHash.startsWith("$2y$"));
		
		boolean passwordMatches = false;
		
		if (isBcryptHash) {
			// Password is already BCrypt hashed
			passwordMatches = passwordEncoder.matches(password, storedPasswordHash);
			logger.debug("BCrypt hash detected. Password match: {}", passwordMatches);
		} else {
			// Password is plain text (legacy) - compare directly
			passwordMatches = storedPasswordHash != null && storedPasswordHash.equals(password);
			logger.debug("Plain text password detected. Password match: {}", passwordMatches);
			
			// Auto-migrate to BCrypt if password matches
			if (passwordMatches) {
				logger.info("Auto-migrating password to BCrypt for user: {}", username);
				String hashedPassword = passwordEncoder.encode(password);
				user.setPasswordHash(hashedPassword);
				repo.save(user);
				logger.info("Password migrated successfully for user: {}", username);
			}
		}
		
		if (passwordMatches) {
			logger.info("Authentication successful for user: {}", username);
			return jwtUtil.generateToken(username);
		} else {
			logger.warn("Authentication failed: Invalid password for user: {}", username);
			throw new RuntimeException("Invalid Credentials!");
		}
	}
}
