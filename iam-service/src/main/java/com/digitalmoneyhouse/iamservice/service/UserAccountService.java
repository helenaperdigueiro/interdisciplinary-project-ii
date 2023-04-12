package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.client.AccountClient;
import com.digitalmoneyhouse.iamservice.dto.*;
import com.digitalmoneyhouse.iamservice.exception.*;
import com.digitalmoneyhouse.iamservice.model.PasswordResetToken;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.model.VerificationToken;
import com.digitalmoneyhouse.iamservice.repository.RoleRepository;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import com.digitalmoneyhouse.iamservice.util.NullUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Service
public class UserAccountService {
    @Autowired
    private AccountClient accountClient;

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

    @Transactional(rollbackOn = BusinessException.class)
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

    @Transactional(rollbackOn = BusinessException.class)
    public UserAccountResponse confirmRegistration(ConfirmRegistration confirmRegistration) throws BusinessException, URISyntaxException, IOException, InterruptedException {
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
                accountClient.createAccount(userAccount.getId(), userAccount.getFirstName() + " " + userAccount.getLastName(), userAccount.getCpf());
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

    public UserProfile findById(Integer id) throws BusinessException{
        UserAccount userAccount = repository.findById(id).orElseThrow(UserNotFoundException::new);
        return new UserProfile(userAccount);
    }

    public UserAccountResponse editById(Integer id, UserAccountPatch userAccountPatch) throws BusinessException {
        UserAccount userAccount = repository.findById(id).orElseThrow(UserNotFoundException::new);

        NullUtils.updateIfPresent(userAccount::setFirstName, userAccountPatch.getFirstName());
        NullUtils.updateIfPresent(userAccount::setLastName, userAccountPatch.getLastName());
        NullUtils.updateIfPresent(userAccount::setEmail, userAccountPatch.getEmail());
        NullUtils.updateIfPresent(userAccount::setPhoneNumber, userAccountPatch.getPhoneNumber());
        NullUtils.updateIfPresent(userAccount::setPassword, userAccountPatch.getPassword());

        return new UserAccountResponse(repository.save(userAccount));
    }
}
