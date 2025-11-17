package model;

public class CompanyRepresentative extends User {
    private Company c;
    private String dept;
    private String position;
    private boolean isApproved;

    public CompanyRepresentative(String id, String name, String email, Company c, String dept, String position) {
        super(id, position, email);
        this.c = c;
        this.dept = dept;
        this.position = position;

        isApproved = false;
    }

    public String getDept() {
        return dept;
    }

    public String getPosition() {
        return position;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    @Override
    public String getLoginId() {
        return super.getEmail();
    }

    public Company getCompany() {
        return this.c;
    }


}
