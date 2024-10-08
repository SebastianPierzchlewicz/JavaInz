package pl.inz.praca.WsbPracaInz.auth.projection.view;

import lombok.Getter;
import pl.inz.praca.WsbPracaInz.auth.model.Role;
import pl.inz.praca.WsbPracaInz.auth.model.User;

import java.util.List;

@Getter
public class UserViewModel
{
    private final long id;
    private final String email;
    private final String username;
    private final boolean verification;
    private final boolean banned;
    private final boolean firstJoin;
    private final boolean takeForm;
    private final List<String> roles;


    public UserViewModel(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.verification = user.isVerification();
        this.banned = user.isBanned();
        this.roles = user.getRoles().stream().map(Role::getName).toList();
        this.firstJoin = user.isFirstJoin();
        this.takeForm = user.isTakeForm();
    }
}
