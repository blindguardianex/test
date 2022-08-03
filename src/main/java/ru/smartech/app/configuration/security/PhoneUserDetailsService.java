package ru.smartech.app.configuration.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.smartech.app.entity.User;
import ru.smartech.app.enums.Roles;
import ru.smartech.app.service.UserService;

import java.util.List;
import java.util.Optional;

@Component
public class PhoneUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public PhoneUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Optional<User> optUser = userService.getByPhone(phone);
        return optUser
                .map(user -> new SecurityUser(phone, user.getPassword(), List.of(new SimpleGrantedAuthority(Roles.USER.name())), user.getId()))
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User by phone \"" + phone + "\" not found");
                });
    }
}
