
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class JFilePickerUI extends JPanel {
    private String textFieldLabel;
    private String buttonLabel;
    public String filePath = "";
    private JLabel label;
    private JTextField textField;
    private JButton button;
    private JButton btnClear;

    private JFileChooser fileChooser;

    private int mode;
    public static final int MODE_OPEN = 1;
    public static final int MODE_SAVE = 2;

    public JFilePickerUI(String textFieldLabel, String buttonLabel) {
        this.textFieldLabel = textFieldLabel;
        this.buttonLabel = buttonLabel;
        this.setPreferredSize(new Dimension(600,60));
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 15));
        Dimension d = new Dimension(100, 30);

        fileChooser = new JFileChooser();
        label = new JLabel(textFieldLabel);
        textField = new JTextField(20);
        textField.setEditable(false);

        button = new JButton(buttonLabel);
        button.setPreferredSize(d);
        btnClear = new JButton("Usuń");
        btnClear.setPreferredSize(d);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                buttonActionPerformed(evt);
            }
        });
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                textField.setText("");
            }
        });

        add(label);
        add(textField);
        add(button);
        add(btnClear);

    }
        private void buttonActionPerformed (ActionEvent evt){
            if (mode == MODE_OPEN) {
                if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    this.filePath = textField.getText();
                }
            } else if (mode == MODE_SAVE) {
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    textField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    this.filePath = textField.getText();
                }
            }
        }

        public void addFileTypeFilter (String extension, String description) {
            FileTypeFilter filter = new FileTypeFilter(extension, description);
            fileChooser.addChoosableFileFilter(filter);
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(filter);
        }

        public void setMode (int mode) {
            this.mode = mode;
        }

        public String getSelectedFilePath () {
            return textField.getText();
        }

        public JFileChooser getFileChooser () {
            return this.fileChooser;
        }

    }

