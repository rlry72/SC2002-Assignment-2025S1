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

public class CompanyRepController {
    private final InternshipRepository internships;
    private final InternshipAppRepository internshipApplications;
    private final UserRepository users;
    private final CompanyRepository companies;

    public CompanyRepController(InternshipRepository internshipRepo, InternshipAppRepository internshipAppRepo, UserRepository userRepo, CompanyRepository companyRepo) {
        this.internships = internshipRepo;
        this.internshipApplications = internshipAppRepo;
        this.users = userRepo;
        this.companies = companyRepo;
    }

    public Internship createInternship(String title, String desc, Internship.Level level, String major, 
        LocalDate startDate, LocalDate endDate, CompanyRepresentative cr, int slots, boolean visibility) {
            Internship internship = new Internship(UUID.randomUUID().toString(), title, desc, level, major, startDate, endDate, cr, slots, visibility, cr.getCompany());

            internships.save(internship);
            return internship;
    }

    public List<Internship> getInternshipsByCompanyRep(CompanyRepresentative rep) {
        return internships.findAll().stream().filter(i -> i.getCr().getUserId().equals(rep.getUserId())).toList();
    }

    public List<InternshipApplication> getInternshipApplications(String internshipId) {
        return internshipApplications.findByInternship(internshipId);
    }

    public void approveApplication(InternshipApplication internshipApplication, Internship internship) {
        if (internshipApplication.getStatus() != InternshipApplication.Status.PENDING) {
            throw new IllegalStateException("Only pending applications may be approved.");
        }
        if (internship.getStatus() != Internship.Status.APPROVED) {
            throw new IllegalStateException("Internship is not approved by Career Center Staff yet!");
        }
        if (internship.isFull()) {
            throw new IllegalStateException("Unable to approve: internship slots are filled.");
        }

        internshipApplication.setStatus(InternshipApplication.Status.SUCCESSFUL);
        internshipApplications.save(internshipApplication);
    }

    public void rejectApplication(InternshipApplication internshipApplication) {
        if (internshipApplication.getStatus() != InternshipApplication.Status.PENDING) {
            throw new IllegalStateException("Only pending applications may be rejected.");
        }

        internshipApplication.setStatus(InternshipApplication.Status.UNSUCCESSFUL);
        internshipApplications.save(internshipApplication);
    }

    public void toggleVisibility(Internship internship, boolean isVisible) {
        internship.setVisibility(isVisible);
        internships.save(internship);
    }

    public CompanyRepresentative registerRep(String name, String email, Company company, String dept, String position) {
        if (users.exists(email)) {
            throw new IllegalStateException("A user with this email already exists.");
        }

        CompanyRepresentative rep = new CompanyRepresentative(UUID.randomUUID().toString(), name, email, company, dept, position);
        users.save(rep);
        return rep;
    }

    public CompanyRepresentative register(String name, String email, String companyName, String dept, String position) {
        if (users.exists(email)) {
            throw new IllegalStateException("An account with this email already exists.");
        }

        Company company = companies.findByName(companyName).orElseGet(() -> {
            Company newCompany = new Company(companyName);
            companies.save(newCompany);
            return newCompany;
        });

        CompanyRepresentative rep = new CompanyRepresentative(email, name, email, company, dept, position);

        users.save(rep);
        return rep;
    }

    public List<Internship> getOwnInternshipsFiltered(CompanyRepresentative rep, InternshipFilter filter) {
        return internships.filter(filter).stream()
            .filter(i -> i.getCr().getUserId().equalsIgnoreCase(rep.getUserId()))
            .sorted(Comparator.comparing(Internship::getTitle))
            .toList();
    }

    /**
     * allow company rep to update internship details if not approved yet
     * @param internship internship to edit
     * @param title updated title, same if null
     * @param desc updated description, same if null
     * @param level updated level, same if null
     * @param major updated major, same if null
     * @param start updated start date, same if null
     * @param end updated end date, same if null
     * @param slots updated slot count, same if -1
     * @throws IllegalStateException if internship already approved/filled
     */
    public void editInternship(Internship internship,
                            String title, String desc, Internship.Level level,
                            String major, LocalDate start, LocalDate end, int slots) {

        if (internship.getStatus() != Internship.Status.PENDING) {
            throw new IllegalStateException("Only pending internships can be edited.");
        }

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
     * delete internship if still pending approval
     * @param internship internship to delete
     * @throws IllegalStateException if internship is approved, rejected or filled
     */
    public void deleteInternship(Internship internship) {
        if (internship.getStatus() != Internship.Status.PENDING) {
            throw new IllegalStateException("Only pending internships can be deleted.");
        }
        internships.delete(internship.getId());
    }

    public Internship getInternshipById(String internshipId) {
        return internships.findById(internshipId).orElse(null);
    }

    public Student getStudentById(String studentId) {
        return users.findById(studentId).filter(u -> u instanceof Student).map(u -> (Student) u).orElse(null);
    }

}
