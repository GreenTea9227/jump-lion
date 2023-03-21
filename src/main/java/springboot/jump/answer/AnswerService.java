package springboot.jump.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Question question, String content, SiteUser siteUser) {
        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .author(siteUser)
                .build();

        question.getAnswers().add(answer);

        answerRepository.save(answer);
    }
}
