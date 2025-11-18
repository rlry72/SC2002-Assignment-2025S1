package controller;

import model.User;
import repository.UserRepository;

/**
 * controller managing login, logout and password change functionality
 * interacts with {@link UserRepository} to authenticate and update user session state
 */
public class LoginController {

    /** repository holding all registered users */
    private final UserRepository userRepo;

    /**
     * create controller with required user repository
     * @param userRepo repository storing all system users
     */
    public LoginController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * authenticate a user using login identifier and password
     * verifies credential match and updates login status on success
     *
     * @param loginId value used to locate user account (email or system id depending on role)
     * @param password raw password entered by user
     * @return logged-in user instance, or null if authentication fails
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
     * log out user if currently authenticated
     * @param user user instance attempting logout
     */
    public void logout(User user) {
        if (user != null && user.isLoggedIn()) {
            user.logout();
        }
    }

    /**
     * attempt to update password for an authenticated user
     * password change is not allowed for non-logged-in users
     *
     * @param user current active authenticated user
     * @param newPwd replacement password value
     * @return true if password updated successfully, false otherwise
     */
    public boolean changePassword(User user, String newPwd) {
        if (user == null || !user.isLoggedIn()) {
            return false; // must be logged in
        }
        return user.changePassword(newPwd);
    }
}
