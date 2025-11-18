package repository;

import java.util.List;
import java.util.Optional;
import model.InternshipApplication;

/**
 * repository interface for managing persistence of internship applications
 * supports CRUD operations and custom query-based retrieval
 */
public interface InternshipAppRepository {

    /**
     * save or update an internship application record
     * @param app internship application to store
     */
    void save(InternshipApplication app);

    /**
     * find an internship application by its unique ID
     * @param id application identifier
     * @return optional containing application if found, else empty
     */
    Optional<InternshipApplication> findById(String id);

    /**
     * retrieve all internship application records
     * @return list of all applications
     */
    List<InternshipApplication> findAll();

    /**
     * retrieve applications with withdrawal requests flagged
     * @return list of applications requesting withdrawal
     */
    List<InternshipApplication> findWithdrawalRequests();

    /**
     * retrieve applications submitted by a specific student
     * @param studentId student identifier
     * @return list of matching applications
     */
    List<InternshipApplication> findByStudent(String studentId);

    /**
     * retrieve applications submitted for a given internship
     * @param internshipId internship identifier
     * @return list of matching applications
     */
    List<InternshipApplication> findByInternship(String internshipId);

    /**
     * remove an application record from storage
     * @param appId identifier of application to delete
     */
    void delete(String appId);
}
