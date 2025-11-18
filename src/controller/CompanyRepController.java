package controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import model.Company;
import model.CompanyRepresentative;
import model.Internship;
import model.InternshipApplication;
import model.InternshipFilter;
import model.Student;
import repository.CompanyRepository;
import repository.InternshipAppRepository;
import repository.InternshipRepository;
import repository.UserRepository;

/**
 * controller that manages company representative related functions
 * includes internship creation, modification, visibility, registration,
 * application review and filtered retrieval
 */
public class CompanyRepController {

    /** internship persistence repository */
    private final InternshipRepository internships;

    /** internship application persistence repository */
    private final InternshipAppRepository internshipApplications;

    /** user persistence repository */
    private final UserRepository users;

    /** company persistence repository */
    private final CompanyRepository companies;

    /**
     * create controller with required repository dependencies
     * @param internshipRepo repository storing internships
     * @param internshipAppRepo repository storing internship applications
     * @param userRepo repository storing system users
     * @param companyRepo repository storing companies
     */
    public CompanyRepController(InternshipRepository internshipRepo, 
                                InternshipAppRepository internshipAppRepo, 
                                UserRepository userRepo, 
                                CompanyRepository companyRepo) {
        this.internships = internshipRepo;
        this.internshipApplications = internshipAppRepo;
        this.users = userRepo;
        this.companies = companyRepo;
    }

    /**
     * create new internship posting assigned to a company representative
     * @param title internship title
     * @param desc internship description
     * @param level level of internship
     * @param major preferred student major
     * @param startDate application open date
     * @param endDate application close date
     * @param cr company representative owning internship
     * @param slots maximum available slots (1-10)
     * @param visibility whether internship is visible to others
     * @return created internship instance
     */
    public Internship createInternship(String title, String desc, Internship.Level level, String major,
                                       LocalDate startDate, LocalDate endDate, CompanyRepresentative cr, 
                                       int slots, boolean visibility) {

        Internship internship = new Internship(
                UUID.randomUUID().toString(), title, desc, level, major,
                startDate, endDate, cr, slots, visibility, cr.getCompany()
        );

        internships.save(internship);
        return internship;
    }

    /**
     * retrieve internships created by selected company representative
     * @param rep company representative owner
     * @return list of owned internships
     */
    public List<Internship> getInternshipsByCompanyRep(CompanyRepresentative rep) {
        return internships.findAll().stream()
                .filter(i -> i.getCr().getUserId().equals(rep.getUserId()))
                .toList();
    }

    /**
     * retrieve all applications submitted for specified internship
     * @param internshipId targeted internship identifier
     * @return list of related internship applications
     */
    public List<InternshipApplication> getInternshipApplications(String internshipId) {
        return internshipApplications.findByInternship(internshipId);
    }

    /**
     * approve pending application if internship is valid and has remaining capacity
     * @param internshipApplication targeted application entry
     * @param internship internship to which application belongs
     * @throws IllegalStateException if invalid state or full capacity
     */
    public void approveApplication(InternshipApplication internshipApplication, Internship internship) {
        if (internshipApplication.getStatus() != InternshipApplication.Status.PENDING)
            throw new IllegalStateException("Only pending applications may be approved.");
        if (internship.getStatus() != Internship.Status.APPROVED)
            throw new IllegalStateException("Internship is not approved by Career Center Staff yet!");
        if (internship.isFull())
            throw new IllegalStateException("Unable to approve: internship slots are filled.");

        internshipApplication.setStatus(InternshipApplication.Status.SUCCESSFUL);
        internshipApplications.save(internshipApplication);
    }

    /**
     * reject a pending internship application
     * @param internshipApplication targeted application entry
     * @throws IllegalStateException if application already resolved
     */
    public void rejectApplication(InternshipApplication internshipApplication) {
        if (internshipApplication.getStatus() != InternshipApplication.Status.PENDING)
            throw new IllegalStateException("Only pending applications may be rejected.");

        internshipApplication.setStatus(InternshipApplication.Status.UNSUCCESSFUL);
        internshipApplications.save(internshipApplication);
    }

