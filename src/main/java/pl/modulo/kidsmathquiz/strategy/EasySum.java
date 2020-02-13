package pl.modulo.kidsmathquiz.strategy;

public class EasySum extends Sum implements QuestionAnswersProvider {
    @Override
    protected int getMax() {
        return 10;
    }
}
