package repository;

import java.util.List;
import java.util.Optional;

import model.Internship;

public interface InternshipRepository {
    Optional<Internship> findById(String id);
    void save(Internship internship);
    List<Internship> findAll();

    List<Internship> findByCompany(String companyName);
    List<Internship> findByStatus(Internship.Status status);
}
