package springboot.jump.category;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.jump.question.Question;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id @GeneratedValue
    private Long id;

    private String categoryName;

    @OneToMany(mappedBy = "category")
    List<CategoryQuestion> categoryQuestions = new ArrayList<>();

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
