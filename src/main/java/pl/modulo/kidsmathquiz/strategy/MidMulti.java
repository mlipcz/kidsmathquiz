package pl.modulo.kidsmathquiz.strategy;

import org.springframework.stereotype.Component;

public class MidMulti extends Multi implements QuestionAnswersProvider {

    public MidMulti() {
        System.out.println("mid multi");
    }

    protected int getMax() {
        return 10;
    }
}
