package model;
public abstract class User {
    private String userId;
    private String name;
    private String password = "password";
    private String email;

    private boolean isLoggedIn = false;

    public User(String id, String n, String email) {
        userId = id;
        name = n;
        this.email = email;
    }


    // methods - to be abstract?
    public void login() {
        this.isLoggedIn = true;
    };

    public void logout() {
        isLoggedIn = false;
    }

    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    // public String getPassword() {
    //     return password;
    // }
    public String getUserId() {
        return userId;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public boolean validatePassword(String pw) {
        return this.password.equals(pw);
    }

    public boolean changePassword(String newPw) {
        if (!isLoggedIn)
            return false;
        this.password = newPw;
        return true;
    }

    public abstract String getLoginId();
}
