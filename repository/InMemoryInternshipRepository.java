package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Internship;
import model.Internship.Status;

public class InMemoryInternshipRepository implements InternshipRepository{
    private final Map<String, Internship> internships = new HashMap<>();

    public List<Internship> findAll() {
        return new ArrayList<>(internships.values());
    }

    @Override
    public Optional<Internship> findById(String id) {
        return Optional.ofNullable(internships.get(id));
    }

    @Override
    public void save(Internship internship) {
        internships.put(internship.getId(), internship);
    }

    @Override
    public List<Internship> findByCompany(String companyName) {
        return internships.values().stream()
            .filter(i -> i.getCompanyName().equals(companyName))
            .toList();
    }

    @Override
    public List<Internship> findByStatus(Status status) {
        return internships.values().stream()
            .filter(i -> i.getStatus().equals(status)).toList();    
    }
}
