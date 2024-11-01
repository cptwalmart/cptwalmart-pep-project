import Controller.SocialMediaController;
import io.javalin.Javalin;

/**
 * This class is provided with a main method to allow you to manually run and test your application. This class will not
 * affect your program in any way and you may write whatever code you like here.
 */
public class Main {
    public static void main(String[] args) {
        try {
            SocialMediaController controller = new SocialMediaController();
            Javalin app = controller.startAPI();
            app.start(8080);
        } catch (Exception e) {
            System.out.println("Error starting Javalin: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
