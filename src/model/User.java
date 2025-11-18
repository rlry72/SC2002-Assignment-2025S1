package model;

/**
 * abstract user entity representing all system users
 */
public abstract class User {

    /**
     * unique identifier of user
     */
    private String userId;

    /**
     * name of user
     */
    private String name;

    /**
     * password of user, default value is "password"
     */
    private String password = "password";

    /**
     * email address of user
     */
    private String email;

    /**
     * login state of user, true if logged in
     */
    private boolean isLoggedIn = false;

    /**
     * create user with id, name and email
     * @param id unique id for user
     * @param n name of user
     * @param email email address of user
     */
    public User(String id, String n, String email) {
        userId = id;
        name = n;
        this.email = email;
    }

    /**
     * log in user, sets login state to true
     */
    public void login() {
        this.isLoggedIn = true;
    }

    /**
     * log out user, sets login state to false
     */
    public void logout() {
        isLoggedIn = false;
    }

    /**
     * get email of user
     * @return email of user
     */
    public String getEmail() {
        return email;
    }

    /**
     * get name of user
     * @return name of user
     */
    public String getName() {
        return name;
    }

    /**
     * get user ID
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * update email of user
     * @param email new email value
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * update name of user
     * @param name new name value
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * update user ID
     * @param userId new user ID value
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * check if user is currently logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    /**
     * manually set login state of user
     * @param isLoggedIn new login state
     */
    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    /**
     * check if entered password matches stored password
     * @param pw password to validate
     * @return true if correct password, false otherwise
     */
    public boolean validatePassword(String pw) {
        return this.password.equals(pw);
    }

    /**
     * change password if user is logged in
     * @param newPw new password value
     * @return true if password changed, false if user not logged in
     */
    public boolean changePassword(String newPw) {
        if (!isLoggedIn)
            return false;
        this.password = newPw;
        return true;
    }

    /**
     * get value used for login (varies by user type)
     * @return login ID string
     */
    public abstract String getLoginId();
}
