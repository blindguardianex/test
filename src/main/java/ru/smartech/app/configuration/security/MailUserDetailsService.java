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
public class MailUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public MailUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Optional<User> optUser = userService.getByEmail(mail);
        return optUser
                .map(user -> new SecurityUser(mail, user.getPassword(), List.of(new SimpleGrantedAuthority(Roles.USER.name())), user.getId()))
                .orElseThrow(() -> {
                    throw new UsernameNotFoundException("User by mail \"" + mail + "\" not found");
                });
    }
}
