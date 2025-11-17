package controller;

import model.User;
import repository.UserRepository;

public class LoginController {

    private final UserRepository userRepo;

    public LoginController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Attempts authentication.
     * Returns logged-in User or null if authentication fails.
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
     * Force logout of a user
     */
    public void logout(User user) {
        if (user != null && user.isLoggedIn()) {
            user.logout();
        }
    }

    /**
     * Attempts to change password of currently logged-in user.
     * Returns true if successful, false if not allowed.
     */
    public boolean changePassword(User user, String newPwd) {
        if (user == null || !user.isLoggedIn()) {
            return false; // must be logged in
        }
        return user.changePassword(newPwd);
    }
}
