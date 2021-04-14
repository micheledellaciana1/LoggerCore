package LoggerCore.Menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import LoggerCore.DataManger2;
import LoggerCore.GlobalVar;
import LoggerCore.LoggerFrame;
import LoggerCore.Menu.BasicMenu;

public class MenuLoggerFrameImportTxtFile extends BasicMenu {

    private ArrayList<ArrayList<Double>> _columns;
    private ArrayList<String> _headers;
    private LoggerFrame _logger;

    public MenuLoggerFrameImportTxtFile(LoggerFrame logger, String name) {
        super(name);
        _logger = logger;
        _columns = new ArrayList<ArrayList<Double>>();
        _headers = new ArrayList<String>();
    }

    private File _currentDirectory = null;

    @Override
    public JMenu BuildDefault() {
        removeAll();

        add(BuildImportFileItem("Import data", false));
        add(BuildImportFileItem("Add data", true));
        add(BuildHelpMenu("Help",
                "Import menu:\n Data are loaded from csv files in a list of columns of data.\n Loaded data are displayed in a frame that pops when data are imported/added.\n To import/add data, select the file or files using the file chooser menu.\n Files are parsed guesting the used divider, text intestation is ignored.\n Multiple files are sorted depending on the creation data.\nPopulation members column considers the elements in a column as the mebers of a single population and it is builded the hinstogram with a setted number of bin."));

        return this;
    }

    public JMenu BuildAssociateSeriesMenu() {

        JMenu LoadSeriesMenu = new JMenu("Associate column");
        LoadSeriesMenu.add(BuildAssociateSerie("Associate columns"));
        LoadSeriesMenu.add(BuildAssociateConsecutiveSerie("Associate consecutive columns"));
        LoadSeriesMenu.add(BuildAssociatePopulationSerie("Population members column"));

        return LoadSeriesMenu;
    }

    public JMenuItem BuildImportFileItem(String name, final boolean addToLoaded) {
        return BuildNoArgMenuItem(new NoInputAction(name) {
            @Override
            public void actionPerformed() {
                JFileChooser fileChooser = new JFileChooser(_currentDirectory);
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setDialogTitle("Open");

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File fileToOpen[] = fileChooser.getSelectedFiles();
                    _currentDirectory = fileChooser.getCurrentDirectory();

                    sort(fileToOpen);
                    if (!addToLoaded) {
                        _columns = new ArrayList<ArrayList<Double>>();
                        _headers = new ArrayList<String>();
                        _logger.removeAllSeries();
                    }
                    for (File file : fileToOpen) {
                        ArrayList<ArrayList<Double>> newColumns = new ArrayList<ArrayList<Double>>();
                        newColumns = GlobalVar.defaultDataManager.parseDoubleColumnsAUTO(file);
                        _columns = DataManger2.combineColumns(_columns, newColumns);
                        for (int i = 0; i < newColumns.size(); i++)
                            _headers.add(file.getName() + "/" + "Columns" + i);

                        _logString = "Imported: " + file.getAbsolutePath();
                    }

                    showDataLabels("Loaded Data");
                }
            }
        });
    }

    public JMenuItem BuildAssociatePopulationSerie(final String name) {
        return BuildArgStringMenuItem(new InputStringAction(name, "Enter: <PopIdx> <NumberBinning> <key>", name,
                "0 100 key0", "Create Popolation") {
            @Override
            public void actionPerformed(String input) {
                try {
                    String args[] = input.split(" ");
                    for (int i = 0; i < args.length;) {
                        int PopIdx = Integer.valueOf(args[i++]);
                        int NumberBinning = Integer.valueOf(args[i++]);

                        if (_logger.addXYSeries(
                                DataManger2.createHistogram(args[i], _columns.get(PopIdx), NumberBinning), "X",
                                "Freq.")) {
                            _logger.DisplayXYSeries(args[i++]);
                        }
                    }

                } catch (Exception _e) {
                    if (verbose)
                        _e.printStackTrace();
                }
            }
        });
    }

    public JMenuItem BuildAssociateSerie(final String name) {
        return BuildArgStringMenuItem(
                new InputStringAction(name, "Enter: <XIdx> <YIdx> <key>", name, "0 1 key0", "Associate") {
                    @Override
                    public void actionPerformed(String input) {
                        try {
                            String args[] = input.split(" ");
                            for (int i = 0; i < args.length;) {
                                int XIdx = Integer.valueOf(args[i++]);
                                int YIdx = Integer.valueOf(args[i++]);

                                if (_logger.addXYSeries(
                                        DataManger2.combineColumns(args[i], _columns.get(XIdx), _columns.get(YIdx)))) {
                                    _logger.DisplayXYSeries(args[i++]);
                                }
                            }

                        } catch (Exception _e) {
                            if (verbose)
                                _e.printStackTrace();
                        }
                    }
                });
    }

    private File[] sort(File[] files) {

        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
            }
        });

        return files;
    }

    public JMenuItem BuildAssociateConsecutiveSerie(final String name) {
        return BuildArgStringMenuItem(new InputStringAction(name, "Enter: <CommonKey>", name, "key", name) {
            public void actionPerformed(String input) {
                String key = input;

                int IdxX = 0;
                int IdxY = 1;

                for (int i = 1; IdxY < _columns.size(); i++) {
                    _logger.addXYSeries(DataManger2.combineColumns(key + i, _columns.get(IdxX), _columns.get(IdxY)));
                    if (_logAction)
                        _logBook.log("Associate: " + IdxX + " " + IdxY + " " + key + i);

                    IdxX = i * 2;
                    IdxY = i * 2 + 1;
                }

                _logger.DisplaEveryLoadedSeries();
            }
        });
    }

    private JFrame frameColumns = null;

    public void showDataLabels(String title) {
        if (frameColumns != null)
            frameColumns.dispose();

        frameColumns = new JFrame(title);

        frameColumns.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String message = new String();

        for (int i = 0; i < _columns.size(); i++)
            message += ("Column" + i + ": " + _headers.get(i) + "\n");

        JTextArea textArea = new JTextArea(message);
        textArea.setEditable(false);
        JScrollPane _panel = new JScrollPane(textArea);

        frameColumns.getContentPane().add(_panel);
        frameColumns.pack();
        frameColumns.setVisible(true);
    }
}
