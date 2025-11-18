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

/**
 * controller class responsible for student-related internship operations
 * handles internship searching, eligibility checking, application submission,
 * acceptance actions, and withdrawal logic
 */
public class StudentController {

    /** repository storing all internships */
    private final InternshipRepository internships;

    /** repository storing all internship applications */
    private final InternshipAppRepository applications;

    /** repository storing all registered users */
    private final UserRepository users;

    /**
     * construct controller with required repositories
     * @param internshipRepo repository containing internship records
     * @param appRepo repository containing internship application records
     * @param userRepo repository containing user records
     */
    public StudentController(InternshipRepository internshipRepo,
                             InternshipAppRepository appRepo,
                             UserRepository userRepo) {
        this.internships = internshipRepo;
        this.applications = appRepo;
        this.users = userRepo;
    }

    /**
     * retrieve list of eligible internships for student
     * @param s student requesting results
     * @return sorted list of eligible internships
     */
    public List<Internship> getEligibleInternships(Student s) {
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

    /**
     * retrieve list of eligible internships for student using additional custom filters
     * @param s student requesting results
     * @param filter applied filter criteria
     * @return sorted list of eligible internships matching both eligibility and filter rules
     */
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

    /**
     * retrieve all internship applications created by the given student
     * @param s student searching their applications
     * @return list of internship applications submitted by student
     */
    public List<InternshipApplication> getInternshipApplications(Student s) {
        return applications.findByStudent(s.getUserId());
    }

    /**
     * find internship by id
     * @param internshipId internship identifier string
     * @return internship if found, null otherwise
     */
    public Internship getInternshipById(String internshipId) {
        return internships.findById(internshipId).orElse(null);
    }

    /**
     * check if student is permitted to apply to specified internship level
     * year 1–2 students can only apply for BASIC level
     * @param student student requesting validation
     * @param internship internship being evaluated
     * @return true if permitted, false otherwise
     */
    private boolean isLevelAllowed(Student student, Internship internship) {
        return !(student.getYearOfStudy() < 3 && internship.getLevel() != Internship.Level.BASIC);
    }

    /**
     * create new internship application for student
     * @param student user applying
     * @param internship internship being applied for
     * @throws IllegalArgumentException if internship not approved
     * @throws IllegalStateException if rules violated
     */
    public void applyInternship(Student student, Internship internship) {

        if (internship.getStatus() != Internship.Status.APPROVED) {
            throw new IllegalArgumentException("This internship is not approved yet!");
        }

        boolean hasAccepted = applications.findByStudent(student.getUserId()).stream()
            .anyMatch(app -> app.getStatus() == InternshipApplication.Status.SUCCESSFUL && app.studentAccepted());
        if (hasAccepted) {
            throw new IllegalStateException("You already have an accepted internship placement and cannot apply for new ones.");
        }

        long activeCount = applications.findByStudent(student.getUserId()).stream()
            .filter(app ->
                app.getStatus() == InternshipApplication.Status.PENDING ||
                (app.getStatus() == InternshipApplication.Status.SUCCESSFUL && !app.studentAccepted())
            )
            .count();
        if (activeCount >= 3) {
            throw new IllegalStateException("Maximum of 3 active internship applications allowed!");
        }

        boolean alreadyApplied = applications.findByStudent(student.getUserId()).stream()
            .anyMatch(app -> app.getInternshipId().equals(internship.getId()));
        if (alreadyApplied) {
            throw new IllegalStateException("You have already applied for this internship.");
        }

        InternshipApplication newApp = new InternshipApplication(
            UUID.randomUUID().toString(),
            student.getUserId(),
            internship.getId()
        );

        applications.save(newApp);
    }

    /**
     * accept a successful internship application and auto-withdraw all others
     * @param student requesting student
     * @param internshipApplication application being accepted
     * @param internship internship associated with application
     * @throws IllegalArgumentException if application not owned or not successful
     * @throws IllegalStateException if student already accepted another internship
     */
    public void acceptInternship(Student student,
                                 InternshipApplication internshipApplication,
                                 Internship internship) {

        if (!internshipApplication.getStudentId().equals(student.getUserId()))
            throw new IllegalArgumentException("This application does not belong to you!");

        if (internshipApplication.getStatus() != InternshipApplication.Status.SUCCESSFUL)
            throw new IllegalArgumentException("You can only accept applications that are successful.");

        boolean alreadyAccepted = applications.findByStudent(student.getUserId()).stream()
                .anyMatch(InternshipApplication::studentAccepted);

        if (alreadyAccepted)
            throw new IllegalStateException("You have already accepted an internship and cannot accept another.");

        internshipApplication.accept();
        applications.save(internshipApplication);

        internship.addConfirmedSlot();
        internships.save(internship);

        applications.findByStudent(student.getUserId()).stream()
            .filter(app -> !app.getId().equals(internshipApplication.getId()))
            .forEach(app -> {
                app.setStatus(InternshipApplication.Status.WITHDRAWN);
                applications.save(app);
            });
    }

    /**
     * request withdrawal of an internship application if allowed
     * @param student student requesting withdrawal
     * @param application application to modify
     * @throws IllegalStateException if not owned or not eligible for withdrawal
     */
    public void withdrawFromInternship(Student student, InternshipApplication application) {
        if (!application.getStudentId().equals(student.getUserId())) {
            throw new IllegalStateException("You can only withdraw from Internship applications made by you.");
        }

        if (application.getStatus() == InternshipApplication.Status.WITHDRAWN ||
            application.getStatus() == InternshipApplication.Status.UNSUCCESSFUL) {
            throw new IllegalStateException("This Internship application cannot be withdrawn from.");
        }

        if (application.getStatus() == InternshipApplication.Status.PENDING ||
            application.getStatus() == InternshipApplication.Status.SUCCESSFUL) {
            application.requestWithdrawal();
        }
    }

    /**
     * retrieve student by id
     * @param studentId student identifier
     * @return matching student or null if not found
     */
    public Student getStudentById(String studentId) {
        return users.findById(studentId)
                    .filter(u -> u instanceof Student)
                    .map(u -> (Student) u)
                    .orElse(null);
    }
}
