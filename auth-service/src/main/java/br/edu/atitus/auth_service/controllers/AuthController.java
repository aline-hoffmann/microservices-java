package br.edu.atitus.auth_service.controllers;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.atitus.auth_service.components.JwtUtil;
import br.edu.atitus.auth_service.dtos.SigninDTO;
import br.edu.atitus.auth_service.dtos.SigninResponseDTO;
import br.edu.atitus.auth_service.dtos.SignupDTO;
import br.edu.atitus.auth_service.entities.UserEntity;
import br.edu.atitus.auth_service.entities.UserType;
import br.edu.atitus.auth_service.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService userService;
	private final AuthenticationConfiguration authenticationConfig;

	public AuthController(UserService userService, AuthenticationConfiguration authenticationConfig) {
		this.userService = userService;
		this.authenticationConfig = authenticationConfig;
	}

	private UserEntity mapSignupToEntity(SignupDTO signupRequest) {
		UserEntity newUser = new UserEntity();
		BeanUtils.copyProperties(signupRequest, newUser);
		return newUser;
	}

	@PostMapping("/signup")
	public ResponseEntity<UserEntity> signup(@RequestBody SignupDTO signupRequest) throws Exception {
		UserEntity newUser = mapSignupToEntity(signupRequest);
		newUser.setType(UserType.Common);
		userService.save(newUser);
		return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
	}

	@PostMapping("/signin")
	public ResponseEntity<SigninResponseDTO> signin(@RequestBody SigninDTO loginRequest) throws AuthenticationException, Exception {
		authenticationConfig.getAuthenticationManager()
					.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
		UserEntity authenticatedUser = (UserEntity) userService.loadUserByUsername(loginRequest.email());
		String jwtToken = JwtUtil.generateToken(authenticatedUser.getEmail(), authenticatedUser.getId(), authenticatedUser.getType());
		SigninResponseDTO authResponse = new SigninResponseDTO(authenticatedUser, jwtToken);
		return ResponseEntity.ok(authResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception exception) {
		String sanitizedMessage = exception.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sanitizedMessage);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthenticationException(AuthenticationException exception) {
		String sanitizedMessage = exception.getMessage().replaceAll("[\\r\\n]", " ");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(sanitizedMessage);
	}
}