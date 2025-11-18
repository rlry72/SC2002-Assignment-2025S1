package repository;

import java.util.List;
import java.util.Optional;
import model.Company;

/**
 * define storage and retrieval operations for Company entities
 * used to check existing company records or register new ones
 * allows multiple implementations such as in-memory or persistent database
 */
public interface CompanyRepository {

    /**
     * find existing company by name (case-sensitive match)
     * @param name company name used for search
     * @return optional containing company if found, else empty
     */
    Optional<Company> findByName(String name);

    /**
     * save new company or update existing one
     * if company with same name exists, implementation may replace or ignore
     * @param company company object to be stored
     */
    void save(Company company);

    /**
     * get list of all stored companies
     * @return list containing every company in storage
     */
    List<Company> findAll();
}
