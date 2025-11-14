package model;
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
    public void login() {
        
    };

    public void logout() {
        isLoggedIn = false;
    }

    public void changePassword(String newPw) {
        if (isLoggedIn)
            this.password = newPw;
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
        if (this.password.equals(pw))
            return true;
        return false;
    }

    public abstract String getLoginId();
}
