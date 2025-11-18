package repository;

import java.util.List;
import java.util.Optional;

import model.CompanyRepresentative;
import model.Staff;
import model.Student;
import model.User;

/**
 * repository interface that defines persistence operations for user accounts
 * supports storage, lookup, existence checking, and category retrieval
 */
public interface UserRepository {

    /**
     * find a user using internal id value
     * @param id identifier used to locate user
     * @return optional containing user if found, else empty
     */
    Optional<User> findById(String id);

    /**
     * find a user by login credential field (email or id based on role)
     * @param loginId login identifier entered during authentication
     * @return optional containing matching user if found, else empty
     */
    Optional<User> findByLoginId(String loginId);

    /**
     * save or update a user account in persistent storage
     * @param user user entity to store
     */
    void save(User user);

    /**
     * check whether a user already exists
     * @param id identifier to be checked
     * @return true if exists, false otherwise
     */
    boolean exists(String id);

    /**
     * retrieve all stored student accounts
     * @return list of student records
     */
    List<Student> getAllStudents();

    /**
     * retrieve all company representative accounts
     * @return list of company representative records
     */
    List<CompanyRepresentative> getAllCompanyRepresentatives();

    /**
     * retrieve all staff accounts
     * @return list of staff records
     */
    List<Staff> getAllStaffs();
}
