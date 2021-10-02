import javax.swing.*;

/**
 * @author Marek Popowicz, grudzień 2018.
 */
public class WorkingHours {

    
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                WorkingHoursUI ui = new WorkingHoursUI();
                ui.setResizable(false);
                ui.setTitle("Generator Karty Czasu Pracy (c) 2018 Marek Popowicz");
                ui.setPosition();
                ui.setVisible(true);
                //ui.setAlwaysOnTop(true);
                ui.pack();
            }
        });
    }
}






  
