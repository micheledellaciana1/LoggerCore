package LoggerCore;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

public class LoggerBook extends JFrame {
    public boolean verbose = true;

    public boolean logDate = true;
    private JTextArea _textArea;
    public DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public LoggerBook(String name) {
        super(name);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(BuildFileMenu());
        setJMenuBar(menuBar);

        _textArea = new JTextArea();
        _textArea.setAutoscrolls(true);

        JScrollPane panel = new JScrollPane(_textArea);

        getContentPane().add(panel);
        setSize(400, 200);

        log("Started logger book");
    }

    public void log(String string) {

        String newLine = new String();
        if (logDate)
            newLine += logDate();

        newLine += " ";
        newLine += string;
        newLine += "\n";

        _textArea.append(newLine);
        setVisible(true);
    }

    public void log(double d) {
        log(Double.toString(d));
    }

    public void log(float f) {
        log(Float.toString(f));
    }

    public void log(int i) {
        log(Integer.toString(i));
    }

    public void log(Short s) {
        log(Short.toString(s));
    }

    public void log(Long s) {
        log(Long.toString(s));
    }

    public void log(Boolean b) {
        log(Boolean.toString(b));
    }

    public void log(Number n) {
        log(n.toString());
    }

    protected String logDate() {
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    protected JMenu BuildFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem ExportItem = new JMenuItem(new AbstractAction("Export .txt") {

            File currentDirectory = null;

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser(currentDirectory);
                fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
                fileChooser.setDialogTitle("Save");

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    currentDirectory = fileToSave.getParentFile();

                    if (fileToSave.exists())
                        switch (JOptionPane.showConfirmDialog(null,
                                "File " + fileToSave.getAbsolutePath() + " exists! Overwrite?", "Overwrite",
                                JOptionPane.YES_NO_OPTION)) {
                            case 0:
                                break;

                            default:
                                return;
                        }

                    String path = fileToSave.getAbsolutePath();
                    if (!path.toString().contains(".txt"))
                        fileToSave = new File(path + ".txt");

                    try {
                        Files.write(fileToSave.toPath(), _textArea.getText().getBytes(), StandardOpenOption.CREATE);
                    } catch (IOException e1) {
                        if (verbose)
                            e1.printStackTrace();
                    }
                }
            }
        });

        JMenuItem AppendItem = new JMenuItem(new AbstractAction("Append .txt") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));
                fileChooser.setDialogTitle("Save");

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    try {
                        Files.write(fileToSave.toPath(), _textArea.getText().getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e1) {
                        if (verbose)
                            e1.printStackTrace();
                    }
                }
            }
        });

        fileMenu.add(ExportItem);
        fileMenu.add(AppendItem);

        return fileMenu;
    }
}
