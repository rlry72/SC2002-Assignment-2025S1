package controller;

import model.User;
import repository.UserRepository;

public class LoginController {
    /**
     * repository of all users
     */
    private final UserRepository userRepo;

    /**
     * constructor for LoginController
     * @param userRepo repository of all users
     */
    public LoginController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * log in user
     * @param loginId id of user to log in
     * @param password password of user to log in
     * @return user to log in, null if login fails
     */
    public User login(String loginId, String password) {
        User user = userRepo.findByLoginId(loginId).orElse(null);

        if (user == null) {
            return null; // user not found
        }

        if (!user.validatePassword(password)) {
            return null; // incorrect password
        }

        user.login();
        return user;
    }

    /**
     * log user out
     * @param user user to log out
     */
    public void logout(User user) {
        if (user != null && user.isLoggedIn()) {
            user.logout();
        }
    }

    /**
    * attempt to change password of currently logged in user  
    * @param user current logged in user
    * @param newPwd new password
    * @return true if successful, false if not allowed
    */
    public boolean changePassword(User user, String newPwd) {
        if (user == null || !user.isLoggedIn()) {
            return false; // must be logged in
        }
        return user.changePassword(newPwd);
    }
}
