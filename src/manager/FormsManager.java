package manager;

import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import main.Main;

import javax.swing.*;
import java.awt.*;

public class FormsManager {
    private static FormsManager instance;
    private Main main;

    private FormsManager() {

    }

    // Get the current view we're at
    public static FormsManager getInstance() {
        if (instance == null) {
            instance = new FormsManager();
        }

        return instance;
    }

    public void initApplication(Main main) {
        this.main = main;
    }

    // Sets the view to the current form specified
    public void showForm(JComponent form) {
        EventQueue.invokeLater(() ->
        {
            FlatAnimatedLafChange.showSnapshot();

            main.setContentPane(form);
            main.revalidate();
            main.repaint();

            FlatAnimatedLafChange.hideSnapshotWithAnimation();
        });
    }
}
