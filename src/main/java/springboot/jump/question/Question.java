package springboot.jump.question;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.answer.Answer;
import springboot.jump.basetime.BaseTime;
import springboot.jump.manytomany.QuestionSiteUser;
import springboot.jump.user.SiteUser;

import java.util.*;

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

//    @ManyToMany
//    private Set<SiteUser> voter;

    @OneToMany(mappedBy = "question")
    private Set<QuestionSiteUser> voter = new HashSet<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers = new ArrayList<>();

    @Builder
    public Question(String subject, String content, SiteUser siteUser) {
        this.author = siteUser;
        this.content = content;
        this.subject = subject;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(getId(), question.getId()) && Objects.equals(getSubject(), question.getSubject()) && Objects.equals(getContent(), question.getContent()) && Objects.equals(getAuthor(), question.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getSubject(), getContent(), getAuthor());
    }
}
