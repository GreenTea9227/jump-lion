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
import springboot.jump.manytomany.AnswerSiteUserRepository;
import springboot.jump.manytomany.QuestionSiteUser;
import springboot.jump.manytomany.QuestionSiteUserRepository;
import springboot.jump.user.SiteUser;
import springboot.jump.util.resolver.QuestionForm;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionSiteUserRepository questionSiteUserRepository;
    private final AnswerSiteUserRepository answerSiteUserRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Page<Question> getList(int page) {

        PageRequest pageRequest =
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageRequest);
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

    public Page<Question> getList(int page, String kw) {
        PageRequest pageRequest =
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        Specification<Question> spec = search(kw);
        return questionRepository.findAll(spec, pageRequest);
    }

    public void modify(Question question, QuestionForm questionForm) {
        question.setSubject(questionForm.getSubject());
        question.setContent(questionForm.getContent());
        //TODO
        questionRepository.save(question);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public void vote(Long id, SiteUser siteUser) {
        Question question = questionRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("데이터 없음"));

        //questionId로 찾기

        List<QuestionSiteUser> listQuestion = questionSiteUserRepository.findByQuestionId(question.getId());

        Optional<QuestionSiteUser> find =
                listQuestion.stream().filter(e -> e.getSiteUser().equals(siteUser)).findAny();
        //추천한 적 없다면 save
        if (find.isEmpty()) {
            QuestionSiteUser newQuestionsiteUser = QuestionSiteUser
                    .builder()
                    .siteUser(siteUser)
                    .question(question)
                    .build();
            questionSiteUserRepository.save(newQuestionsiteUser);

            question.getVoter().add(newQuestionsiteUser);
        }
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
