package springboot.jump.aggregate.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserService;
import springboot.jump.aggregate.voter.AnswerVoter;
import springboot.jump.aggregate.voter.AnswerVoterRepository;
import springboot.jump.exception.DataNotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final AnswerVoterRepository answerVoterRepository;

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

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answerRepository.save(answer);
    }

    public void delete(Long id) {
        Optional<Answer> optionalAnswer = answerRepository.findById(id);
        if (optionalAnswer.isEmpty()) {
            throw new DataNotFoundException("answer not found");
        }

        answerRepository.delete(optionalAnswer.get());
    }

    public Long vote(Long id, Principal principal) {

        Answer answer = answerRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("해당 answer 데이터 없음"));

        SiteUser user = userService.getUser(principal.getName());

        List<AnswerVoter> answerList = answerVoterRepository.findByAnswerId(id);

        Optional<AnswerVoter> find = answerList.stream()
                .filter(e -> e.getSiteUser().equals(user)).findAny();

        if (find.isEmpty()) {
            AnswerVoter answerVoter = AnswerVoter
                    .builder()
                    .answer(answer)
                    .siteUser(user)
                    .build();
            answerVoterRepository.save(answerVoter);
            answer.getVoter().add(answerVoter);
        }

        return answer.getQuestion().getId();
    }

    public Page<Answer> findAnswerByQuestion(Long questionId, int page) {
        PageRequest pageRequest = PageRequest.of(page, 5);
        return answerRepository.findWithQuestionId(questionId, pageRequest);
    }
}
