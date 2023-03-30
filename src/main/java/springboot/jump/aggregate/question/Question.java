package springboot.jump.aggregate.question;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.aggregate.answer.Answer;
import springboot.jump.aggregate.category.CategoryQuestion;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.voter.QuestionVoter;
import springboot.jump.common.basetime.BaseTime;

import java.util.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    private Long totalVisit;
    @ManyToOne
    private SiteUser author;

    @OneToMany(mappedBy = "question")
    private Set<QuestionVoter> voter = new HashSet<>();

    @OneToMany(mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<CategoryQuestion> categoryQuestions = new ArrayList<>();

    @Builder
    public Question(String subject, String content, SiteUser siteUser) {
        this.author = siteUser;
        this.content = content;
        this.subject = subject;
        this.totalVisit = 0L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return Objects.equals(getSubject(), question.getSubject()) && Objects.equals(getContent(), question.getContent()) && Objects.equals(getTotalVisit(), question.getTotalVisit()) && Objects.equals(getAuthor(), question.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getContent(), getTotalVisit(), getAuthor());
    }
}
