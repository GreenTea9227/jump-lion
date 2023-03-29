package springboot.jump.aggregate.voter;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerVoterRepository extends JpaRepository<AnswerVoter, Long> {

    @EntityGraph(attributePaths = {"siteUser", "answer"})
    List<AnswerVoter> findByAnswerId(Long AnswerId);
}
