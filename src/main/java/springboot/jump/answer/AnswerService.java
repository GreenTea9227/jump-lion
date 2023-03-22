package springboot.jump.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserService;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final UserService userService;

    public Answer create(Question question, String content, SiteUser siteUser) {
        Answer answer = Answer.builder()
                .content(content)
                .question(question)
                .author(siteUser)
                .build();

        question.getAnswers().add(answer);

        answerRepository.save(answer);
        return answer;
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

    public Long vote(Long id, Principal principal) {

        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if (optionalAnswer.isEmpty()) {
            throw new DataNotFoundException("데이터 없음");
        }
        Answer answer = optionalAnswer.get();

        SiteUser user = userService.getUser(principal.getName());
        answer.getVoter().add(user);

        return answer.getQuestion().getId();
    }
}
