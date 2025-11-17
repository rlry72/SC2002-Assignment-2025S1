package model;

import java.time.LocalDate;

public class InternshipFilter {

    private Internship.Status status;
    private String major;
    private Internship.Level level;
    private String companyName;

    // Numeric filtering: null means "no filter"
    private Integer slotsMin;
    private Integer slotsMax;

    // Optional date filtering (null means no filter)
    private LocalDate startDate;
    private LocalDate endDate;
    private String repId;

    public InternshipFilter() {}

    public Internship.Status getStatus() { return status; }
    public String getMajor() { return major; }
    public Internship.Level getLevel() { return level; }
    public String getCompanyName() { return companyName; }
    public Integer getSlotsMin() { return slotsMin; }
    public Integer getSlotsMax() { return slotsMax; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getRepId() { return repId; }

    public InternshipFilter setStatus(Internship.Status status) {
        this.status = status;
        return this;
    }

    public InternshipFilter setMajor(String major) {
        this.major = major;
        return this;
    }

    public InternshipFilter setLevel(Internship.Level level) {
        this.level = level;
        return this;
    }

    public InternshipFilter setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    public InternshipFilter setSlotsMin(Integer slotsMin) {
        this.slotsMin = slotsMin;
        return this;
    }

    public InternshipFilter setSlotsMax(Integer slotsMax) {
        this.slotsMax = slotsMax;
        return this;
    }

    public InternshipFilter setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public InternshipFilter setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public InternshipFilter setRepId(String repId) {
        this.repId = repId;
        return this;
    }
}
