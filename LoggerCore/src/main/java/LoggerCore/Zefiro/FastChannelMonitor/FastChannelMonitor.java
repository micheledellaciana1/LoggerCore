package LoggerCore.Zefiro.FastChannelMonitor;

import java.awt.geom.Point2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import LoggerCore.LoggerApp;
import LoggerCore.PointsArrayStream;
import LoggerCore.Menu.MenuLoggerAppFile;
import LoggerCore.Zefiro.Zefiro;

public class FastChannelMonitor extends PointsArrayStream {

    LoggerApp _FastMonitorApp;
    Zefiro _zef;

    String CommandArg;

    public FastChannelMonitor(Zefiro zef) {
        super("FastChannelMonitor");

        _zef = zef;

        _FastMonitorApp = new LoggerApp();
        _FastMonitorApp.setTitle("Fast channel Monitor");
        _FastMonitorApp.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        _FastMonitorApp.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                _FastMonitorApp.setVisible(false);
                if (getisRunning())
                    kill();
            }
        });

        MenuLoggerAppFile fileMenu = new MenuLoggerAppFile(_FastMonitorApp, "File");
        fileMenu.add(fileMenu.BuildPropertyChartMenu(false));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);

        _FastMonitorApp.setJMenuBar(menuBar);
        _FastMonitorApp.addXYSeries(_points, "time[us]", "Voltage[V]");
        _FastMonitorApp.DisplayXYSeries("FastChannelMonitor");
    }

    @Override
    public boolean setup(Object arg) {

        CommandArg = String.class.cast(arg);
        try {
            String args[] = CommandArg.split(" ");
            Integer.valueOf(args[0]);
            Integer.valueOf(args[1]);
        } catch (Exception e) {
            if (verbose)
                e.printStackTrace();
            return false;
        }

        _FastMonitorApp.setVisible(true);
        return true;
    }

    @Override
    public ArrayList<Point2D> acquirePoints() {
        return (ArrayList<Point2D>) _zef.executeCommand("ReadFastValues", CommandArg);
    }
}