    /**
     * update internship visibility state
     * @param internship internship to modify
     * @param isVisible new visibility state
     */
    public void toggleVisibility(Internship internship, boolean isVisible) {
        internship.setVisibility(isVisible);
        internships.save(internship);
    }

    /**
     * register company representative using predefined company instance
     * @param name representative name
     * @param email representative email/login id
     * @param company company assigned
     * @param dept department assigned
     * @param position job position
     * @return created company representative
     * @throws IllegalStateException if email already registered
     */
    public CompanyRepresentative registerRep(String name, String email, Company company, 
                                             String dept, String position) {
        if (users.exists(email))
            throw new IllegalStateException("A user with this email already exists.");

        CompanyRepresentative rep = new CompanyRepresentative(
                UUID.randomUUID().toString(), name, email, company, dept, position
        );
        users.save(rep);
        return rep;
    }

    /**
     * register company representative by company name lookup or creation
     * @param name representative name
     * @param email representative login id
     * @param companyName company association
     * @param dept department assigned
     * @param position role position
     * @return created representative instance
     * @throws IllegalStateException if email already exists
     */
    public CompanyRepresentative register(String name, String email, String companyName,
                                          String dept, String position) {
        if (users.exists(email))
            throw new IllegalStateException("An account with this email already exists.");

        Company company = companies.findByName(companyName).orElseGet(() -> {
            Company newCompany = new Company(companyName);
            companies.save(newCompany);
            return newCompany;
        });

        CompanyRepresentative rep = new CompanyRepresentative(email, name, email, company, dept, position);
        users.save(rep);
        return rep;
    }

    /**
     * retrieve representative-owned internships after filtering rules applied
     * @param rep owner requesting list
     * @param filter filtering options
     * @return sorted filtered internship list
     */
    public List<Internship> getOwnInternshipsFiltered(CompanyRepresentative rep, InternshipFilter filter) {
        return internships.filter(filter).stream()
                .filter(i -> i.getCr().getUserId().equalsIgnoreCase(rep.getUserId()))
                .sorted(Comparator.comparing(Internship::getTitle))
                .toList();
    }

    /**
     * update internship details if pending approval
     * @param internship internship to be modified
     * @param title new title (ignored if null)
     * @param desc new description (ignored if null)
     * @param level new level (ignored if null)
     * @param major new preferred major (ignored if null)
     * @param start new start date (ignored if null)
     * @param end new end date (ignored if null)
     * @param slots new slot count (ignored if less than 0)
     * @throws IllegalStateException if internship cannot be modified
     */
    public void editInternship(Internship internship, String title, String desc, Internship.Level level,
                               String major, LocalDate start, LocalDate end, int slots) {

        if (internship.getStatus() != Internship.Status.PENDING)
            throw new IllegalStateException("Only pending internships can be edited.");

        if (title != null) internship.setTitle(title);
        if (desc != null) internship.setDesc(desc);
        if (level != null) internship.setLevel(level);
        if (major != null) internship.setMajor(major);
        if (start != null) internship.setStartDate(start);
        if (end != null) internship.setEndDate(end);
        if (slots > 0 && slots <= 10) internship.setMaxSlots(slots);

        internships.save(internship);
    }

    /**
     * permanently delete internship if still pending
     * @param internship internship entry to remove
     * @throws IllegalStateException if internship is already evaluated
     */
    public void deleteInternship(Internship internship) {
        if (internship.getStatus() != Internship.Status.PENDING)
            throw new IllegalStateException("Only pending internships can be deleted.");

        internships.delete(internship.getId());
    }

    /**
     * retrieve internship instance using identifier
     * @param internshipId id value used to locate internship
     * @return internship if exists, null otherwise
     */
    public Internship getInternshipById(String internshipId) {
        return internships.findById(internshipId).orElse(null);
    }

    /**
     * retrieve student instance using identifier
     * @param studentId id value used to locate student
     * @return student if exists, null otherwise
     */
    public Student getStudentById(String studentId) {
        return users.findById(studentId)
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .orElse(null);
    }
}
