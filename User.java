public class User {
    private String userId;
    private String name;
    private String password = "password";

    private boolean isLoggedIn;

    public User(String id, String n) {
        userId = id;
        name = n;
        // password = pw;
        isLoggedIn = false;
    }


    // methods - to be abstract?
    public void login(String userId) {

    }

    public void logout() {
        isLoggedIn = false;
    }

    public void changePassword(String newPw) {
        this.password = newPw;
    }
}
