package pl.modulo.kidsmathquiz;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class MatiRepository {

    public TreeSet<String> users = new TreeSet<>();

    public void deleteUser(String name) {
        users.remove(name);
    }

    public void addUser(String name) {
        users.add(name);
    }

    public List<String> getUsers() {
        return users.stream().collect(Collectors.toList());
    }
}
