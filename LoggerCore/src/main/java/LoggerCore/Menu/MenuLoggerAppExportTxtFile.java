package LoggerCore.Menu;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.jorphan.gui.MenuScroller;

import LoggerCore.LoggerApp;

public class MenuLoggerAppExportTxtFile extends BasicMenu {

    protected LoggerApp _logger;
    protected static File _currentDirectory = null;

    public MenuLoggerAppExportTxtFile(LoggerApp Logger, String name) {
        super(name);
        setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        _logger = Logger;
    }

    public MenuLoggerAppExportTxtFile BuildDefault() {
        MenuScroller.setScrollerFor(this, 10);

        removeAll();
        for (int i = 0; i < _logger.getLoadedDataset().getSeriesCount(); i++)
            add(BuildExportTextSeriesItem(i));

        add(BuildExportEverySeriesItem("Export every series .txt", " "));
        return this;
    }

    public MenuListener BuildDefaultMenuListener() {
        MenuListener menuListener = new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                BuildDefault();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        };
        return menuListener;
    }

    public JMenu BuildSubMenu(String name, int[] indexSeries) {
        JMenu subMenu = new JMenu(name);

        for (int i = 0; i < indexSeries.length; i++)
            subMenu.add(BuildExportTextSeriesItem(indexSeries[i]));

        return subMenu;
    }

    public JMenu BuildSubMenu(String name, Comparable<?>[] keys) {
        JMenu subMenu = new JMenu(name);

        for (int i = 0; i < keys.length; i++)
            subMenu.add(BuildExportTextSeriesItem(keys[i]));

        return subMenu;
    }

    public JMenuItem BuildExportTextSeriesItem(final int index) {
        return BuildNoArgMenuItem(new NoInputAction(_logger.getLoadedDataset().getSeriesKey(index).toString()) {
            @Override
            public void actionPerformed() {
                JFileChooser fileChooser = new JFileChooser(_currentDirectory);
                fileChooser.setDialogTitle("Select destination file");

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    _currentDirectory = fileChooser.getCurrentDirectory();

                    if (fileToSave.exists())
                        switch (JOptionPane.showConfirmDialog(null,
                                "File " + fileToSave.getAbsolutePath() + " exists! Overwrite?", "Overwrite",
                                JOptionPane.YES_NO_OPTION)) {
                            case 0:
                                break;

                            default:
                                return;
                        }

                    _logger.ExportTextXYSeries(fileToSave, index);

                    if (_logAction)
                        _logBook.log("Exported " + (String) _logger.getLoadedDataset().getSeriesKey(index) + " at: "
                                + fileToSave.getAbsolutePath());
                }
            }
        });
    }

    public JMenuItem BuildExportTextSeriesItem(final Comparable key) {
        return BuildExportTextSeriesItem(_logger.getLoadedDataset().getSeriesIndex(key));
    }

    public JMenuItem BuildExportEverySeriesItem(String name, final String Devider) {
        return BuildNoArgMenuItem(new NoInputAction(name) {
            @Override
            public void actionPerformed() {
                JFileChooser fileChooser = new JFileChooser(_currentDirectory);
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setDialogTitle("Select destination directory");

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File folder = fileChooser.getSelectedFile();
                    _currentDirectory = fileChooser.getCurrentDirectory();
                    _logger.ExportEveryLoadeXYSeries(folder);

                    if (_logAction)
                        _logBook.log("Exported every series at: " + folder.getAbsolutePath());
                }
            }
        });
    }
}
