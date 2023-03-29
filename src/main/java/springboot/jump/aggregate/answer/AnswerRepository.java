package springboot.jump.aggregate.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    @Query("select a from Answer a join fetch Question q where a.question.id = :id order by size(a.voter) DESC ")
    Page<Answer> findWithQuestionId(@Param("id") Long id, Pageable pageable);
}
