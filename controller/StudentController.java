package controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import model.Internship;
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

    public void apply(Student student, Internship internship) {
        
    }
    
}
