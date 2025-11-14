package controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import model.Internship;
import model.InternshipApplication;
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

    private boolean isLevelAllowed(Student student, Internship internship) {
        if (student.getYearOfStudy() < 3 && internship.getLevel() != Internship.Level.BASIC)
            return false;
        else
            return true;
    }

    public void applyInternship(Student student, Internship internship) {
        long studentActiveAppCount = applications.findByStudent(student.getUserId()).stream()
            .filter(application -> application.getStatus() == InternshipApplication.Status.PENDING || application.getStatus() == InternshipApplication.Status.SUCCESSFUL)
            .count();

        if (studentActiveAppCount > 3)
            throw new IllegalStateException("Maximum of 3 active Internship Applications allowed!");

            InternshipApplication application = new InternshipApplication(UUID.randomUUID().toString(), student.getUserId(), internship.getId());
            
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
    
}
