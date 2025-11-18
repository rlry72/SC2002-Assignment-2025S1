package controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import model.CompanyRepresentative;
import model.Internship;
import model.InternshipApplication;
import model.InternshipFilter;
import model.Student;
import model.User;
import repository.InternshipAppRepository;
import repository.InternshipRepository;
import repository.UserRepository;

/**
 * staffcontroller class to implement logic for staff operations
 */
public class StaffController {
    /**
     * repository of all users
     */
    private final UserRepository users;
    /**
     * repository of all internships
     */
    private final InternshipRepository internships;
    /**
     * repository of all internship applications
     */
    private final InternshipAppRepository applications;

    /**
     * constructor of staff controller
     * @param userRepository    repository of users
     * @param internshipRepository  repository of internships
     * @param appRepository repository of applications
     */
    public StaffController(UserRepository userRepository, InternshipRepository internshipRepository, InternshipAppRepository appRepository) {
        this.users = userRepository;
        this.internships = internshipRepository;
        this.applications = appRepository;
    }
    
    /**
     * gets a list of registered company representatives who are not yet approved by staff
     * @return list of pending company representatives
     */
    public List<CompanyRepresentative> getPendingCompanyReps() {
        return users.getAllCompanyRepresentatives().stream().filter(rep -> !rep.isApproved()).toList();
    }

    /**
     * approve company representative account and update in repository
     * @param cr the company representative object to be approved
     */
    public void approveCompanyRep(CompanyRepresentative cr) {
        cr.setApproved(true);
        users.save(cr);
    }

    /**
     * approve company representative account and update in repository
     * @param cr the company representative object to be rejected
     */
    public void rejectCompanyRep(CompanyRepresentative cr) {
        cr.setApproved(false);
        users.save(cr);
    }

    /**
     * get a list of internship opportunities pending approval
     * @return list of internship opportunities pending approval
     */
    public List<Internship> getPendingInternships() {
        return internships.findByStatus(Internship.Status.PENDING);
    }

    /**
     * approve a pending internship opportunity
     * @param internship    internship opportunity to be approved
     */
    public void approveInternship(Internship internship) {
        internship.setStatus(Internship.Status.APPROVED);
        internships.save(internship);
    }
    
    /**
     * reject a pending internship opportunity
     * @param internship    internship opportunity to be rejected
     */
    public void rejectInternship(Internship internship) {
        internship.setStatus(Internship.Status.REJECTED);
        internships.save(internship);
    }

    /**
     * get a list of withdrawal requests for internship applications by students
     * @return a list of withdrawal requests for internship applications by students
     */
    public List<InternshipApplication> getWithdrawalRequests() {
        return applications.findWithdrawalRequests();
    }

    /**
     * approve withdrawal from an internship application
     * @param application internship application to be withdrawn from
     */
    public void approveWithdrawal(InternshipApplication application) {
        application.setStatus(InternshipApplication.Status.WITHDRAWN);
        applications.save(application);
    }

    /**
     * reject withdrawal from an internship application
     * @param application internship application to be withdrawn from
     */
    public void rejectWithdrawal(InternshipApplication application) {
        application.setStatus(InternshipApplication.Status.PENDING);
        applications.save(application);
    }

    // public List<Internship> generateReport(Internship.Status status, String major, Internship.Level level, String companyName, Integer remainingSlotsMin, Integer remainingSlotsMax) {
    //     return internships.filter(status, major, level, companyName, remainingSlotsMin, remainingSlotsMax);
    // }

    /**
     * sort list of internships by remaining slots left
     * @return list of internships by remaining slots left
     */
    public List<Internship> sortByRemainingSlots() {
        return internships.findAll().stream().sorted((a, b) -> Integer.compare(b.getRemainingSlots(), a.getRemainingSlots())).toList();
    }

    /**
     * get list of internships by a particular company
     * @param companyName naem of company to filter
     * @return  list of internships by a particular company
     */
    public List<Internship> getByCompany(String companyName) {
        return internships.findAll().stream().filter(i -> i.getCompanyName().equalsIgnoreCase(companyName)).toList();
    }

    /**
     * group list of internships by company
     * @return grouped list of internships by company
     */
    public Map<String, List<Internship>> groupByCompany() {
        return internships.findAll().stream().collect(Collectors.groupingBy(i -> i.getCompanyName()));
    }

    /**
     * get list of internships created by a particular company representative
     * @param repId ID of company representative 
     * @return list of internships created by a particular company representative
     */
    public List<Internship> getByRepresentative(String repId) {
        return internships.findAll().stream().filter(i -> i.getCr().getUserId().equals(repId)).toList();
    }

