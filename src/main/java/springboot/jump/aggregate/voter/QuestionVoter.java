package springboot.jump.aggregate.voter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.jump.aggregate.question.Question;
import springboot.jump.aggregate.user.SiteUser;
import springboot.jump.common.basetime.BaseTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class QuestionVoter extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_user_id")
    private SiteUser siteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
