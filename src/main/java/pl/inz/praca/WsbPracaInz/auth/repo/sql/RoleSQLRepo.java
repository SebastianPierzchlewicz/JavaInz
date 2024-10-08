package pl.inz.praca.WsbPracaInz.auth.repo.sql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.inz.praca.WsbPracaInz.auth.model.Role;
import pl.inz.praca.WsbPracaInz.auth.repo.RoleRepo;

@Repository
 interface RoleSQLRepo extends RoleRepo, JpaRepository<Role, Long> {

}
