package springboot.jump.aggregate.category;

import jakarta.persistence.*;
import lombok.*;
import springboot.jump.aggregate.question.Question;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class CategoryQuestion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;

    public static CategoryQuestion create(Category category, Question question) {

        CategoryQuestion build = CategoryQuestion.builder()
                .category(category)
                .question(question)
                .build();

        category.getCategoryQuestions().add(build);
        question.getCategoryQuestions().add(build);

        return build;
    }
}
