package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Company;

/**
 * in-memory implementation of CompanyRepository
 * used for runtime storage without writing to disk
 * keys are stored in lowercase for case-insensitive matching
 */
public class InMemoryCompanyRepository implements CompanyRepository {

    /** map storing companies using lowercase company name as key */
    private final Map<String, Company> companies = new HashMap<>();

    /**
     * search for company by name (case-insensitive)
     * @param name company name to search
     * @return optional containing company if found, else empty
     */
    @Override
    public Optional<Company> findByName(String name) {
        return Optional.ofNullable(companies.get(name.toLowerCase()));
    }

    /**
     * save new company or replace existing entry using lowercase key
     * @param company company record to store
     */
    @Override
    public void save(Company company) {
        companies.put(company.getCompanyName().toLowerCase(), company);
    }

    /**
     * retrieve full list of stored companies
     * @return list containing all company entries
     */
    @Override
    public List<Company> findAll() {
        return new ArrayList<>(companies.values());
    }
}
