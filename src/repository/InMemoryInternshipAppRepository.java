package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.InternshipApplication;

/**
 * in-memory implementation of InternshipAppRepository
 * provides runtime storage and lookup support for internship applications
 * data does not persist to disk and resets when application restarts
 */
public class InMemoryInternshipAppRepository implements InternshipAppRepository {

    /** map storing internship applications keyed by application id */
    private final Map<String, InternshipApplication> internshipApps = new HashMap<>();

    /**
     * save or update internship application record
     * @param app internship application to store
     */
    @Override
    public void save(InternshipApplication app) {
        internshipApps.put(app.getId(), app);
    }

    /**
     * find internship application by unique id
     * @param id application id to search
     * @return optional containing matching application if exists, else empty
     */
    @Override
    public Optional<InternshipApplication> findById(String id) {
        return Optional.ofNullable(internshipApps.get(id));
    }

    /**
     * find all internship applications submitted by a specific student
     * @param studentId id of student requester
     * @return list of all matching internship applications for student
     */
    @Override
    public List<InternshipApplication> findByStudent(String studentId) {
        return internshipApps.values().stream()
            .filter(a -> a.getStudentId().equals(studentId))
            .toList();
    }

    /**
     * find all internship applications for a specific internship
     * @param internshipId internship record id to search
     * @return list of applications belonging to target internship
     */
    @Override
    public List<InternshipApplication> findByInternship(String internshipId) {
        return internshipApps.values().stream()
            .filter(a -> a.getInternshipId().equals(internshipId))
            .toList();
    }

    /**
     * permanently remove internship application entry
     * @param appId id of application to delete
     */
    @Override
    public void delete(String appId) {
        internshipApps.remove(appId);
    }

    /**
     * retrieve all stored internship applications
     * @return list containing every application record
     */
    @Override
    public List<InternshipApplication> findAll() {
        return new ArrayList<>(internshipApps.values());
    }

    /**
     * fetch applications with withdrawal requested flag set to true
     * used by staff for withdrawal approval workflow
     * @return list of withdrawal requested applications
     */
    @Override
    public List<InternshipApplication> findWithdrawalRequests() {
        return internshipApps.values().stream()
            .filter(InternshipApplication::isWithdrawalRequested)
            .toList();
    }
}
