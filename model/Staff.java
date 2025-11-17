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

    // // might be replaced with more secure system if possible
    // @Override
    // public void login() {
    //     boolean loginSuccess = false;
    //     String staffId;
    //     String password;

    //     Scanner sc = new Scanner(System.in);
    //     System.out.println("Staff Login");
    //     do {
    //         System.out.print("Please enter Staff ID: ");
    //         staffId = sc.nextLine();
    //         System.out.print("Please enter your password: ");
    //         password = sc.nextLine();
    //         if (checkLoginInfo(staffId, password)) {
    //             loginSuccess = true;            
    //             super.setLoggedIn(loginSuccess);
    //         }
    //     } while (loginSuccess == false);
        
    // }

    // public boolean checkLoginInfo(String staffId, String password) {
    //     if (staffId.equals(this.getUserId()) && this.validatePassword(password))
    //         return true;
    //     else
    //         return false;
    // }
}
