package springboot.jump.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import springboot.jump.answer.Answer;
import springboot.jump.answer.AnswerForm;
import springboot.jump.answer.AnswerService;
import springboot.jump.user.SiteUser;
import springboot.jump.user.UserService;
import springboot.jump.common.util.resolver.CreateQuestion;
import springboot.jump.common.util.resolver.QuestionForm;

import java.security.Principal;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "") String kw) {

        Page<Question> paging = questionService.getList(page, kw);
        model.addAttribute("paging", paging);

        return "question/question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Long id, AnswerForm answerForm
            ,@RequestParam(value = "answerPage",defaultValue = "0") int answerPage) {
        Question question = questionService.getQuestion(id);
        Page<Answer> answers = answerService.findAnswerByQuestion(id, answerPage);
        model.addAttribute("question", question);
        model.addAttribute("answers",answers);
        return "question/question_detail";
    }

    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question/question_form";
    }

    @PostMapping("/create")
    public String questionCreate(@Validated @CreateQuestion QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question/question_form";
        }

        SiteUser siteUser = userService.getUser(principal.getName());

        questionService.create(questionForm, siteUser);
        return "redirect:/question/list";
    }

    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable Long id,
                                 @AuthenticationPrincipal User auth) {
        log.info("authentication ={}", auth);
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(auth.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question/question_form";
    }

    @PostMapping("/modify/{id}")
    public String questionModify(@Validated QuestionForm questionForm, BindingResult bindingResult,
                                 @PathVariable Long id, UsernamePasswordAuthenticationToken auth) {

        log.info("authentication ={}", auth);
        if (bindingResult.hasErrors()) {
            return "question/question_form";
        }
        Question question = questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionService.modify(question, questionForm);
        return "redirect:/question/detail/{id}";
    }

    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable Long id) {
        Question question = questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        questionService.delete(question);
        return "redirect:/";
    }

    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable Long id) {

        SiteUser user = userService.getUser(principal.getName());
        questionService.vote(id, user);
        return String.format("redirect:/question/detail/%s", id);
    }
}
