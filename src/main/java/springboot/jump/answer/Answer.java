package springboot.jump.answer;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.basetime.BaseTime;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;

import java.time.LocalDateTime;

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

    private LocalDateTime createDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
