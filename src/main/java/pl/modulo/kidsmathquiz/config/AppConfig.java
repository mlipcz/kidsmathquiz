package pl.modulo.kidsmathquiz.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.modulo.kidsmathquiz.strategy.MidSum;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswersProvider;

@Configuration
public class AppConfig {
	@Bean
	public QuestionAnswersProvider getQuestionAnswersProvider() {
		//return new MidMulti();
		return new MidSum();
	}

	public int getAnswerTimeout() {
		return 7000;
	}

	public int getQuestionCount() {
		return 3;
	}

	public int getAnswerCount() {
		return 5;
	}

}
