package pl.modulo.kidsmathquiz.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.modulo.kidsmathquiz.MatiRepository;


@RestController
public class MatiRestController {

    @Autowired
    MatiRepository repository;

    @GetMapping(value = "/rest/users", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getUsers() {
        JSONObject o = new JSONObject();
        o.put("users", repository.getUsers());
        return o.toString();
    }

}
