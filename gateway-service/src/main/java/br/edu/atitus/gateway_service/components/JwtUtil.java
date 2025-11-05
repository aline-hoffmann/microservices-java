package br.edu.atitus.gateway_service.components;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

	private static final String JWT_SECRET = "jwtSecretKeyForMicroservicesAuthExample$%456"; // Deve ser a mesma do auth-service

	private static SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
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
}