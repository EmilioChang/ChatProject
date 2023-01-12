package chatClient;

import chatClient.presentation.Controller;
import chatClient.presentation.Model;
import chatClient.presentation.View;

import javax.swing.*;

public class Application {
    public static JFrame window;
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");}
        catch (Exception ex) {};
        window = new JFrame();
        Model model= new Model();
        View view = new View();
        Controller controller = new Controller(view, model);
        window.setSize(700,450);
        window.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        window.setTitle("CHAT");
        window.setContentPane(view.getPanel());
        window.setVisible(true);
    }
}
