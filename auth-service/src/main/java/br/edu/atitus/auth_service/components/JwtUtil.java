package br.edu.atitus.auth_service.components;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import br.edu.atitus.auth_service.entities.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

public class JwtUtil {

	private static final String JWT_SECRET = "jwtSecretKeyForMicroservicesAuthExample$%456"; // Em produção, usar variável de ambiente
	private static final long TOKEN_VALIDITY_MS = 86400000; // 24 horas em milissegundos

	private static SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
	}

	public static String generateToken(String email, Long id, UserType type) {
		Map<String, Object> tokenClaims = new HashMap<>();
		tokenClaims.put("id", id);
		tokenClaims.put("email", email);
		tokenClaims.put("type", type.ordinal());
		
		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + TOKEN_VALIDITY_MS);
		
		return Jwts.builder()
				.claims(tokenClaims)
				.issuedAt(now)
				.expiration(expirationDate)
				.signWith(getSigningKey())
				.compact();
	}

	public static Claims validateToken(String token) {
		try {
			return Jwts.parser()
					.verifyWith(getSigningKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getJwtFromRequest(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader == null || authHeader.isEmpty()) {
			authHeader = request.getHeader("authorization");
		}
		if (authHeader != null && !authHeader.isEmpty() && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		return null;
	}

}