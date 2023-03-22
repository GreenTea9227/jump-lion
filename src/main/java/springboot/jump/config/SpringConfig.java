package springboot.jump.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springboot.jump.question.Question;
import springboot.jump.question.QuestionRepository;
import springboot.jump.util.resolver.QuestionResolver;

import java.util.List;

@Profile("real")
@Configuration
public class SpringConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new QuestionResolver());
    }

    @Bean
    CommandLineRunner applicationStart(QuestionRepository questionRepository) {
        return args -> {
            for (int i = 0; i < 100; i++) {
                questionRepository.save(Question.builder()
                        .subject("question" + i)
                        .content("content" + i).build());
            }
        };
    }
}
