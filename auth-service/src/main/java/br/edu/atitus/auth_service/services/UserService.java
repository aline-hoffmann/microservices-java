package br.edu.atitus.auth_service.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.atitus.auth_service.components.Validator;
import br.edu.atitus.auth_service.entities.UserEntity;
import br.edu.atitus.auth_service.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	private void validateUser(UserEntity userEntity) throws Exception {
		if (userEntity.getName() == null || userEntity.getName().isEmpty())
			throw new Exception("Nome informado inválido");
		if (userEntity.getEmail() == null || userEntity.getEmail().isEmpty()
				|| !Validator.validateEmail(userEntity.getEmail()))
			throw new Exception("E-mail informado inválido");
		if (userEntity.getPassword() == null || userEntity.getPassword().isEmpty())
			throw new Exception("Senha informada inválida");

		if (userEntity.getId() != null) {
			if (repository.existsByEmailAndIdNot(userEntity.getEmail(), userEntity.getId()))
				throw new Exception("Já existe usuário com este e-mail");
		} else {
			if (repository.existsByEmail(userEntity.getEmail()))
				throw new Exception("Já existe usuário com este e-mail");
		}
	}

	private void encodePassword(UserEntity userEntity) throws Exception {
		userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
	}

	@Transactional
	public UserEntity save(UserEntity userEntity) throws Exception {
		if (userEntity == null)
			throw new Exception("Objeto nulo");
		validateUser(userEntity);
		encodePassword(userEntity);
		return repository.save(userEntity);
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		var foundUser = repository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com este e-mail"));
		return foundUser;
	}
}
