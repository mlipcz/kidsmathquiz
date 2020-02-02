package pl.modulo.kidsmathquiz.strategy;

public class MidSum extends Sum implements QuestionAnswersProvider {

    @Override
    protected int getMax() {
        return 20;
    }
}
