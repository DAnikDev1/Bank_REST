package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails, CredentialsContainer {
    private Long id;
    private String email;
    private String password;
    private Set<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private boolean nonLocked;

    public static UserPrincipal build(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(String.format("ROLE_%s", role.getName())));
                if (role.getPrivileges() != null) {
                    role.getPrivileges().forEach(privilege ->
                            authorities.add(new SimpleGrantedAuthority(privilege.getName())));
                }
            });
        }

        return new UserPrincipal(user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities,
                user.getEnabled(),
                user.getNonLocked());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return nonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void eraseCredentials() {
        password = null;
    }
    public List<String> getAuthoritiesStrings() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
