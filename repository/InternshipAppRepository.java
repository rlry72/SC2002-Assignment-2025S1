package repository;

import java.util.List;
import java.util.Optional;

import model.InternshipApplication;

public interface InternshipAppRepository {
    void save(InternshipApplication app);

    Optional<InternshipApplication> findById(String id);
    List<InternshipApplication> findAll();
    List<InternshipApplication> findWithdrawalRequests();
    List<InternshipApplication> findByStudent(String studentId);
    List<InternshipApplication> findByInternship(String internshipId);

    void delete(String appId);
}
