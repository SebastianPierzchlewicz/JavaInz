package pl.inz.praca.WsbPracaInz.auth.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.auth.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String name);
    User findByUsernameOrEmail(String name, String email);

    boolean existsByEmailOrUsername(final String email, final String username);

}
