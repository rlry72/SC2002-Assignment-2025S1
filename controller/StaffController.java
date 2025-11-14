package controller;

import model.CompanyRepresentative;
import model.Internship;
import repository.InternshipRepository;
import repository.UserRepository;

public class StaffController {
    private final UserRepository users;
    private final InternshipRepository internships;

    public StaffController(UserRepository userRepository, InternshipRepository internshipRepository) {
        this.users = userRepository;
        this.internships = internshipRepository;
    }

    public void approveOrRejectCompanyRep(CompanyRepresentative cr, boolean approval) {
        cr.setApproved(approval);
        users.save(cr);
    }

    public void approveInternship(Internship internship) {
        internship.setStatus(Internship.Status.APPROVED);
        internships.save(internship);
    }

    public void rejectInternship(Internship internship) {
        internship.setStatus(Internship.Status.REJECTED);
        internships.save(internship);
    }
}
