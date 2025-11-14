package controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.User;

public class LoginManager {
    List<User> users;
    int studentCount;
    int staffCount;
    int repCount;

    public LoginManager(List<User> u) {
        users = u;
    }

    public User login() {
        User foundUser = null;
        Scanner sc = new Scanner(System.in);
        String id, password, userType;

        System.out.print("Login: please enter user type: ");
        userType = sc.nextLine();

        if (userType.equals("Student")) {
            System.out.print("Please enter your user ID: ");            
        } else if (userType.equals("Staff")) {
            System.out.print("Please enter your Staff ID: ");
        } else if (userType.equals("Company Representative")) {
            System.out.print("Please enter your Company email: ");
        } else {
            System.out.println("No such user type!");
            return null;
        }
        id = sc.nextLine();
        System.out.print("Please enter your password: ");
        password = sc.nextLine();
        for (User user : users) {
            if (user.getLoginId().equals(id)) {
                if (user.validatePassword(password) && user.getClass().getSimpleName().equals(userType)) {
                System.out.println("Logging in as " + user.getClass().getSimpleName());
                System.out.println("ID: " + user.getUserId() + ", Name: " + user.getName());
                foundUser = user;
                } else {
                    System.out.println("Wrong password!");
                }            
                if (foundUser != null) {
                    foundUser.setLoggedIn(true);
                    return foundUser;
                }
            }
        }
        System.out.println("No such user found!");
        return null;
    }

    public User login(String userType) {
        User foundUser = null;
        Scanner sc = new Scanner(System.in);
        String id, password;

        // System.out.print("Login: please enter user type: ");
        // userType = sc.nextLine();

        if (userType.equals("Student")) {
            System.out.print("Please enter your user ID: ");            
        } else if (userType.equals("Staff")) {
            System.out.print("Please enter your Staff email: ");
        } else if (userType.equals("Company Representative")) {
            System.out.print("Please enter your Company email: ");
        } else {
            System.out.println("No such user type!");
            return null;
        }
        id = sc.nextLine();
        System.out.print("Please enter your password: ");
        password = sc.nextLine();
        for (User user : users) {
            if (user.getUserId().equals(id)) {
                if (user.validatePassword(password) && user.getClass().getSimpleName().equals(userType)) {
                System.out.println("Logging in as " + user.getClass().getSimpleName());
                System.out.println("ID: " + user.getUserId() + ", Name: " + user.getName());
                foundUser = user;
                } else {
                    System.out.println("Wrong password!");
                }            
                if (foundUser != null) {
                    foundUser.setLoggedIn(true);
                    return foundUser;
                }
            }
        }
        System.out.println("No such user found!");
        return null;
    }

    public void registerUser() {
        User newUser = null;
        Scanner sc = new Scanner(System.in);
        String id, password, userType;
        System.out.print("Register: please enter user type: ");
        userType = sc.nextLine();

        
    }

    public void changePassword(User u) {
        if (u.isLoggedIn()) {
            String newPw;
            Scanner sc = new Scanner(System.in);

            System.out.print("Please input your new password:");
            newPw = sc.nextLine();
            u.changePassword(newPw);
            u.setLoggedIn(false);
            login(u.getClass().getSimpleName());
        }
    }
}
