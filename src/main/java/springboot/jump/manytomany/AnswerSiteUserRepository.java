package springboot.jump.manytomany;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerSiteUserRepository extends JpaRepository<AnswerSiteUser, Long> {

    @EntityGraph(attributePaths = {"siteUser","answer"})
    List<AnswerSiteUser> findByAnswerId(Long AnswerId);
}
