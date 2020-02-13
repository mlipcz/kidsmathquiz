package pl.modulo.kidsmathquiz.strategy;

import java.util.Random;
import java.util.TreeSet;
import java.util.stream.Collectors;

public abstract class Multi implements QuestionAnswersProvider {
    final private Random rnd = new Random();

    @Override
    public QuestionAnswers giveQuestionAnswers(int answersCount) {
        final int max = getMax();
        int x = rnd.nextInt(max - 1) + 2;
        int y = rnd.nextInt(max - 1) + 2;
        TreeSet<Integer> answers = new TreeSet<>();
        answers.add(x * y);
        while (answers.size() < answersCount) {
            answers.add(rnd.nextInt(max * max - 3) + 4);
        }
        QuestionAnswers qa = new QuestionAnswers();
        qa.setAnswers(answers.stream().map(String::valueOf).collect(Collectors.toList()));
        qa.setQuestion(x + " &times; " + y);
        qa.setCorrectAnswer("" + x * y);
        return qa;
    }

    protected abstract int getMax();
}
