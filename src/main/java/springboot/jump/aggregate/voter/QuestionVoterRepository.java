package springboot.jump.aggregate.voter;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionVoterRepository extends JpaRepository<QuestionVoter, Long> {

    @EntityGraph(attributePaths = {"question", "siteUser"})
    List<QuestionVoter> findByQuestionId(Long questionId);
}
