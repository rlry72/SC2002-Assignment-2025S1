package model;
// import java.util.Scanner;

public class Staff extends User {
    private String role;
    private String dept;

    public Staff(String id, String name, String email, String role, String dept) {
        super(id, name, email);
        this.role = role;
        this.dept = dept;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    @Override
    public String getEmail() {
        return super.getEmail();
    }
    @Override
    public void setEmail(String email) {
        super.setEmail(email);
    }

    public String getLoginId() {
        return super.getEmail();
    }
}
