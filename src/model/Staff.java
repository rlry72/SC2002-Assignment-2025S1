package model;

/**
 * represent a staff user in the system
 * staff are responsible for approving company representatives, internship postings,
 * withdrawal requests and generating reports
 */
public class Staff extends User {
    /** staff job role or position */
    private String role;

    /** staff department name */
    private String dept;

    /**
     * create a staff account
     * @param id unique staff id
     * @param name full name of staff
     * @param email staff email used as login id
     * @param role staff job role or position
     * @param dept staff department name
     */
    public Staff(String id, String name, String email, String role, String dept) {
        super(id, name, email);
        this.role = role;
        this.dept = dept;
    }

    /**
     * get staff job role
     * @return role of staff
     */
    public String getRole() {
        return role;
    }

    /**
     * update staff job role
     * @param role new role value
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * get staff department
     * @return department name
     */
    public String getDept() {
        return dept;
    }

    /**
     * update staff department
     * @param dept new department value
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * get staff email
     * @return email value
     */
    @Override
    public String getEmail() {
        return super.getEmail();
    }

    /**
     * update staff email
     * @param email new staff email to set
     */
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    /**
     * get login id for staff user
     * uses email as system login identifier
     * @return email used to log in
     */
    @Override
    public String getLoginId() {
        return super.getEmail();
    }
}
