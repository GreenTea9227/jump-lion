package springboot.jump.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerException {

    @ExceptionHandler(DataNotFoundException.class)
    public String notFound(DataNotFoundException exception, Model model) {
        model.addAttribute("exception", exception.getMessage());
        return "exp";
    }
}
