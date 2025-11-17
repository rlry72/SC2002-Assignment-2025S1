package controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import model.Internship;
import model.InternshipApplication;
import model.InternshipFilter;
import model.Student;
import repository.InternshipAppRepository;
import repository.InternshipRepository;

public class StudentController {
    private final InternshipRepository internships;
    private final InternshipAppRepository applications;

    public StudentController(InternshipRepository internshipRepo, InternshipAppRepository appRepo) {
        this.internships = internshipRepo;
        this.applications = appRepo;
    }

    public List<Internship> getEligibleInternships(Student s) {
        System.out.println("View Eligible internships");

        LocalDate currDate = LocalDate.now();

        return internships.findAll().stream()
            .filter(i -> i.getStatus() == Internship.Status.APPROVED)
            .filter(Internship::getVisibility)
            .filter(i -> i.isOpen(currDate))
            .filter(i -> i.getMajor().equalsIgnoreCase(s.getMajor()))
            .filter(i -> isLevelAllowed(s, i))
            .sorted(Comparator.comparing(Internship::getTitle))
            .toList();
    }

    public List<Internship> getEligibleInternships(Student s, InternshipFilter filter) {
        LocalDate today = LocalDate.now();

        return internships.filter(filter).stream()
            .filter(i -> i.getStatus() == Internship.Status.APPROVED)
            .filter(Internship::getVisibility)
            .filter(i -> i.isOpen(today))
            .filter(i -> i.getMajor().equalsIgnoreCase(s.getMajor()))
            .filter(i -> isLevelAllowed(s, i))
            .sorted(Comparator.comparing(Internship::getTitle))
            .toList();
    }


    public List<InternshipApplication> getInternshipApplications(Student s) {
        return applications.findByStudent(s.getUserId());
    }

    public Internship getInternshipById(String internshipId) {
        return internships.findById(internshipId).orElse(null);
    }

    private boolean isLevelAllowed(Student student, Internship internship) {
        if (student.getYearOfStudy() < 3 && internship.getLevel() != Internship.Level.BASIC)
            return false;
        else
            return true;
    }

    public void applyInternship(Student student, Internship internship) {
        if (internship.getStatus() != Internship.Status.APPROVED) {
            throw new IllegalArgumentException("This internship is not approved yet!");
        }

        // All applications by this student
        List<InternshipApplication> apps = applications.findByStudent(student.getUserId());

        // 1) Enforce max 3 active applications
        long activeCount = apps.stream()
                .filter(InternshipApplication::isActive)   // PENDING or SUCCESSFUL
                .count();

        if (activeCount >= 3) {
            throw new IllegalStateException("Maximum of 3 active internship applications allowed!");
        }

        // 2) Prevent duplicate applications for the same internship
        boolean alreadyAppliedForThis = apps.stream()
                .anyMatch(a ->
                        a.getInternshipId().equals(internship.getId()) &&
                        a.isActive()
                );

        if (alreadyAppliedForThis) {
            throw new IllegalStateException("You already have an active application for this internship.");
        }

        // 3) (Optional) extra safety: check internship is still open + not full
        // LocalDate today = LocalDate.now();
        // if (!internship.isOpen(today)) throw new IllegalStateException("Application period is closed.");
        // if (internship.isFull()) throw new IllegalStateException("Internship slots are filled.");

        // 4) Create and save new application
        InternshipApplication application =
                new InternshipApplication(UUID.randomUUID().toString(),
                                        student.getUserId(),
                                        internship.getId());

        applications.save(application);
    }


    public void acceptInternship(Student student, InternshipApplication internshipApplication, Internship internship) {
        if (!internshipApplication.getStudentId().equals(student.getUserId()))
            throw new IllegalArgumentException("This application does not belong to you!");
        if (internshipApplication.getStatus() != InternshipApplication.Status.SUCCESSFUL)
            throw new IllegalArgumentException("You can only accept applications that are successful.");
        if (internshipApplication.studentAccepted())
            throw new IllegalArgumentException("This application has already been accepted.");

        internshipApplication.accept();
        applications.save(internshipApplication);
        internship.addConfirmedSlot();
        internships.save(internship);

        for (InternshipApplication otherApplications : applications.findByStudent(student.getUserId())) {
            if (!otherApplications.getId().equals(internshipApplication.getId())) {
                otherApplications.setStatus(InternshipApplication.Status.WITHDRAWN);
                applications.save(otherApplications);
            }
        }
    }

    public void withdrawFromInternship(Student student, InternshipApplication application) {
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only withdraw from Internship applications made by you.");
        }

        if (application.getStatus() == InternshipApplication.Status.WITHDRAWN || application.getStatus() == InternshipApplication.Status.UNSUCCESSFUL) {
            throw new IllegalStateException("This Internship application cannot be withdrawn from.");
        }

        if (application.getStatus() == InternshipApplication.Status.PENDING || application.getStatus() == InternshipApplication.Status.SUCCESSFUL) {
            application.requestWithdrawal();
            return;
        }


    }
    
}
