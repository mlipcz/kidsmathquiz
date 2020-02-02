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
import pl.modulo.kidsmathquiz.model.MessageIn;
import pl.modulo.kidsmathquiz.model.MessageOut;
import pl.modulo.kidsmathquiz.strategy.QuestionAnswersProvider;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

@Controller
public class QuizController {

    @Autowired
    MatiRepository repository;

    @Autowired
    SimpMessagingTemplate messagingTemplate;

    @Resource
    QuestionAnswersProvider questionAnswersProvider;

    private final int PERIOD = 5000; // in ms
    private final Timer timer = new Timer(true);

    private QuestionAnswerTimerTask timerTask;

    private Map<String, Integer> scores = new HashMap<>();

    @MessageMapping("/chat")
    @SendTo("/topic/chats")
    public MessageOut handleChat(MessageIn msg) {
        String message = msg.getMessage();
        System.out.println(message+"/"+msg.getSenderName());
        if ("Start".equals(message)) {
            if (timerTask != null)
                timerTask.cancel();
            timerTask = new QuestionAnswerTimerTask(messagingTemplate, questionAnswersProvider);
            timer.scheduleAtFixedRate(timerTask, 0, PERIOD);
        } else if (isNumber(message)) {
            boolean ok = timerTask.isAnswerCorrect(msg.getMessage());
            scores.put(msg.getSenderName(), scores.getOrDefault(msg.getSenderName(), 0) + (ok ? 0 : 1));
            return new MessageOut(ok ? "GOOD" : "WRONG");
        }
        return new MessageOut(HtmlUtils.htmlEscape(message));
    }

    private boolean isNumber(String s) {
        return s.matches("\\d+");
    }

    @MessageMapping("/join")
    @SendTo("/topic/names")
    public MessageOut handleJoin(MessageIn name) {
        repository.addUser(name.getSenderName());
        return new MessageOut("przyszła: "+name.getMessage());
    }

    @MessageMapping("/left")
    @SendTo("/topic/left")
    public MessageOut handleLeft(MessageIn msg) {
        repository.deleteUser(msg.getSenderName());
        return new MessageOut("odeszła: "+msg.getMessage());
    }

    @MessageExceptionHandler
    @SendTo("/topic/errors")
    public MessageOut handleException(Throwable exception) {
        return new MessageOut("error: "+exception.getMessage());
    }

}
