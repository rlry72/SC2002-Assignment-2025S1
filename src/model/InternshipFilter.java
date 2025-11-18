package model;

import java.time.LocalDate;

/**
 * represent a filter object used to search, sort or narrow internship results
 * all attributes are optional; null values indicate no filtering condition for that field
 * this class uses method chaining for cleaner filter building
 */
public class InternshipFilter {

    /** filter by internship status */
    private Internship.Status status;

    /** filter by preferred major */
    private String major;

    /** filter by internship level */
    private Internship.Level level;

    /** filter by company name */
    private String companyName;

    /** minimum number of remaining internship slots allowed */
    private Integer slotsMin;

    /** maximum number of remaining internship slots allowed */
    private Integer slotsMax;

    /** minimum internship start date allowed */
    private LocalDate startDate;

    /** maximum internship end date allowed */
    private LocalDate endDate;

    /** filter by company representative user id */
    private String repId;

    /**
     * create an empty filter where all conditions are unset
     */
    public InternshipFilter() {}

    /**
     * get internship status filter value
     * @return internship status or null if not filtered
     */
    public Internship.Status getStatus() { return status; }

    /**
     * get major filter value
     * @return major string or null if not filtered
     */
    public String getMajor() { return major; }

    /**
     * get internship level filter value
     * @return internship level or null if not filtered
     */
    public Internship.Level getLevel() { return level; }

    /**
     * get company name filter value
     * @return company name string or null if not filtered
     */
    public String getCompanyName() { return companyName; }

    /**
     * get minimum remaining slots filter value
     * @return integer minimum slots or null if not filtered
     */
    public Integer getSlotsMin() { return slotsMin; }

    /**
     * get maximum remaining slots filter value
     * @return integer maximum slots or null if not filtered
     */
    public Integer getSlotsMax() { return slotsMax; }

    /**
     * get minimum allowed start date filter
     * @return localdate minimum start date or null if not filtered
     */
    public LocalDate getStartDate() { return startDate; }

    /**
     * get maximum allowed end date filter
     * @return localdate end date or null if not filtered
     */
    public LocalDate getEndDate() { return endDate; }

    /**
     * get company representative id filter
     * @return user id string or null if not filtered
     */
    public String getRepId() { return repId; }

    /**
     * set internship status filter
     * @param status internship status
     * @return current filter object for chaining
     */
    public InternshipFilter setStatus(Internship.Status status) {
        this.status = status;
        return this;
    }

    /**
     * set major filter
     * @param major preferred major string
     * @return current filter object for chaining
     */
    public InternshipFilter setMajor(String major) {
        this.major = major;
        return this;
    }

    /**
     * set internship level filter
     * @param level internship level
     * @return current filter object for chaining
     */
    public InternshipFilter setLevel(Internship.Level level) {
        this.level = level;
        return this;
    }

    /**
     * set company name filter
     * @param companyName desired company name
     * @return current filter object for chaining
     */
    public InternshipFilter setCompanyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    /**
     * set minimum number of remaining slots
     * @param slotsMin minimum value allowed
     * @return current filter object for chaining
     */
    public InternshipFilter setSlotsMin(Integer slotsMin) {
        this.slotsMin = slotsMin;
        return this;
    }

    /**
     * set maximum number of remaining slots
     * @param slotsMax maximum value allowed
     * @return current filter object for chaining
     */
    public InternshipFilter setSlotsMax(Integer slotsMax) {
        this.slotsMax = slotsMax;
        return this;
    }

    /**
     * set earliest allowed internship start date
     * @param startDate filter minimum start date
     * @return current filter object for chaining
     */
    public InternshipFilter setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    /**
     * set latest allowed internship end date
     * @param endDate filter maximum end date
     * @return current filter object for chaining
     */
    public InternshipFilter setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    /**
     * set company representative id filter
     * @param repId unique id of representative user
     * @return current filter object for chaining
     */
    public InternshipFilter setRepId(String repId) {
        this.repId = repId;
        return this;
    }
}
