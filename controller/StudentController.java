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
import repository.UserRepository;

public class StudentController {
    private final InternshipRepository internships;
    private final InternshipAppRepository applications;
    private final UserRepository users;

    public StudentController(InternshipRepository internshipRepo, InternshipAppRepository appRepo, UserRepository userRepo) {
        this.internships = internshipRepo;
        this.applications = appRepo;
        this.users = userRepo;
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
        // Only approved internships can be applied for
        if (internship.getStatus() != Internship.Status.APPROVED) {
            throw new IllegalArgumentException("This internship is not approved yet!");
        }

        // Check if student has already accepted an internship
        boolean hasAccepted = applications.findByStudent(student.getUserId()).stream()
            .anyMatch(app -> 
                app.getStatus() == InternshipApplication.Status.SUCCESSFUL
                && app.studentAccepted()
            );

        if (hasAccepted) {
            throw new IllegalStateException("You already have an accepted internship placement and cannot apply for new ones.");
        }

        // Count current active applications (pending OR successful but not accepted)
        long activeCount = applications.findByStudent(student.getUserId()).stream()
            .filter(app ->
                app.getStatus() == InternshipApplication.Status.PENDING ||
                (app.getStatus() == InternshipApplication.Status.SUCCESSFUL && !app.studentAccepted())
            )
            .count();

        if (activeCount >= 3) {
            throw new IllegalStateException("Maximum of 3 active internship applications allowed!");
        }

        // Prevent duplicate application to the same internship
        boolean alreadyApplied = applications.findByStudent(student.getUserId()).stream()
            .anyMatch(app -> app.getInternshipId().equals(internship.getId()));

        if (alreadyApplied) {
            throw new IllegalStateException("You have already applied for this internship.");
        }

        // Create and save application
        InternshipApplication newApp = new InternshipApplication(
            UUID.randomUUID().toString(),
            student.getUserId(),
            internship.getId()
        );

        applications.save(newApp);
    }


    public void acceptInternship(Student student, InternshipApplication internshipApplication, Internship internship) {
        // Must belong to student
        if (!internshipApplication.getStudentId().equals(student.getUserId()))
            throw new IllegalArgumentException("This application does not belong to you!");

        // Only successful applications can be accepted
        if (internshipApplication.getStatus() != InternshipApplication.Status.SUCCESSFUL)
            throw new IllegalArgumentException("You can only accept applications that are successful.");

        // Check if student has already accepted any internship
        boolean alreadyAccepted = applications.findByStudent(student.getUserId()).stream()
                .anyMatch(app -> app.studentAccepted());

        if (alreadyAccepted)
            throw new IllegalStateException("You have already accepted an internship and cannot accept another.");

        // Accept this internship
        internshipApplication.accept();
        applications.save(internshipApplication);

        internship.addConfirmedSlot();  
        internships.save(internship);

        // Withdraw all other apps
        for (InternshipApplication other : applications.findByStudent(student.getUserId())) {
            if (!other.getId().equals(internshipApplication.getId())) {
                other.setStatus(InternshipApplication.Status.WITHDRAWN);
                applications.save(other);
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

    public Student getStudentById(String studentId) {
        return users.findById(studentId).filter(u -> u instanceof Student).map(u -> (Student) u).orElse(null);
    }
    
}
