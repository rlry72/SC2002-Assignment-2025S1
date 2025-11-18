package model;

/**
 * represent a company employee who creates and manages internship postings
 * approval by staff is required before their internships become visible and valid
 * login is performed using company email
 */
public class CompanyRepresentative extends User {

    /** associated company object */
    private Company c;

    /** department of representative */
    private String dept;

    /** job position or title */
    private String position;

    /** approval flag managed by staff */
    private boolean isApproved;

    /**
     * create new unapproved company representative account
     * @param id unique login and identity reference
     * @param name representative name
     * @param email company email used for login
     * @param c associated company
     * @param dept department assigned
     * @param position job position title
     */
    public CompanyRepresentative(String id, String name, String email,
                                 Company c, String dept, String position) {
        super(id, name, email);
        this.c = c;
        this.dept = dept;
        this.position = position;
        this.isApproved = false;
    }

    /** 
     * return department name
     * @return department name 
     */
    public String getDept() {
        return dept;
    }

    /**
     * update department name
     * @param dept department name
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * return job position title 
     * @return job position title 
     */
    public String getPosition() {
        return position;
    }

    /**
     * update job position title
     * @param position job position title
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /** 
     * return approval state
     * @return approval state 
     */
    public boolean isApproved() {
        return isApproved;
    }

    /**
     * update approval state
     * @param isApproved new approval state
     */
    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    /**
     * login id for representative is always email
     * @return email string for authentication
     */
    @Override
    public String getLoginId() {
        return super.getEmail();
    }

    /**
     * get associated company object 
     * @return associated company object
     */
    public Company getCompany() {
        return this.c;
    }

    /**
     * update associated company object
     * @param c new company object
     */
    public void setCompany(Company c) {
        this.c = c;
    }
}
