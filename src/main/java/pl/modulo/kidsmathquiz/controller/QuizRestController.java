package pl.modulo.kidsmathquiz.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.modulo.kidsmathquiz.QuizRepository;

@RestController
public class QuizRestController {

	@Autowired
	QuizRepository repository;

	@GetMapping(value = "/rest/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public String getUsers() {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("users", repository.getUsers());
		return jsonObject.toString();
	}

}
