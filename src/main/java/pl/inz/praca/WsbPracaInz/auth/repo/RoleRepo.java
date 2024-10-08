package pl.inz.praca.WsbPracaInz.auth.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import pl.inz.praca.WsbPracaInz.auth.model.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);

}
