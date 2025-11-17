package repository;

import java.util.List;
import java.util.Optional;

import model.Company;

public interface CompanyRepository {
    Optional<Company> findByName(String name);
    void save(Company company);
    List<Company> findAll();
}