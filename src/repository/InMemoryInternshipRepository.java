package repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import model.Internship;
import model.Internship.Status;
import model.InternshipFilter;

/**
 * in-memory implementation of InternshipRepository
 * stores internship records using a hash map keyed by internship id
 * data is volatile and cleared when application terminates
 */
public class InMemoryInternshipRepository implements InternshipRepository {

    /** map storing internship records keyed by internship id */
    private final Map<String, Internship> internships = new HashMap<>();

    /**
     * retrieve all internship records
     * @return list containing every stored internship object
     */
    @Override
    public List<Internship> findAll() {
        return new ArrayList<>(internships.values());
    }

    /**
     * find internship by unique id
     * @param id internship identifier
     * @return optional with stored internship if found, else empty
     */
    @Override
    public Optional<Internship> findById(String id) {
        return Optional.ofNullable(internships.get(id));
    }

    /**
     * save or update internship entry
     * if id already exists, record is overwritten
     * @param internship internship object to store
     */
    @Override
    public void save(Internship internship) {
        internships.put(internship.getId(), internship);
    }

    /**
     * delete internship entry permanently
     * no action if id does not exist
     * @param id internship identifier to remove
     */
    @Override
    public void delete(String id) {
        internships.remove(id);
    }

    /**
     * find internships offered by a company
     * comparison is done using exact company name match
     * @param companyName company name to filter by
     * @return list of matching internship records
     */
    @Override
    public List<Internship> findByCompany(String companyName) {
        return internships.values().stream()
            .filter(i -> i.getCompanyName().equals(companyName))
            .toList();
    }

    /**
     * find internships by approval or lifecycle status
     * @param status internship status to filter by
     * @return list of matching internship records
     */
    @Override
    public List<Internship> findByStatus(Status status) {
        return internships.values().stream()
            .filter(i -> i.getStatus().equals(status))
            .toList();
    }
    

    /**
     * filter internships using multiple optional conditions from InternshipFilter
     * filtering supports status, major, level, company, representative id, slot range, and date range logic
     * any null criteria is ignored
     * @param filter configured InternshipFilter object containing filter criteria
     * @return list of internships satisfying all provided filter criteria
     */
    @Override
    public List<Internship> filter(InternshipFilter filter) {
        return internships.values().stream()
            .filter(i -> filter.getStatus() == null || i.getStatus() == filter.getStatus())
            .filter(i -> filter.getMajor() == null || i.getMajor().equalsIgnoreCase(filter.getMajor()))
            .filter(i -> filter.getLevel() == null || i.getLevel() == filter.getLevel())
            .filter(i -> filter.getCompanyName() == null || 
                    i.getCompanyName().equalsIgnoreCase(filter.getCompanyName()))
            .filter(i -> filter.getRepId() == null || i.getCr().getUserId().equalsIgnoreCase(filter.getRepId()))
            .filter(i -> filter.getSlotsMin() == null || i.getRemainingSlots() >= filter.getSlotsMin())
            .filter(i -> filter.getSlotsMax() == null || i.getRemainingSlots() <= filter.getSlotsMax())
            .filter(i -> {
                // perform optional date-range overlap logic
                if (filter.getStartDate() == null && filter.getEndDate() == null) return true;

                LocalDate start = filter.getStartDate();
                LocalDate end   = filter.getEndDate();
                LocalDate iStart = i.getStartDate();
                LocalDate iEnd   = i.getEndDate();

                boolean noOverlap =
                        (end   != null && iStart.isAfter(end)) ||
                        (start != null && iEnd.isBefore(start));

                return !noOverlap;
            })
            .toList();
    }
}
