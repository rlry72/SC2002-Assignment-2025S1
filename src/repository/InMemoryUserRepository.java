package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.User;
import model.CompanyRepresentative;
import model.Student;
import model.Staff;

/**
 * in-memory implementation of UserRepository
 * stores User objects using a hash map keyed by user ID
 * data is volatile and lost when application terminates
 */
public class InMemoryUserRepository implements UserRepository {

    /** map storing user records keyed by user ID */
    private final Map<String, User> users = new HashMap<>();

    /**
     * find user by unique system-assigned user ID
     * @param id user identifier
     * @return optional containing user if found, else empty
     */
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    /**
     * save or update a user record
     * if the ID already exists, record is replaced
     * @param user user object to store
     */
    @Override
    public void save(User user) {
        users.put(user.getUserId(), user);
    }

    /**
     * check if a given ID exists in storage
     * @param id user identifier to test
     * @return true if ID exists, else false
     */
    @Override
    public boolean exists(String id) {
        return users.containsKey(id);
    }

    /**
     * retrieve all stored student accounts
     * @return list of Student objects
     */
    @Override
    public List<Student> getAllStudents() {
        return users.values().stream()
            .filter(u -> u instanceof Student)
            .map(u -> (Student) u)
            .toList();
    }

    /**
     * retrieve all stored company representative accounts
     * @return list of CompanyRepresentative objects
     */
    @Override
    public List<CompanyRepresentative> getAllCompanyRepresentatives() {
        return users.values().stream()
            .filter(u -> u instanceof CompanyRepresentative)
            .map(u -> (CompanyRepresentative) u)
            .toList();
    }

    /**
     * retrieve all stored staff accounts
     * @return list of Staff objects
     */
    @Override
    public List<Staff> getAllStaffs() {
        return users.values().stream()
            .filter(u -> u instanceof Staff)
            .map(u -> (Staff) u)
            .toList();
    }

    /**
     * find user by login identifier (email or student ID depending on user type)
     * @param loginId login credential used at authentication
     * @return optional containing matching user if found, else empty
     */
    @Override
    public Optional<User> findByLoginId(String loginId) {
        return users.values().stream()
            .filter(u -> u.getLoginId().equals(loginId))
            .findFirst();
    }
}
