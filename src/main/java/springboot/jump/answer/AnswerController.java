package springboot.jump.answer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import springboot.jump.exception.DataNotFoundException;
import springboot.jump.question.Question;
import springboot.jump.question.QuestionService;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserService;

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
        SiteUser siteUser = userService.getUser(principal.getName());

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            return "question_detail";
        }

        //TODO 답변 저장
        answerService.create(question, answerForm.getContent(), siteUser);
        return "redirect:/question/detail/{id}";
    }

    @GetMapping("/modify/{id}")
    public String answerModify(AnswerForm answerForm, @PathVariable Long id,Principal principal) {
        Answer answer = answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }
        answerForm.setContent(answer.getContent());
        return "answer_form";
    }

    @PostMapping("/modify/{id}")
    public String answerModify(@Validated AnswerForm answerForm, BindingResult bindingResult,
                               @PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }

        Answer answer = answerService.getAnswer(id);
        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"수정권한이 없습니다.");
        }

        Long questionId = answerService.modify(answer, answerForm.getContent());
        redirectAttributes.addAttribute("questionId",questionId);
        return "redirect:/question/detail/{questionId}";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id,Principal principal,RedirectAttributes redirectAttributes) {

        Answer answer = answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제권한이 없습니다.");
        }

        answerService.delete(id);

        Long questionId = answer.getQuestion().getId();
        redirectAttributes.addAttribute("questionId",questionId);
        return "redirect:/question/detail/{questionId}";
    }





}
