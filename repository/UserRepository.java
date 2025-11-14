package repository;

import java.util.List;
import java.util.Optional;

import model.CompanyRepresentative;
import model.Staff;
import model.Student;
import model.User;

public interface UserRepository {
    Optional<User> findById(String id);
    void save(User user);
    boolean exists(String id);
    List<Student> getAllStudents();
    List<CompanyRepresentative> getAllCompanyRepresentatives();
    List<Staff> getAllStaffs();
}
