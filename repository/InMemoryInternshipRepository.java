package repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import model.Internship;
import model.Internship.Level;
import model.Internship.Status;
import model.InternshipFilter;

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

    // @Override
    // public List<Internship> filter(Status status, String major, Level level, String companyName, Integer remainingSlotsMin, Integer remainingSlotsMax) {
    //     return findAll().stream()
    //         .filter(i -> status == null || i.getStatus() == status)
    //         .filter(i -> major == null || i.getMajor().equalsIgnoreCase(major))
    //         .filter(i -> level == null || i.getLevel() == level)
    //         .filter(i -> companyName == null || i.getCompany().getCompanyName().equalsIgnoreCase(companyName))
    //         .filter(i -> {
    //             int remaining = i.getMaxSlots() - i.getConfirmedSlots();
    //             boolean aboveMin = (remainingSlotsMin == null) || remaining >= remainingSlotsMin;
    //             boolean belowMax = (remainingSlotsMax == null) || remaining <= remainingSlotsMax;
    //             return aboveMin && belowMax;
    //         })
    //         .toList();
    // }
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
                // handle date range (null means ignore)
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
