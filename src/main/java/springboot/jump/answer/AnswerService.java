package springboot.jump.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.manytomany.AnswerSiteUser;
import springboot.jump.manytomany.AnswerSiteUserRepository;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final AnswerSiteUserRepository answerSiteUserRepository;

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

    public Long modify(Answer answer, String content) {
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

        Answer answer = answerRepository
                .findById(id)
                .orElseThrow(() -> new DataNotFoundException("데이터 없음"));

        SiteUser user = userService.getUser(principal.getName());

        List<AnswerSiteUser> answerList = answerSiteUserRepository.findByAnswerId(id);

        Optional<AnswerSiteUser> find = answerList.stream()
                .filter(e -> e.getSiteUser().equals(user)).findAny();

        if (find.isEmpty()) {
            AnswerSiteUser answerSiteUser = AnswerSiteUser
                    .builder()
                    .answer(answer)
                    .siteUser(user)
                    .build();
            answerSiteUserRepository.save(answerSiteUser);
            answer.getVoter().add(answerSiteUser);
        }

        return answer.getQuestion().getId();
    }
}
