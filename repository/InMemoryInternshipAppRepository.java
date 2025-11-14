package repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.InternshipApplication;

public class InMemoryInternshipAppRepository implements InternshipAppRepository{

    private final Map<String, InternshipApplication> internshipApps = new HashMap<>();

    @Override
    public void save(InternshipApplication app) {
        internshipApps.put(app.getId(), app);
    }

    @Override
    public Optional<InternshipApplication> findById(String id) {
        return Optional.ofNullable(internshipApps.get(id));
    }

    @Override
    public List<InternshipApplication> findByStudent(String studentId) {
        return internshipApps.values().stream()
            .filter(a -> a.getStudentId().equals(studentId)).toList();
    }

    @Override
    public List<InternshipApplication> findByInternship(String internshipId) {
        return internshipApps.values().stream()
            .filter(a -> a.getInternshipId().equals(internshipId)).toList();
    }

    @Override
    public void delete(String appId) {
        internshipApps.remove(appId);
    }

}
