package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Company;

public class InMemoryCompanyRepository implements CompanyRepository{
    private final Map<String, Company> companies = new HashMap<>();

    @Override
    public Optional<Company> findByName(String name) {
        return Optional.ofNullable(companies.get(name.toLowerCase()));
    }

    @Override
    public void save(Company company) {
        companies.put(company.getCompanyName().toLowerCase(), company);
    }

    @Override
    public List<Company> findAll() {
        return new ArrayList<>(companies.values());
    }
}
