package springboot.jump.question;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.answer.Answer;
import springboot.jump.basetime.BaseTime;
import springboot.jump.user.SiteUser;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Question extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private SiteUser author;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    @Builder
    public Question(String subject, String content, SiteUser siteUser) {
        this.author = siteUser;
        this.content = content;
        this.subject = subject;
    }
}
