package pl.modulo.kidsmathquiz;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import pl.modulo.kidsmathquiz.model.RichMessageOut;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswers;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswersProvider;

import java.util.TimerTask;

public class QuestionAnswerTimerTask extends TimerTask {

    final private int ANSWERS_COUNT = 5;

    final private SimpMessagingTemplate messagingTemplate;
    final private QuestionAnswersProvider questionAnswersProvider;
    private int counter = 5;
    private QuestionAnswers qa = null;

    public QuestionAnswerTimerTask(SimpMessagingTemplate messagingTemplate, QuestionAnswersProvider questionAnswersProvider) {
        this.messagingTemplate = messagingTemplate;
        this.questionAnswersProvider = questionAnswersProvider;
    }

    @Override
    public void run() {
        qa = questionAnswersProvider.giveQuestionAnswers(ANSWERS_COUNT);
        messagingTemplate.convertAndSend("/topic/questions", new RichMessageOut(qa.getQuestion(), qa.getAnswers()));
        if (--this.counter == 0)
            this.cancel();
    }

    public boolean isAnswerCorrect(String answer) {
        return qa != null && qa.getCorrectAnswer() != null && qa.getCorrectAnswer().equals(answer);
    }
}
