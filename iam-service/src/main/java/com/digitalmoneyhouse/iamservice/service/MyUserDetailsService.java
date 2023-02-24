package com.digitalmoneyhouse.iamservice.service;

import com.digitalmoneyhouse.iamservice.model.Role;
import com.digitalmoneyhouse.iamservice.model.UserAccount;
import com.digitalmoneyhouse.iamservice.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserAccount userAccount = userAccountRepository.findByEmailAndIsEnabled(username, true);

        if (userAccount == null) {
            throw new UsernameNotFoundException("User not found or account not confirmed");
        }

        Set<GrantedAuthority> grantList = new HashSet<GrantedAuthority>();
        for (Role role: userAccount.getRoles()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(role.getName());
            grantList.add(grantedAuthority);
        }
        UserDetails user = null;
        user = (UserDetails) new User(username, userAccount.getPassword(), grantList);
        return user;
    }
}
