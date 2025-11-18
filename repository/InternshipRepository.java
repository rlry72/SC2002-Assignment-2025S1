package repository;

import java.util.List;
import java.util.Optional;

import model.Internship;
import model.InternshipFilter;

public interface InternshipRepository {
    Optional<Internship> findById(String id);
    void save(Internship internship);
    void delete(String id);
    List<Internship> findAll();

    List<Internship> findByCompany(String companyName);
    List<Internship> findByStatus(Internship.Status status);
    List<Internship> filter(InternshipFilter filter);

    // List<Internship> filter(Internship.Status status, String major, Internship.Level level, String companyName, Integer remainingSlotsMin, Integer remainingSlotsMax);
}
