package springboot.jump.common.util.resolver;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionForm {
    @NotEmpty(message = "제목은 필수항목입니다.")
    @Size(max = 200)
    private String subject;

    private String categoryName;

    @NotEmpty(message = "내용은 필수항목입니다.")
    private String content;

    public QuestionForm(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
