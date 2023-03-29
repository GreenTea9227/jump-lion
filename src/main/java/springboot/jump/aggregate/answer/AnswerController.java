package springboot.jump.aggregate.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.question.QuestionService;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.user.UserService;
import springboot.jump.security.oauth2.PrincipalUser;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/answer")
@Controller
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable Long id, @Validated AnswerForm answerForm
            , BindingResult bindingResult, Principal principal) {

        Question question = questionService.getQuestion(id);

        if (bindingResult.hasErrors()) {
            return "redirect:/question/question_detail/"+question.getId();
        }

        SiteUser siteUser = userService.getUser(principal.getName());

        Answer answer = answerService.create(question, answerForm.getContent(), siteUser);

        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable Long id, Principal principal) {
        Answer answer = answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer/answer_form";
    }

    @PostMapping("/modify/{id}")
    public String answerModify(@Validated AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "answer/answer_form";
        }

        Answer answer = answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        answerService.modify(answer, answerForm.getContent());

        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Principal principal) {

        Answer answer = answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        answerService.delete(id);

        return String.format("redirect:/question/detail/%s#answer_%s",
                answer.getQuestion().getId(), answer.getId());
    }

    @GetMapping("/vote/{id}")
    public String vote(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        Long questionId = answerService.vote(id, principal);

        redirectAttributes.addAttribute("id", questionId);
        return "redirect:/question/detail/{id}";
    }
}
