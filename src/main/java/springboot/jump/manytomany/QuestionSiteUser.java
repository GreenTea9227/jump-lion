package springboot.jump.manytomany;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.jump.common.basetime.BaseTime;
import springboot.jump.question.Question;
import springboot.jump.user.SiteUser;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class QuestionSiteUser extends BaseTime {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_user_id")
    private SiteUser siteUser;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;
}
