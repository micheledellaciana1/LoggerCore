package LoggerCore;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import LoggerCore.Menu.MenuLoggerFrameDisplay;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;

public class LoggerFrameMinimal extends LoggerFrame {

    protected MenuLoggerFrameDisplay _displayMenu;
    protected MenuLoggerFrameExportTxtFile _exportMenu;
    protected MenuLoggerFrameFile _menuFile;
    protected JMenu _eraseMenu;

    public LoggerFrameMinimal(boolean includeFIFO, boolean includeEraseSeries, boolean includeEraseDataSeries,
            boolean includeEraseSelectedSeries) {
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

        _eraseMenu = new JMenu("Erase");
        if (includeEraseSeries)
            _eraseMenu.add(_menuFile.BuildEraseSeriesItem("Erase all series"));

        if (includeEraseDataSeries)
            _eraseMenu.add(_menuFile.BuildEraseSeriesDataItem("Erase all data", true));

        if (includeEraseSelectedSeries)
            _eraseMenu.add(_menuFile.BuildEraseSelectedSeriesItem("Erase selected series"));

        _menuFile.add(_eraseMenu);
        _menuFile.add(_menuFile.BuildDuplicateItem(includeFIFO));

        menuBar.add(_menuFile);
        menuBar.add(_displayMenu);

        this.setJMenuBar(menuBar);
    }

    public JMenuItem BuildDisplayFrameMenuItem(String label) {
        JMenuItem menuItem = new JMenuItem(new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(true);
            }
        });
        return menuItem;
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
