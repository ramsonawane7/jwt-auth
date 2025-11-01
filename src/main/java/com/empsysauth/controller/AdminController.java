package com.empsysauth.controller;

import com.empsysauth.dto.AuthRequest;
import com.empsysauth.entity.UserCreds;
import com.empsysauth.repository.UserCredsRepository;
import com.empsysauth.service.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for user management.
 * Note: In production, secure these endpoints with proper authentication and authorization.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

	@Autowired
	private UserCredsRepository userCredsRepository;

	@Autowired
	private PasswordService passwordService;

	/**
	 * Create a new user with BCrypt hashed password.
	 * 
	 * POST /api/admin/users
	 * Body: { "username": "user1", "password": "plainPassword123" }
	 */
	@PostMapping("/users")
	public ResponseEntity<?> createUser(@RequestBody AuthRequest request) {
		try {
			// Check if user already exists
			if (userCredsRepository.findByUsername(request.getUsername()) != null) {
				return ResponseEntity.badRequest().body("Username already exists");
			}

			// Create new user with hashed password
			UserCreds newUser = new UserCreds();
			newUser.setUsername(request.getUsername());
			newUser.setPasswordHash(passwordService.encodePassword(request.getPassword()));

			userCredsRepository.save(newUser);

			return ResponseEntity.ok("User created successfully");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error creating user: " + e.getMessage());
		}
	}

	/**
	 * Update password for an existing user.
	 * 
	 * PUT /api/admin/users/{username}/password
	 * Body: { "password": "newPassword123" }
	 */
	@PutMapping("/users/{username}/password")
	public ResponseEntity<?> updatePassword(@PathVariable String username, @RequestBody AuthRequest request) {
		try {
			UserCreds user = userCredsRepository.findByUsername(username);
			if (user == null) {
				return ResponseEntity.notFound().build();
			}

			// Hash and update password
			user.setPasswordHash(passwordService.encodePassword(request.getPassword()));
			userCredsRepository.save(user);

			return ResponseEntity.ok("Password updated successfully");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error updating password: " + e.getMessage());
		}
	}

	/**
	 * Hash a plain text password (utility endpoint for testing).
	 * 
	 * POST /api/admin/hash-password
	 * Body: { "password": "plainPassword123" }
	 */
	@PostMapping("/hash-password")
	public ResponseEntity<?> hashPassword(@RequestBody AuthRequest request) {
		try {
			String hashedPassword = passwordService.encodePassword(request.getPassword());
			return ResponseEntity.ok().body("{\"hashedPassword\": \"" + hashedPassword + "\"}");
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body("Error hashing password: " + e.getMessage());
		}
	}
}


