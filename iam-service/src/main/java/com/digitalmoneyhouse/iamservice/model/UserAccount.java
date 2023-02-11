package com.digitalmoneyhouse.iamservice.model;

import com.digitalmoneyhouse.iamservice.dto.UserAccountBody;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Entity
@Getter @Setter
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    private String firstName;

    @Column(length = 100, nullable = false)
    private String lastName;

    @Column(length = 100, nullable = false)
    private String cpf;

    @Column(length = 100, nullable = false)
    private String phoneNumber;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    private String cvu;

    private String alias;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "userAccount_role", joinColumns = @JoinColumn(name = "userAccount_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnoreProperties("userAccounts")
    private List<Role> roles;

    public UserAccount() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public UserAccount(UserAccountBody user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.cpf = user.getCpf();
        this.phoneNumber = user.getPhoneNumber();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.cvu = generateCVU();
        this.alias = generateAlias();
    }

    public String generateCVU() {
        Random random = new Random();
        String cvu = "";
        for (int i = 0; i < 22; i++) {
            cvu += random.nextInt(10);
        }
        return cvu;
    }

    public String generateAlias() {
        Random random = new Random();
        int randomNumber = random.nextInt(1000);
        return StringUtils.trimAllWhitespace(this.getFirstName() + this.getLastName() + randomNumber);
    }
}
