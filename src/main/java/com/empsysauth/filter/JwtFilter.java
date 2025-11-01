package com.empsysauth.filter;
import com.empsysauth.util.JwtUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter implements Filter {
	
	private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	
	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Check if the request path is a public endpoint that doesn't require JWT authentication
	 */
	private boolean isPublicEndpoint(String path) {
		if (path == null) {
			return false;
		}
		
		// Public endpoints - no JWT authentication required
		return path.equals("/api/auth/login") ||
			   path.equals("/api/auth/public-key") ||
			   path.startsWith("/api/admin/");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getRequestURI();

		// Skip JWT filtering for public endpoints - let them pass through without authentication
		if (isPublicEndpoint(path)) {
			logger.debug("Skipping JWT filter for public endpoint: {}", path);
			chain.doFilter(request, response);
			return;
		}

		// For protected endpoints, validate JWT token
		String authHeader = req.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			try {
				String username = jwtUtil.extractUsername(token);

				if (jwtUtil.validateToken(token) && username != null) {
					UsernamePasswordAuthenticationToken authToken =
							new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

					SecurityContextHolder.getContext().setAuthentication(authToken);
					logger.debug("JWT authentication successful for user: {}", username);
				}
			} catch (Exception e) {
				logger.debug("JWT token validation failed: {}", e.getMessage());
				// Invalid token, continue without authentication (let Spring Security handle authorization)
			}
		}

		chain.doFilter(request, response);
	}
}
