package springboot.jump.aggregate.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Question findBySubject(String subject);

    Question findBySubjectAndContent(String subject, String content);

    List<Question> findBySubjectLike(String subject);

    Page<Question> findAll(Pageable pageable);

    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    @Query("select q from Question q where q.id = :id")
    Question findWithQuestionId(@Param("id") Long id);

    @Query("select distinct q from Question q left join SiteUser s on q.author = s " +
            "left join Answer a on a.question = q " +
            "left join SiteUser s2 on a.author = s2 " +
            "where " +
            "q.subject like %:kw% " +
            "or q.content like %:kw% " +
            "or s.username like %:kw% " +
            "or a.content like %:kw% " +
            "or s2.username like %:kw% ")
    Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);
}
