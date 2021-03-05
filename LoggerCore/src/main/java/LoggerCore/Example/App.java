package LoggerCore.Example;

import javax.swing.JMenuBar;

import LoggerCore.LinkedPointAnalysis;
import LoggerCore.LoggerFrame;
import LoggerCore.PointsArrayStream;
import LoggerCore.PointsStream;
import LoggerCore.Menu.MenuDevice;
import LoggerCore.Menu.MenuLoggerFrameDisplay;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;
import LoggerCore.Menu.MenuRoutine;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    /**
     * Says hello to the world.
     * 
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        DummyDevice dd = new DummyDevice("dd");

        PointsStream ps = new RandomPointsStream();
        PointsArrayStream pas = new RandomPointsArrayStream();

        LinkedPointAnalysis lp2 = new LinkedRandomPointsArrayStream();
        pas.getLinkedAnalysisCollection().add(lp2);

        LoggerFrame app = new LoggerFrame();
        app.addXYSeries(pas.getSeries(), "au", "au");
        app.addXYSeries(lp2.getSeries(), "Time [ms]", "au");
        app.addXYSeries(ps.getSeries(), "Time [ms]", "au");

        JMenuBar menubar = new JMenuBar();
        MenuLoggerFrameDisplay displayMenu = new MenuLoggerFrameDisplay(app, "Display");
        displayMenu.add(displayMenu.BuildSubMenu("SubMenu1",
                new String[] { "Random Points Array Stream", "Linked Random Points Array Stream" }));
        displayMenu.add(displayMenu.BuildSubMenu("SubMenu2", new int[] { 1 }));
        displayMenu.add(displayMenu.BuildSubMenu("SubMenu3", new int[] { 0, 1, 2 }));

        MenuLoggerFrameFile fileMenu = new MenuLoggerFrameFile(app, "Edit");
        fileMenu.add(fileMenu.BuildPropertyChartMenu(true));
        MenuLoggerFrameExportTxtFile menuLoggerFrameExporttxt = new MenuLoggerFrameExportTxtFile(app, "Export .txt");
        fileMenu.add(menuLoggerFrameExporttxt.BuildDefault());
        fileMenu.add(fileMenu.BuildDuplicateItem(true));

        MenuDevice deviceMenu = new MenuDevice("Command", dd);
        deviceMenu.add(deviceMenu.BuildStringCommandItem("Parrot", "write something:", "Parrot"));
        deviceMenu.add(deviceMenu.BuildNoArgCommandItem("SayNothing", "SayNothing"));
        deviceMenu.add(deviceMenu.BuildDeviceMonitorFrame("Monitor Status", "Example"));

        MenuRoutine _MenuRoutine = new MenuRoutine("Routine");
        _MenuRoutine.add(_MenuRoutine.BuildNoArgRoutineItem("r1", ps));
        _MenuRoutine.add(_MenuRoutine.BuildNoArgRoutineItem("r2", pas));

        menubar.add(fileMenu);
        menubar.add(displayMenu);
        menubar.add(deviceMenu);
        menubar.add(_MenuRoutine);

        app.setJMenuBar(menubar);
        app.setVisible(true);
    }
}
