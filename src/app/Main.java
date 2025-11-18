package app;

/**
 * entry point of internship placement system
 * intiializes and starts application runtime via AppConfig
 */
public class Main {
    /**
     * main method
     * @param args args
     */
    public static void main(String[] args) {
        AppConfig config = new AppConfig();
        config.start();
    }
}
