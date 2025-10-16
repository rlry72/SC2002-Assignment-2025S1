public class Staff extends User {
    private String role;
    private String dept;

    public Staff(String id, String name, String email, String role, String dept) {
        super(id, name, email);
        this.role = role;
        this.dept = dept;
    }

    
}
