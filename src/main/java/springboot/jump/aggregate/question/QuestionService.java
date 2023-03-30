package springboot.jump.aggregate.question;

import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.category.Category;
import springboot.jump.aggregate.category.CategoryQuestion;
import springboot.jump.aggregate.category.CategoryQuestionRepository;
import springboot.jump.aggregate.category.CategoryRepository;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.voter.AnswerVoterRepository;
import springboot.jump.aggregate.voter.QuestionVoter;
import springboot.jump.aggregate.voter.QuestionVoterRepository;
import springboot.jump.exception.DataNotFoundException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionVoterRepository questionVoterRepository;
    private final AnswerVoterRepository answerVoterRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryQuestionRepository cateGoryQuestionRepository;

    public List<Question> getList() {
        return questionRepository.findAll();
    }

    public Page<Question> getList(int page) {

        PageRequest pageRequest =
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageRequest);
    }

    public Question getQuestion(Long id) {
        Question question = questionRepository.findWithQuestionId(id);

        if (question == null) {
            throw new DataNotFoundException("데이터 없음");
        }

        return question;
    }

    public void create(QuestionForm form, SiteUser siteUser) {

        String categoryName = form.getCategoryName();
        String[] split = new String[0];

        if (categoryName != null) {
            split = categoryName.split(",");
        }

        Question question = Question.builder()
                .subject(form.getSubject())
                .content(form.getContent())
                .siteUser(siteUser)
                .build();

        for (String s : split) {
            Category category = new Category(s);
            categoryRepository.save(category);

            CategoryQuestion categoryQuestion = CategoryQuestion.create(category, question);
            cateGoryQuestionRepository.save(categoryQuestion);
        }

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
        List<QuestionVoter> listQuestion = questionVoterRepository.findByQuestionId(question.getId());

        Optional<QuestionVoter> find =
                listQuestion.stream().filter(e -> e.getSiteUser().equals(siteUser)).findAny();
        //추천한 적 없다면 save
        if (find.isEmpty()) {
            QuestionVoter newQuestionsiteUser = QuestionVoter
                    .builder()
                    .siteUser(siteUser)
                    .question(question)
                    .build();
            questionVoterRepository.save(newQuestionsiteUser);

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

    public void increaseVisitCount(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(()
                -> new DataNotFoundException(" 해당 id를 가진 질문이 없습니다."));
        Long totalVisit = question.getTotalVisit();
        question.setTotalVisit(totalVisit + 1);
    }
}
