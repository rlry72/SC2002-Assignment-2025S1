package model;

/**
 * represent a company entity that owns internship opportunities
 * used by company representatives to associate internship postings with a company
 */
public class Company {

    /** official company name */
    private String name;

    /**
     * create company object with given name
     * @param name official company name
     */
    public Company(String name) {
        this.name = name;
    }

    /**
     * get company name
     * @return company name
     */
    public String getCompanyName() {
        return name;
    }

    /**
     * update company name
     * @param name new company name
     */
    public void setCompanyName(String name) {
        this.name = name;
    }
}
