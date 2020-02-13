package pl.modulo.kidsmathquiz.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import pl.modulo.kidsmathquiz.MatiRepository;
import pl.modulo.kidsmathquiz.QuestionAnswerTimerTask;
import pl.modulo.kidsmathquiz.config.AppConfig;
import pl.modulo.kidsmathquiz.model.MessageIn;
import pl.modulo.kidsmathquiz.model.MessageOut;
import pl.modulo.kidsmathquiz.model.MessageOutOneRecipient;
import pl.modulo.kidsmathquiz.model.QuestionAnswersMessageOut;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Controller
public class QuizController {

    @Autowired
    MatiRepository repository;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Autowired
    AppConfig appConfig;

    private final Timer timer = new Timer(true);

    private QuestionAnswerTimerTask timerTask;

    private Map<String, Integer> scores = new HashMap<>();

    @MessageMapping("/chat")
    public void handleChat(MessageIn msg /*, Principal principal*/) {
        String message = msg.getMessage();
        System.out.println(message+"/"+msg.getSenderName()); // +"/"+principal.getName()
        if (isNumber(message)) {
            boolean ok = timerTask.isAnswerCorrect(msg.getMessage());
            scores.put(msg.getSenderName(), scores.getOrDefault(msg.getSenderName(), 0) + (ok ? 0 : 1));
            messagingTemplate.convertAndSend("/topic/answer-rating-"+msg.getSenderName(), new MessageOutOneRecipient(ok ? "GOOD" : "WRONG", msg.getSenderName()));
        } else {
            if ("Start".equals(message)) {
                if (timerTask != null)
                    timerTask.cancel();
                timerTask = new QuestionAnswerTimerTask(messagingTemplate, appConfig.getQuestionAnswersProvider());
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
