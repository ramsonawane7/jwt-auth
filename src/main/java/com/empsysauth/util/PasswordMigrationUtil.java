package com.empsysauth.util;

import com.empsysauth.entity.UserCreds;
import com.empsysauth.repository.UserCredsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Uncomment below to enable password migration on startup
// import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Utility to migrate existing plain text passwords to BCrypt hashed passwords.
 * This will run automatically on application startup if enabled.
 * 
 * To use this, uncomment the @Component annotation below and run the application.
 * After migration, comment it out again to prevent re-running.
 */
// @Component
public class PasswordMigrationUtil implements CommandLineRunner {

	@Autowired
	private UserCredsRepository userCredsRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Starting password migration...");
		
		List<UserCreds> allUsers = userCredsRepository.findAll();
		int migratedCount = 0;
		
		for (UserCreds user : allUsers) {
			String passwordHash = user.getPasswordHash();
			
			// Check if password is already BCrypt hashed (BCrypt hashes start with $2a$, $2b$, or $2y$)
			if (passwordHash == null || 
				(!passwordHash.startsWith("$2a$") && 
				 !passwordHash.startsWith("$2b$") && 
				 !passwordHash.startsWith("$2y$"))) {
				
				// This is a plain text password, hash it
				String hashedPassword = passwordEncoder.encode(passwordHash);
				user.setPasswordHash(hashedPassword);
				userCredsRepository.save(user);
				
				System.out.println("Migrated password for user: " + user.getUsername());
				migratedCount++;
			}
		}
		
		System.out.println("Password migration completed! Migrated " + migratedCount + " users.");
		System.out.println("IMPORTANT: Comment out @Component annotation on PasswordMigrationUtil to prevent re-running.");
	}
}

