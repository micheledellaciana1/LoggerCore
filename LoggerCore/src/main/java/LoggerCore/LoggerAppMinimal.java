package LoggerCore;

import javax.swing.JMenuBar;

import LoggerCore.Menu.MenuLoggerAppDisplay;
import LoggerCore.Menu.MenuLoggerAppExportTxtFile;
import LoggerCore.Menu.MenuLoggerAppFile;

public class LoggerAppMinimal extends LoggerApp {

    public LoggerAppMinimal(boolean includeFIFO, boolean includeEraseSeries) {
        super();

        JMenuBar menuBar = new JMenuBar();
        MenuLoggerAppDisplay _displayMenu = new MenuLoggerAppDisplay(this, "Display");
        _displayMenu.addMenuListener(_displayMenu.BuildDefaultMenuListener());
        _displayMenu.setAutoscrolls(true);

        MenuLoggerAppExportTxtFile _exportMenu = new MenuLoggerAppExportTxtFile(this, "Export .txt");
        _exportMenu.addMenuListener(_exportMenu.BuildDefaultMenuListener());
        _exportMenu.setAutoscrolls(true);

        MenuLoggerAppFile menuFile = new MenuLoggerAppFile(this, "file");
        menuFile.add(menuFile.BuildPropertyChartMenu(false));
        menuFile.add(_exportMenu);
        if (includeEraseSeries)
            menuFile.add(menuFile.BuildEraseSeriesItem("Erase all series"));
        menuFile.add(menuFile.BuildDuplicateItem(includeFIFO));

        menuBar.add(menuFile);
        menuBar.add(_displayMenu);

        this.setJMenuBar(menuBar);
    }

    public void addToAutosave(String relativePath) {
        AutosaveRunnable.getInstance().addDataset(this, relativePath);
    }
}
