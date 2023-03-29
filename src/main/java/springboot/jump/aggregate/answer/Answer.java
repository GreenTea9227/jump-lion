package springboot.jump.aggregate.answer;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.aggregate.voter.AnswerVoter;
import springboot.jump.common.basetime.BaseTime;

import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@Entity
public class Answer extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    private SiteUser author;

    @Builder.Default
    @OneToMany(mappedBy = "answer")
    private Set<AnswerVoter> voter = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
