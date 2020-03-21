package pl.modulo.kidsmathquiz;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class QuizRepository {

	public TreeSet<String> users = new TreeSet<>();
	public Set<String> pendingAnswers = new HashSet<>();
	private Map<String, Integer> scores = new HashMap<>();

	public void deleteUser(String name) {
		users.remove(name);
	}

	public void addUser(String name) {
		users.add(name);
	}

	public List<String> getUsers() {
		return users.stream().collect(Collectors.toList());
	}

	public void resetPendingAnswers() {
		pendingAnswers = new HashSet<>(users);
	}

	public void markAnswered(String name) {
		pendingAnswers.remove(name);
	}

	public Collection<String> getUsersWithoutAnswer() {
		return Collections.unmodifiableCollection(this.pendingAnswers);
	}

	public void initScores() {
		scores = new HashMap<>();
		users.forEach(name -> scores.put(name, 0));
	}

	public void incScore(String name) {
		scores.put(name, scores.getOrDefault(name, 0) + 1);
	}

	public String getScores() {
		return scores.entrySet().stream().map(e -> e.getKey()+" : "+e.getValue()).collect(Collectors.joining("<br/>"));
	}

}
