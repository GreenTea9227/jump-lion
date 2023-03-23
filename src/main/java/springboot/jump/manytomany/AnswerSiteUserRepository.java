package springboot.jump.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerSiteUserRepository extends JpaRepository<AnswerSiteUser, Long> {
    List<AnswerSiteUser> findByAnswerId(Long AnswerId);
}
