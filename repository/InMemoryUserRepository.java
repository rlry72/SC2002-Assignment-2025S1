package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.User;
import model.CompanyRepresentative;
import model.Student;
import model.Staff;

public class InMemoryUserRepository implements UserRepository{
    private final Map<String, User> users = new HashMap<>();

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    public boolean exists(String id) {
        return users.containsKey(id);
    }

    public List<Student> getAllStudents() {
        return users.values().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .toList();        
    }

    public List<CompanyRepresentative> getAllCompanyRepresentatives() {
        return users.values().stream()
            .filter(u -> u instanceof CompanyRepresentative)
            .map(u -> (CompanyRepresentative) u)
            .toList();
    }

    public List<Staff> getAllStaffs() {
        return users.values().stream()
            .filter(u -> u instanceof Staff)
            .map(u -> (Staff) u)
            .toList();
    }
    
}
