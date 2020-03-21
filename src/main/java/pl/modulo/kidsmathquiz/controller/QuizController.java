package pl.modulo.kidsmathquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import pl.modulo.kidsmathquiz.QuizRepository;
import pl.modulo.kidsmathquiz.QuestionAnswerTimerTask;
import pl.modulo.kidsmathquiz.config.AppConfig;
import pl.modulo.kidsmathquiz.model.MessageIn;
import pl.modulo.kidsmathquiz.model.MessageOut;

import java.util.Timer;

@Controller
public class QuizController {

	@Autowired
	QuizRepository repository;

	@Autowired
	SimpMessagingTemplate messagingTemplate;

	@Autowired
	AppConfig appConfig;

	private final Timer timer = new Timer(true);

	private QuestionAnswerTimerTask timerTask;

	@MessageMapping("/chat")
	public void handleChat(MessageIn msg /*, Principal principal*/) {
		String message = msg.getMessage();
		System.out.println(message+"/"+msg.getSenderName()); // +"/"+principal.getName()
		// TODO apply another way to distinguish an answer, no number parsing
		if (isNumber(message)) {
			boolean ok = timerTask.isAnswerCorrect(msg.getMessage());
			if (ok)
				repository.incScore(msg.getSenderName());
			repository.markAnswered(msg.getSenderName());
			messagingTemplate.convertAndSend("/topic/answer-rating-"+msg.getSenderName(), new MessageOut(ok ? "GOOD" : "WRONG"));
		} else {
			if ("Start".equals(message)) {
				repository.initScores();
				if (timerTask != null)
					timerTask.cancel();
				timerTask = new QuestionAnswerTimerTask(messagingTemplate, repository, appConfig);
				timer.scheduleAtFixedRate(timerTask, 0, appConfig.getAnswerTimeout());
			}
			messagingTemplate.convertAndSend("/topic/chats", new MessageOut(HtmlUtils.htmlEscape(message)));
		}
	}

	private boolean isNumber(String s) {
		return s.matches("\\d+");
	}

	@MessageMapping("/join")
	@SendTo("/topic/names")
	public MessageOut handleJoin(MessageIn msg) {
		repository.addUser(msg.getSenderName());
		return new MessageOut("przyszła: "+msg.getSenderName());
	}

	@MessageMapping("/left")
	@SendTo("/topic/left")
	public MessageOut handleLeft(MessageIn msg) {
		repository.deleteUser(msg.getSenderName());
		return new MessageOut("odeszła: "+msg.getSenderName());
	}

	@MessageExceptionHandler
	@SendTo("/topic/errors")
	public MessageOut handleException(Throwable exception) {
		return new MessageOut("error: "+exception.getMessage());
	}

}
