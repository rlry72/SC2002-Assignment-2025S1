public abstract class User {
    private String userId;
    private String name;
    private String password = "password";
    private String email;

    private boolean isLoggedIn;

    public User(String id, String n, String email) {
        userId = id;
        name = n;
        // password = pw;
        this.email = email;
        isLoggedIn = false;
    }


    // methods - to be abstract?
    public abstract void login();

    public void logout() {
        isLoggedIn = false;
    }

    public void changePassword(String newPw) {
        this.password = newPw;
    }
}
