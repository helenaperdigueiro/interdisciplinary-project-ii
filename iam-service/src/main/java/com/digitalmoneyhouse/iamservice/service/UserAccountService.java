package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.dto.ConfirmRegistration;
import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.digitalmoneyhouse.iamservice.dto.UserAccountResponse;
import com.digitalmoneyhouse.iamservice.exception.AccountConfirmationException;
import com.digitalmoneyhouse.iamservice.exception.BusinessException;
import com.digitalmoneyhouse.iamservice.exception.CpfAlreadyInUseException;
import com.digitalmoneyhouse.iamservice.exception.EmailAlreadyInUseException;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.repository.RoleRepository;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import com.digitalmoneyhouse.iamservice.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;

@Service
public class UserAccountService {

    @Autowired
    private RoleRepository RoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserAccountRepository repository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public VerificationToken save(UserAccountBody userAccountBody) throws BusinessException {
        existsByCpfOrEmail(userAccountBody.getCpf(), userAccountBody.getEmail());
        String encryptedPassword = bCryptPasswordEncoder.encode(userAccountBody.getPassword());
        userAccountBody.setPassword(encryptedPassword);
        UserAccount userModel = new UserAccount(userAccountBody);
        userModel.setRoles(Arrays.asList(RoleRepository.getById(1)));
        userModel = repository.save(userModel);
        return createVerificationCode(userModel);
    }

    @Transactional
    public UserAccountResponse confirmRegistration(ConfirmRegistration confirmRegistration) throws Exception {
        Boolean isUserEmail = verificationTokenRepository.existsByVerificationCodeAndUserAccountEmail(confirmRegistration.getVerificationCode(), confirmRegistration.getEmail());
        if (isUserEmail) {
            VerificationToken verificationToken = verificationTokenRepository.findByVerificationCode(confirmRegistration.getVerificationCode());
            Boolean isValid = verificationToken.getExpiryDate().isAfter(LocalDateTime.now());
            if (isValid) {
                UserAccount userAccount = verificationToken.getUserAccount();
                userAccount.setIsEnabled(true);
                userAccount = repository.save(userAccount);
                verificationTokenRepository.delete(verificationToken);
                return new UserAccountResponse(userAccount);
            }
        }
        throw new AccountConfirmationException();
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

    public void existsByCpfOrEmail(String cpf, String email) throws BusinessException {
        if (repository.existsByCpf(cpf)) {
            throw new CpfAlreadyInUseException(cpf);
        }
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }
    }

    public VerificationToken createVerificationCode(UserAccount userAccount) {
        Random generator = new Random();
        String verificationCode = String.valueOf(generator.nextInt(100000,1000000));
        VerificationToken verificationToken = new VerificationToken(verificationCode, userAccount);
        return verificationTokenRepository.save(verificationToken);
    }
}
