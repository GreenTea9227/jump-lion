package springboot.jump.manytomany;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionSiteUserRepository extends JpaRepository<QuestionSiteUser, Long> {
    List<QuestionSiteUser> findByQuestionId(Long questionId);
}
