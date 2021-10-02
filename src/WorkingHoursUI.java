

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;


/**
 *
 * @author Marek Popowicz
 */
public class WorkingHoursUI extends JFrame {

    private static final int DEFAULT_SIZE = 15;
    private Container cntKontener;
    private final JPanel textPanel;
    private final JPanel buttonPanel;
    private final JPanel monthPanel;
    private final JPanel okPanel;
    private final ButtonGroup group;
    private final JSlider monthSelector;
    private final JLabel monthLabel;
    private final JLabel label;
    private final JButton btnFreeTime;
    private final JFilePickerUI filePicker;
    private final JButton btnOK;
    private final JSeparator separator;
    public String workingHoursMode = "8.5/6.0";
    private int customMonth;
    private boolean considerFreeTime = true;
    private final int currentMonth = Calendar.getInstance(Locale.getDefault()).get(Calendar.MONTH) + 1;
    private final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

    public WorkingHoursUI() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("virus.jpg"));
        textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        okPanel = new JPanel();
        //okPanel.setLayout(new FlowLayout());

        btnFreeTime = new JButton("Dni wolne");
        btnFreeTime.setPreferredSize(new Dimension(120, 30));
        btnFreeTime.addActionListener(new btnFreeTimeActionListener(this));

        btnOK = new JButton("Generuj");
        btnOK.setIcon(new ImageIcon(this.getClass().getResource("virus.jpg")));
        btnOK.setPreferredSize(new Dimension(200, 40));
        btnOK.setFont(new Font("",Font.PLAIN,14));

        btnOK.addActionListener(new btnOKTimeActionListener());

        okPanel.add(btnOK,BorderLayout.CENTER);

        label = new JLabel("Wybierz swój czas pracy:");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        textPanel.add(label);

        group = new ButtonGroup();
        addRadioButton("8.0/8.0", false);
        addRadioButton("8.5/6.0", true);

        monthPanel = new JPanel();
        monthPanel.setBackground(Color.decode("#eff7eb"));
        monthPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        monthLabel = new JLabel("Wskaż miesiąc:");
        monthSelector = new JSlider(1,12, currentMonth);
        customMonth = monthSelector.getValue();
        monthSelector.addChangeListener(new monthListener());
        monthSelector.setPreferredSize(new Dimension(300, 50));
        monthSelector.setMinorTickSpacing(1);
        monthSelector.setMajorTickSpacing(1);
        monthSelector.setPaintTicks(true);
        monthSelector.setPaintLabels(true);
        monthPanel.add(monthLabel);
        monthPanel.add(monthSelector);
        monthPanel.add(btnFreeTime);

        // Ustawienie FilePickera
        filePicker = new JFilePickerUI("\"zadania.txt\"", "Wybierz...");
        filePicker.setMode(JFilePickerUI.MODE_OPEN);
        filePicker.addFileTypeFilter(".txt", "Plik tekstowy");

        JFileChooser fileChooser = filePicker.getFileChooser();
        fileChooser.setCurrentDirectory(new File(""));

        separator = new JSeparator();
        cntKontener = this.getContentPane();
        cntKontener.setLayout(new BoxLayout(cntKontener, BoxLayout.Y_AXIS));
        cntKontener.add(textPanel);
        cntKontener.add(buttonPanel);
        cntKontener.add(monthPanel);
        cntKontener.add(filePicker);
        cntKontener.add(separator);
        cntKontener.add(okPanel);
    }


    public void setConsiderFreeTime(boolean considerFreeTime) {
        this.considerFreeTime = considerFreeTime;
    }

    public boolean getConsiderFreeTime(){
        return this.considerFreeTime;
    }

    private void addRadioButton(String mode, boolean selected)
   {
      JRadioButton button = new JRadioButton(mode, selected);
      group.add(button);
      buttonPanel.add(button);
      button.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent event)
        {
            workingHoursMode = event.getActionCommand();
            label.setText("Wybierz sw\u00f3j czas pracy:");
        }
      });
   }

    public void setPosition() {
        int frameWidth = this.getSize().width;
        int frameHeight = this.getSize().height;
        this.setLocation((screenWidth - frameWidth) / 3, (screenHeight - frameHeight) / 3);
    }

    public class monthListener implements ChangeListener {
        @Override
        public void stateChanged(ChangeEvent e) {
            if (!monthSelector.getValueIsAdjusting()) {
                customMonth = monthSelector.getValue();
            }
        }
    }

    private class btnFreeTimeActionListener implements ActionListener {
        private WorkingHoursUI workingHours;

        btnFreeTimeActionListener(WorkingHoursUI workingHours){
            this.workingHours = workingHours;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                FreeTimeUI freeTimeUI = new FreeTimeUI(this.workingHours);
                freeTimeUI.setVisible(true);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Ooops! Co\u015b posz\u0142o nie tak...","Informacja", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }



    private class btnOKTimeActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {

                if(currentMonth==customMonth) {
                    DataPresenter presenter = new  DataPresenter(workingHoursMode, filePicker.filePath, considerFreeTime);
                    saveDocument(presenter.saveDocument(), presenter.getFilename());
                }
                else {
                    DataPresenter presenter = new  DataPresenter(workingHoursMode, customMonth, filePicker.filePath, considerFreeTime);
                    saveDocument(presenter.saveDocument(), presenter.getFilename());
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Ooops! Co\u015b posz\u0142o nie tak...","Informacja", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void saveDocument(boolean save, String filename){
        if(save==true)
            JOptionPane.showMessageDialog(null, "Karta czasu pracy została wygenerowana do pliku:\n\""+ filename + "\"", "Informacja", JOptionPane.INFORMATION_MESSAGE);
        else JOptionPane.showMessageDialog(null, "Karta czasu pracy nie została wygenerowana.","Informacja", JOptionPane.INFORMATION_MESSAGE);
    }
}