    /**
     * get list of all internships
     * @return list of all internships
     */
    public List<Internship> getAll() {
        return internships.filter(new InternshipFilter()); // empty filter: return all
    }

    /**
     * filter list of internships by status
     * @param status status to filter by (PENDING, APPROVED, REJECTED, FILLED)
     * @return filtered list of internships by status
     */
    public List<Internship> filterByStatus(Internship.Status status) {
        return internships.filter(new InternshipFilter().setStatus(status));
    }

    /**
     * filter list of internships by major
     * @param major preferred major
     * @return filtered list of internships by major
     */
    public List<Internship> filterByMajor(String major) {
        return internships.filter(new InternshipFilter().setMajor(major));
    }

    /**
     * filter list of internships by level
     * @param level level (BASIC, INTERMEDIATE, ADVANCED)
     * @return filtered list of internships by level
     */
    public List<Internship> filterByLevel(Internship.Level level) {
        return internships.filter(new InternshipFilter().setLevel(level));
    }

    /**
     * filter list of internships by company name
     * @param companyName name of company
     * @return filtered list of internships by company name
     */
    public List<Internship> filterByCompany(String companyName) {
        return internships.filter(new InternshipFilter().setCompanyName(companyName));
    }

    /**
     * filter list of internships by range of remaining slots
     * @param min min number of slots remaining 
     * @param max max number of slots remaining
     * @return filtered list of internships by range of remaining slots
     */
    public List<Internship> filterByRemainingSlotsRange(int min, int max) {
        return internships.filter(new InternshipFilter()
                .setSlotsMin(min)
                .setSlotsMax(max));
    }

    /**
     * filter list of internships by date range
     * @param start opening date
     * @param end closing date
     * @return filtered list of internships by date range
     */
    public List<Internship> filterByDateRange(LocalDate start, LocalDate end) {
        return internships.filter(new InternshipFilter()
                .setStartDate(start)
                .setEndDate(end));
    }

    /**
     * sort list of internships by remaining slots in descending order
     * @return sort list of internships by remaining slots in descending order
     */
    public List<Internship> sortByRemainingSlotsDesc() {
        return getAll().stream()
                .sorted(Comparator.comparingInt(Internship::getRemainingSlots).reversed())
                .toList();
    }

    /**
     * get list of internships sorted by number of applications in descending order
     * @return list of internships sorted by number of applications in descending order
     */
    public List<String> getPopularityReport() {
    return getAll().stream()
            .map(i -> i.getTitle() + " (" + i.getCompanyName() + ") : " +
                    applications.findByInternship(i.getId()).size() + " applications")
            .collect(Collectors.toList());
    }

    /**
     * filter list of internships by company representative
     * @param repId id of company representative
     * @return filtered list of internships by company representative
     */
    public List<Internship> filterByRepresentative(String repId) {
        return internships.filter(new InternshipFilter().setRepId(repId));
    }

    /**
     * get filtered list of internships based on filters input by user
     * @param filter desired filters by user
     * @return filtered list of internships based on filters input by user
     */
    public List<Internship> getFiltered(InternshipFilter filter) {
        return internships.filter(filter).stream()
            .sorted(Comparator.comparing(Internship::getTitle))
            .toList();
    }

    /**
     * get name of student
     * @param studentId id of student
     * @return name of student if available, else "Unknown Student"
     */
    public String getStudentName(String studentId) {
        return users.findById(studentId)
                .filter(u -> u instanceof Student)
                .map(User::getName)
                .orElse("Unknown Student");
    }

    /**
     * get title of internship
     * @param internshipId id of internship
     * @return title of internship if available, else "Unknown Internship"
     */
    public String getInternshipTitle(String internshipId) {
        return internships.findById(internshipId)
                .map(Internship::getTitle)
                .orElse("Unknown Internship");
    }

    /**
     * get internship by its ID
     * @param internshipId ID of internship
     * @return internship object if found, else null
     */
    public Internship getInternshipById(String internshipId) {
        return internships.findById(internshipId).orElse(null);
    }

    /**
     * get student by its ID
     * @param studentId ID of student
     * @return student object if found, else null
     */
    public Student getStudentById(String studentId) {
        return users.findById(studentId).filter(u -> u instanceof Student).map(u -> (Student) u).orElse(null);
    }

    /**
     * get list of all internships
     * @return list of all internships
     */
    public List<Internship> getAllInternships() {
            return internships.findAll();
    }

    /**
     * get list of all applications for a specific internship
     * @param internshipId ID of internship
     * @return list of all applications for a specific internship
     */
    public List<InternshipApplication> getAllApplicationsForInternship(String internshipId) {
        return applications.findByInternship(internshipId); // does NOT filter by status
    }

}

