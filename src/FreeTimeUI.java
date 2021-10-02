
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class FreeTimeUI extends JDialog {

    private Container cntKontener;
    private JPanel panel;
    private JPanel considerPanel;
    private JCheckBox chbConsider;
    private JTextPane textPane;
    private JButton btnOKbutton;
    private String[] items;
    private StringBuilder content = new StringBuilder();
    private WorkingHoursUI workingHoursUI;


    public FreeTimeUI(final WorkingHoursUI workingHoursUI) throws IOException {
        this.setTitle("Dni wolne od pracy w roku " + Calendar.getInstance(Locale.getDefault()).get(Calendar.YEAR));
        this.items = new HolidaysCalendar().getHolydaysPatterns();
        this.workingHoursUI = workingHoursUI;
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setModal(true);
        this. setIconImage(Toolkit.getDefaultToolkit().getImage("virus.jpg"));

        cntKontener = this.getContentPane();
        cntKontener.setLayout(new BoxLayout(cntKontener, 1));
        panel = new JPanel();
        considerPanel = new JPanel();
        chbConsider = new JCheckBox("Uwzgl\u0119dnij");

        chbConsider.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                workingHoursUI.setConsiderFreeTime(chbConsider.isSelected());
            }
        });
        chbConsider.setSelected(workingHoursUI.getConsiderFreeTime());
        chbConsider.setFont(new Font("Tahoma", Font.PLAIN, 13));
        chbConsider.setForeground(Color.BLUE);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        textPane = new JTextPane();
        textPane.setBackground(panel.getBackground());
        textPane.setEditable(false);

        for (int i = 0; i < items.length; i++) {
            content.append("\n" + items[i]);
        }

        btnOKbutton = new JButton("Zamknij");
        btnOKbutton.setPreferredSize(new Dimension(160,35));
        btnOKbutton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FreeTimeUI.this.dispose();
            }
        });
        textPane.setText(content.toString());
        panel.add(textPane);
        considerPanel.add(chbConsider, BorderLayout.EAST,0);
        considerPanel.add(btnOKbutton, BorderLayout.WEST,1);
        cntKontener.add(panel);
        cntKontener.add(considerPanel);
        pack();
    }

}
