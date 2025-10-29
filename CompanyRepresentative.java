import java.util.Scanner;

public class CompanyRepresentative extends User {
    private Company c;
    private String dept;
    private String position;
    private int noOfInternships = 5;
    private boolean isApproved;

    public CompanyRepresentative(String id, String name, String email, Company c, String dept, String position) {
        super(id, position, email);
        this.c = c;
        this.dept = dept;
        this.position = position;

        isApproved = false;
    }

    public String getLoginId() {
        return super.getEmail();
    }

    @Override
    public void login() {
        String email;
        String username;
        Scanner sc = new Scanner(System.in);
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}
