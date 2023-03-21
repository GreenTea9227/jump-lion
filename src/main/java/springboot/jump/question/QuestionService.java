package springboot.jump.question;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.resolver.QuestionForm;
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

    public Page<Question> getList(int page) {
        PageRequest pageRequest =
                PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createDate")));
        return questionRepository.findAll(pageRequest);
    }
}
