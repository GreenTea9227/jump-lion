package springboot.jump.answer;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.common.basetime.BaseTime;
import springboot.jump.manytomany.AnswerSiteUser;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;

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

    //    private LocalDateTime createDate;
//    @ManyToMany
//    private Set<SiteUser> voter;

    @OneToMany(mappedBy = "answer")
    private Set<AnswerSiteUser> voter = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
