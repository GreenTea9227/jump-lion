package springboot.jump.aggregate.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SiteUser, Long> {

    Optional<SiteUser> findByUsername(String username);

    SiteUser findByEmail(String email);
}
