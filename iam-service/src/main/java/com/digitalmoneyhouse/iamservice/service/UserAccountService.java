package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.dto.*;
import com.digitalmoneyhouse.iamservice.exception.*;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.repository.RoleRepository;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class UserAccountService {

    @Autowired
    private RoleRepository RoleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserAccountRepository repository;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public GenericSucessResponse save(UserAccountBody userAccountBody) throws BusinessException {
        existsByCpfOrEmail(userAccountBody.getCpf(), userAccountBody.getEmail());
        String encryptedPassword = bCryptPasswordEncoder.encode(userAccountBody.getPassword());
        userAccountBody.setPassword(encryptedPassword);
        UserAccount userModel = new UserAccount(userAccountBody);
        userModel.setRoles(Arrays.asList(RoleRepository.getById(1)));
        userModel = repository.save(userModel);
        VerificationToken verificationToken =  verificationTokenService.create(userModel);
        emailService.sendAccountConfirmationCode(verificationToken);
        return new GenericSucessResponse("Please confirm your account.");
    }

    @Transactional
    public UserAccountResponse confirmRegistration(ConfirmRegistration confirmRegistration) throws BusinessException {
        String verificationCode = confirmRegistration.getVerificationCode();
        Boolean isUserEmail = verificationTokenService.existsByVerificationCodeAndUserAccountEmail(verificationCode, confirmRegistration.getEmail());
        if (isUserEmail) {
            VerificationToken verificationToken = verificationTokenService.findByVerificationCode(verificationCode);
            Boolean isValid = verificationToken.getExpiryDate().isAfter(LocalDateTime.now());
            if (isValid) {
                UserAccount userAccount = verificationToken.getUserAccount();
                userAccount.setIsEnabled(true);
                userAccount = repository.save(userAccount);
                verificationTokenService.deleteById(verificationToken.getId());
                return new UserAccountResponse(userAccount);
            }
        }
        throw new AccountConfirmationException();
    }

    public UserAccount findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public UserAccount findByPasswordResetToken(PasswordResetToken passwordResetToken) {
        return repository.findByToken(passwordResetToken.getToken());
    }

    public GenericSucessResponse changeUserPassword(String resetPasswordToken,PasswordDto passwordDto) throws BusinessException {
        PasswordResetToken passwordResetToken = passwordResetTokenService.validatePasswordResetToken(resetPasswordToken);
        UserAccount user = findByPasswordResetToken(passwordResetToken);
        user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
        repository.save(user);
        passwordResetTokenService.deleteToken(passwordResetToken);
        return new GenericSucessResponse("Your password has been changed successfully.");
    }

    public void existsByCpfOrEmail(String cpf, String email) throws BusinessException {
        if (repository.existsByCpf(cpf)) {
            throw new CpfAlreadyInUseException(cpf);
        }
        if (repository.existsByEmail(email)) {
            throw new EmailAlreadyInUseException(email);
        }
    }

    public GenericSucessResponse resetPassword(String email) {
        UserAccount user = findByEmail(email);
        return passwordResetTokenService.reset(user);
    }
}
