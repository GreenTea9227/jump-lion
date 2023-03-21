package springboot.jump.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;

import java.util.Optional;

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

    public Answer getAnswer(Long id) {
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if (optionalAnswer.isEmpty()) {
            throw new DataNotFoundException("answer not found");
        }
        return optionalAnswer.get();
    }

    public Long modify(Answer answer,String content) {
        answer.setContent(content);
        Answer save = answerRepository.save(answer);
        return save.getQuestion().getId();
    }

    public void delete(Long id) {
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if (optionalAnswer.isEmpty()) {
            throw new DataNotFoundException("answer not found");
        }

        answerRepository.delete(optionalAnswer.get());
    }
}
