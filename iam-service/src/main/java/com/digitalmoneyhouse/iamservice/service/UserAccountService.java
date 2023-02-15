package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.repository.RoleRepository;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserAccountService {

    @Autowired
    private RoleRepository RoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserAccountRepository repository;

    public UserAccountResponse save(UserAccountBody userAccountBody) {
        existsByEmailOrCPF(userAccountBody.getEmail(), userAccountBody.getCpf());
        String encryptedPassword = bCryptPasswordEncoder.encode(userAccountBody.getPassword());
        userAccountBody.setPassword(encryptedPassword);
        UserAccount userModel = new UserAccount(userAccountBody);
        userModel.setRoles(Arrays.asList(RoleRepository.getById(1)));
        userModel = repository.save(userModel);
        return new UserAccountResponse(userModel);
    }

    public UserAccount findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public UserAccount getUserByPasswordResetToken(PasswordResetToken passwordResetToken) {
        return repository.findByToken(passwordResetToken.getToken());
    }

    public void changeUserPassword(UserAccount user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));
        repository.save(user);
    }

    public void existsByEmailOrCPF(String email, String cpf) {
        if (repository.existsByEmail(email)) {
            throw new DataIntegrityViolationException(String.format("Value %s for field E-mail is already in use", email));
        }
        if (repository.existsByCpf(cpf)) {
            throw new DataIntegrityViolationException(String.format("Value %s for field CPF is already in use", cpf));
        }
    }
}
