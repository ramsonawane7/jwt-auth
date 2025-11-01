package com.empsysauth.controller;

import com.empsysauth.dto.AuthRequest;
import com.empsysauth.dto.AuthResponse;
import com.empsysauth.dto.PublicKeyResponse;
import com.empsysauth.service.JwtService;
import com.empsysauth.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@Autowired
	private JwtService jwtService;

	@Autowired
	private JwtUtil jwtUtil;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) {
		try {
			logger.info("Login attempt for username: {}", request.getUsername());
			String token = jwtService.authenticate(request.getUsername(), request.getPassword());
			logger.info("Login successful for username: {}", request.getUsername());
			return ResponseEntity.ok(new AuthResponse(token));
		} catch (RuntimeException e) {
			logger.error("Login failed for username: {} - {}", request.getUsername(), e.getMessage());
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("{\"error\": \"" + e.getMessage() + "\"}");
		} catch (Exception e) {
			logger.error("Unexpected error during login for username: {}", request.getUsername(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{\"error\": \"Internal server error\"}");
		}
	}

	@GetMapping("/public-key")
	public ResponseEntity<PublicKeyResponse> getPublicKey() {
		try {
			String publicKeyPem = jwtUtil.getPublicKeyPem();
			PublicKeyResponse response = new PublicKeyResponse(
					publicKeyPem,
					"RSA",
					"PEM",
					"PUBLIC"
			);
			logger.debug("Public key requested and returned");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			logger.error("Error retrieving public key", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}

