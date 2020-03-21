package pl.modulo.kidsmathquiz.strategy;

public class EasyMulti extends Multi implements QuestionAnswersProvider {

	protected int getMax() {
		return 5;
	}
}
