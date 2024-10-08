package pl.inz.praca.WsbPracaInz.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import pl.inz.praca.WsbPracaInz.auth.model.Role;
import pl.inz.praca.WsbPracaInz.auth.model.User;

import java.util.List;

public interface UserService

{
    User saveUser(User user);

    boolean existsByEmailOrUsername(final String email, final String username);

    Role saveRole(Role role);
    void addRoleToUser(String username, String realName);

    void removeRoleFromUser(String username, String roleName);

    User getUser(String username);
    User getUser(String username, final String email);
    List<User> getUsers();

    UserDetails getUserDetails(String username);
    UserDetails getUserDetails(User user);

}
