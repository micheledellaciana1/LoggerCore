package LoggerCore;

import javax.swing.JMenuBar;

import LoggerCore.Menu.MenuLoggerFrameDisplay;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;

public class LoggerFrameMinimal extends LoggerFrame {

    protected MenuLoggerFrameDisplay _displayMenu;
    protected MenuLoggerFrameExportTxtFile _exportMenu;
    protected MenuLoggerFrameFile _menuFile;

    public LoggerFrameMinimal(boolean includeFIFO, boolean includeEraseSeries, boolean includeEraseDataSeries) {
        super();

        JMenuBar menuBar = new JMenuBar();
        _displayMenu = new MenuLoggerFrameDisplay(this, "Display");
        _displayMenu.addMenuListener(_displayMenu.BuildDefaultMenuListener());
        _displayMenu.setAutoscrolls(true);

        _exportMenu = new MenuLoggerFrameExportTxtFile(this, "Export .txt");
        _exportMenu.addMenuListener(_exportMenu.BuildDefaultMenuListener());
        _exportMenu.setAutoscrolls(true);

        _menuFile = new MenuLoggerFrameFile(this, "file");
        _menuFile.add(_menuFile.BuildPropertyChartMenu(false));
        _menuFile.add(_exportMenu);
        if (includeEraseSeries)
            _menuFile.add(_menuFile.BuildEraseSeriesItem("Erase all series"));

        if (includeEraseDataSeries)
            _menuFile.add(_menuFile.BuildEraseSeriesDataItem("Erase all data", true));

        _menuFile.add(_menuFile.BuildDuplicateItem(includeFIFO));

        menuBar.add(_menuFile);
        menuBar.add(_displayMenu);

        this.setJMenuBar(menuBar);
    }

    public MenuLoggerFrameDisplay get_displayMenu() {
        return _displayMenu;
    }

    public MenuLoggerFrameExportTxtFile get_exportMenu() {
        return _exportMenu;
    }

    public MenuLoggerFrameFile get_menuFile() {
        return _menuFile;
    }
}
