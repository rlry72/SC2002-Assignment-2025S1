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
import repository.InternshipAppRepository;
import repository.InternshipRepository;
import repository.UserRepository;

public class StaffController {
    private final UserRepository users;
    private final InternshipRepository internships;
    private final InternshipAppRepository applications;

    public StaffController(UserRepository userRepository, InternshipRepository internshipRepository, InternshipAppRepository appRepository) {
        this.users = userRepository;
        this.internships = internshipRepository;
        this.applications = appRepository;
    }
    
    public List<CompanyRepresentative> getPendingCompanyReps() {
        return users.getAllCompanyRepresentatives().stream().filter(rep -> !rep.isApproved()).toList();
    }

    public void approveCompanyRep(CompanyRepresentative cr) {
        cr.setApproved(true);
        users.save(cr);
    }

    public void rejectCompanyRep(CompanyRepresentative cr) {
        cr.setApproved(false);
        users.save(cr);
    }

    public List<Internship> getPendingInternships() {
        return internships.findByStatus(Internship.Status.PENDING);
    }

    public void approveInternship(Internship internship) {
        internship.setStatus(Internship.Status.APPROVED);
        internships.save(internship);
    }

    public void rejectInternship(Internship internship) {
        internship.setStatus(Internship.Status.REJECTED);
        internships.save(internship);
    }

    public List<InternshipApplication> getWithdrawalRequests() {
        return applications.findWithdrawalRequests();
    }

    public void approveWithdrawal(InternshipApplication application) {
        application.setStatus(InternshipApplication.Status.WITHDRAWN);
        applications.save(application);
    }

    public void rejectWithdrawal(InternshipApplication application) {
        application.setStatus(InternshipApplication.Status.PENDING);
        applications.save(application);
    }

    // public List<Internship> generateReport(Internship.Status status, String major, Internship.Level level, String companyName, Integer remainingSlotsMin, Integer remainingSlotsMax) {
    //     return internships.filter(status, major, level, companyName, remainingSlotsMin, remainingSlotsMax);
    // }

    public List<Internship> sortByRemainingSlots() {
        return internships.findAll().stream().sorted((a, b) -> Integer.compare(b.getRemainingSlots(), a.getRemainingSlots())).toList();
    }

    public List<Internship> getByCompany(String companyName) {
        return internships.findAll().stream().filter(i -> i.getCompanyName().equalsIgnoreCase(companyName)).toList();
    }

    public Map<String, List<Internship>> groupByCompany() {
        return internships.findAll().stream().collect(Collectors.groupingBy(i -> i.getCompanyName()));
    }

    public List<Internship> getByRepresentative(String repId) {
        return internships.findAll().stream().filter(i -> i.getCr().getUserId().equals(repId)).toList();
    }

    public List<Internship> getAll() {
        return internships.filter(new InternshipFilter()); // empty filter → return all
    }

    public List<Internship> filterByStatus(Internship.Status status) {
        return internships.filter(new InternshipFilter().setStatus(status));
    }

    public List<Internship> filterByMajor(String major) {
        return internships.filter(new InternshipFilter().setMajor(major));
    }

    public List<Internship> filterByLevel(Internship.Level level) {
        return internships.filter(new InternshipFilter().setLevel(level));
    }

    public List<Internship> filterByCompany(String companyName) {
        return internships.filter(new InternshipFilter().setCompanyName(companyName));
    }

    public List<Internship> filterByRemainingSlotsRange(int min, int max) {
        return internships.filter(new InternshipFilter()
                .setSlotsMin(min)
                .setSlotsMax(max));
    }

    public List<Internship> filterByDateRange(LocalDate start, LocalDate end) {
        return internships.filter(new InternshipFilter()
                .setStartDate(start)
                .setEndDate(end));
    }

    public List<Internship> sortByRemainingSlotsDesc() {
        return getAll().stream()
                .sorted(Comparator.comparingInt(Internship::getRemainingSlots).reversed())
                .toList();
    }

    public List<String> getPopularityReport() {
    return getAll().stream()
            .map(i -> i.getTitle() + " (" + i.getCompanyName() + ") : " +
                    applications.findByInternship(i.getId()).size() + " applications")
            .collect(Collectors.toList());
    }

    public List<Internship> filterByRepresentative(String repId) {
        return internships.filter(new InternshipFilter().setRepId(repId));
    }

}

