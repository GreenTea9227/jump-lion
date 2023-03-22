package springboot.jump.question;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.answer.Answer;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.util.resolver.QuestionForm;
import springboot.jump.user.SiteUser;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Question getQuestion(Long id) {
        return questionRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("데이터 없음"));
    }

    public void create(QuestionForm form, SiteUser siteUser) {
        Question question = Question.builder()
                .subject(form.getSubject())
                .content(form.getContent())
                .siteUser(siteUser)
                .build();

        questionRepository.save(question);
    }

    public Page<Question> getList(int page,String kw) {
        PageRequest pageRequest =
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = search(kw);
        return questionRepository.findAll(spec,pageRequest);
    }

    public void modify(Question question ,QuestionForm questionForm) {
        question.setSubject(questionForm.getSubject());
        question.setContent(questionForm.getContent());
        //TODO
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public void vote(Question question,SiteUser siteUser) {
        question.getVoter().add(siteUser);
        questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answers", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
}
