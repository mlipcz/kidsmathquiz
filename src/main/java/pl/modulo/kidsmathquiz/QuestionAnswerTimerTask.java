package pl.modulo.kidsmathquiz;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.modulo.kidsmathquiz.config.AppConfig;
import pl.modulo.kidsmathquiz.model.MessageOut;
import pl.modulo.kidsmathquiz.model.QuestionAnswersMessageOut;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswers;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswersProvider;

import java.util.TimerTask;

public class QuestionAnswerTimerTask extends TimerTask {

	final private SimpMessagingTemplate messagingTemplate;
	final private QuestionAnswersProvider questionAnswersProvider;
	final private QuizRepository repository;
	private int questionCount, answerCount;
	private QuestionAnswers qa = null;

	public QuestionAnswerTimerTask(SimpMessagingTemplate messagingTemplate, QuizRepository repository, AppConfig appConfig) {
		this.messagingTemplate = messagingTemplate;
		this.questionAnswersProvider = appConfig.getQuestionAnswersProvider();
		this.repository = repository;
		this.questionCount = appConfig.getQuestionCount();
		this.answerCount = appConfig.getAnswerCount();
	}

	@Override
	public void run() {
		sendTimeoutToUserWithoutAnswer();
		if (--questionCount < 0) {
			String score = repository.getScores();
				messagingTemplate.convertAndSend("/topic/scores", new MessageOut(score));
			cancel();
			return;
		}
		qa = questionAnswersProvider.giveQuestionAnswers(answerCount);
		repository.resetPendingAnswers();
		messagingTemplate.convertAndSend("/topic/questions", new QuestionAnswersMessageOut(qa.getQuestion(), qa.getAnswers()));
	}

	private void sendTimeoutToUserWithoutAnswer() {
		for (String user : repository.getUsersWithoutAnswer()) {
			messagingTemplate.convertAndSend("/topic/answer-rating-"+user, new MessageOut("WRONG"));
		}
	}

	public boolean isAnswerCorrect(String answer) {
		return qa != null && qa.getCorrectAnswer() != null && qa.getCorrectAnswer().equals(answer);
	}
}
