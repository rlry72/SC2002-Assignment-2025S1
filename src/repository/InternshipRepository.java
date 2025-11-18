package repository;

import java.util.List;
import java.util.Optional;
import model.Internship;
import model.InternshipFilter;

/**
 * repository interface that defines persistence operations for internship records
 * supports CRUD operations, lookup queries, and filtered search
 */
public interface InternshipRepository {

    /**
     * find an internship using its unique identifier
     * @param id internship identifier
     * @return optional containing internship if found, else empty
     */
    Optional<Internship> findById(String id);

    /**
     * save or update an internship record in data storage
     * @param internship internship instance to persist
     */
    void save(Internship internship);

    /**
     * delete an internship from storage
     * @param id identifier of internship to remove
     */
    void delete(String id);

    /**
     * retrieve all internships stored in repository
     * @return list of all internships
     */
    List<Internship> findAll();

    /**
     * find internships offered by a specific company
     * @param companyName company name used for search
     * @return list of internships that match company name
     */
    List<Internship> findByCompany(String companyName);

    /**
     * find internships by approval or availability status
     * @param status internship status enum
     * @return list of internships matching given status
     */
    List<Internship> findByStatus(Internship.Status status);

    /**
     * filter internships based on multiple optional criteria
     * @param filter filter object containing nullable fields defining constraints
     * @return list of internships that meet specified filter conditions
     */
    List<Internship> filter(InternshipFilter filter);

    // old filter for reference, not used currently
    // List<Internship> filter(Internship.Status status, String major, Internship.Level level, String companyName, Integer remainingSlotsMin, Integer remainingSlotsMax);
}
