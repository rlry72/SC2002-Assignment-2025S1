package controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import model.Company;
import model.CompanyRepresentative;
import model.Internship;
import model.InternshipApplication;
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
}
