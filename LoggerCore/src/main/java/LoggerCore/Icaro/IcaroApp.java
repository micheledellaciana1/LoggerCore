package LoggerCore.Icaro;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import LoggerCore.*;
import LoggerCore.Menu.*;
import LoggerCore.themal.*;

public class IcaroApp extends LoggerApp {

    private Icaro _ica;
    private Configuration _configFile;
    private FeedBackController_type2 _feedBackController;
    private HeaterTemperatureMonitor _heaterMonitor;

    public IcaroApp() {
        super();
        setTitle("Icaro2");

        // _configFile = new Configuration("ZefiroConfig",
        // System.getProperty("user.dir") + "\\config.txt");

        _configFile = new Configuration("IcaroConfig",
                "C:\\Users\\utente\\Documents\\Visual Studio 2019\\projects\\LoggerCore\\LoggerCore_gui\\LoggerCore\\src\\main\\java\\LoggerCore\\Icaro\\config.txt");

        AutosaveRunnable.getInstance().setAutosavePath(new File(_configFile.search("AutosavePath")));
        int AutosavePeriod = Integer.valueOf(_configFile.search("AutosavePeriodSec")) * 1000;
        AutosaveRunnable.getInstance().setExecutionDelay(AutosavePeriod);
        boolean isASimulation = Boolean.valueOf(_configFile.search("Simulate"));

        if (isASimulation)
            _ica = new Icaro("Zefiro", null, null);
        else
            _ica = new Icaro("Zefiro", null, SerialPort.getCommPorts()[0]);

        _ica.isASimulation = isASimulation;
        _ica.executeCommand("Open", null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showOptionDialog(null, "Close Icaro?", "Exit", JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, new String[] { "Yes", "Cancel", "Force close" }, "Cancel");
                switch (option) {
                    case 0:
                        _ica.executeCommand("Close", null);
                        System.exit(0);
                        break;
                    case 2:
                        JOptionPane.showMessageDialog(null, "Manually turn off Icaro (disconnect USB)", "Force close",
                                JOptionPane.WARNING_MESSAGE);
                        System.exit(1);
                        break;
                    default:
                        break;
                }
            }
        });

        double FB1 = Double.valueOf(_configFile.search("FBPar1"));
        double FB2 = Double.valueOf(_configFile.search("FBPar2"));
        double FB3 = Double.valueOf(_configFile.search("FBPar3"));

        _feedBackController = new FeedBackController_type2(FB1, FB2, FB3);
        _feedBackController.MIN_responce = Double.valueOf(_configFile.search("MinVoltageHeater"));
        _feedBackController.MAX_responce = Double.valueOf(_configFile.search("MaxVoltageHeater"));

        _heaterMonitor = new HeaterTemperatureMonitor(_ica, _feedBackController, "Heater temperature", "Heater current",
                "Target temperature");
        _heaterMonitor.setExecutionDelay(Integer.valueOf(_configFile.search("DelayHeaterAcq")));
        Thread HeaterMonitorThread = new Thread(_heaterMonitor);
        HeaterMonitorThread.start();

        ChamberMonitor cm = new ChamberMonitor(_ica, "Chamber Temperature", "Chamber Relative Humidity");
        cm.setExecutionDelay(Integer.valueOf(_configFile.search("DelayChamberAcq")));
        Thread cmThread = new Thread(cm);
        cmThread.start();

        addXYSeries(_heaterMonitor.getTemperatureSeries(), "time[sec]", "Temperature[°C]");
        addXYSeries(_heaterMonitor.getVoltagetHeaterSeries(), "time[sec]", "Voltage[a.u]");
        addXYSeries(_heaterMonitor.getTargetTemperatureSeries(), "time[sec]", "Temperature[°C]");

        addXYSeries(cm.getTempSeries(), "time[sec]", "Temperature[°C]");
        addXYSeries(cm.getRHSeries(), "time[sec]", "RH[%]");

        MenuLoggerAppDisplay DisplayMenu = new MenuLoggerAppDisplay(this, "Display");
        JMenu subMenuHeater = DisplayMenu.BuildSubMenu("Heater", new int[] { 0, 1, 2 });
        JMenu subMenuChamber = DisplayMenu.BuildSubMenu("Chamber", new int[] { 3, 4 });

        DisplayMenu.add(subMenuHeater);
        DisplayMenu.add(subMenuChamber);

        DisplayMenu.add(DisplayMenu.BuildHideJMenuItem());

        MenuFeedBackController menuFeedback = new MenuFeedBackController("Temperature Feedback", _feedBackController);
        menuFeedback.add(menuFeedback.BuildSetTargetValueItem("Set temperature", "Enter: <Temperature[°C]>", "100"));
        menuFeedback.add(menuFeedback.BuildSetParametersItem("Feedback parameters", "Enter: <Prop.> <Int.> <Diff.>",
                FB1 + " " + FB2 + " " + FB3));
        menuFeedback.add(menuFeedback.BuildFeedBackOnCheckBox("Active feedback"));

        MenuDevice DeviceMenu = new MenuDevice("Icaro", _ica);
        JMenu subMenuGeneral = new JMenu("General");
        JMenu subMenuSet = new JMenu("Set");
        subMenuGeneral.add(DeviceMenu.BuildNoArgCommandItem("Reset", "Reset"));
        JMenu subMenuSetAcq = new JMenu("Acquisition");

        subMenuSetAcq.add(DeviceMenu.BuildStringCommandItem("Set average time", "Enter: <AverageTimeMillis>", "10"));

        subMenuGeneral.add(subMenuSetAcq);
        DeviceMenu.add(subMenuGeneral);
        subMenuSet.add(DeviceMenu.BuildStringCommandItem("Set current heater", "Enter: <VoltageHeater>", "0"));
        DeviceMenu.add(subMenuSet);
        DeviceMenu.add(menuFeedback);

        MenuRoutine mr = new MenuRoutine("Routine");
        String DefTemperatureRamp = _configFile.search("DefTemperatureRamp");
        mr.add(mr.BuildStringRoutineItem("Temperature ramp", new TemperatureRamp(_feedBackController),
                "Enter: <TStart> <TStop> <TStep> <NCycle> <Up&Down> <delay>", DefTemperatureRamp));
        mr.add(mr.BuildStopRoutineItem("Stop routine"));

        MenuLoggerAppExportTxtFile menuExport = new MenuLoggerAppExportTxtFile(this, "Export .txt");
        JMenu subMenuHeaterExp = menuExport.BuildSubMenu("Heater", new int[] { 0, 1, 2 });
        JMenu subMenuChamberExp = menuExport.BuildSubMenu("Chamber", new int[] { 3, 4 });
        menuExport.add(subMenuHeaterExp);
        menuExport.add(subMenuChamberExp);
        menuExport.add(menuExport.BuildExportEverySeriesItem("Export every series .txt", " "));

        MenuLoggerAppFile menuFile = new MenuLoggerAppFile(this, "file");
        menuFile.add(menuFile.BuildPropertyChartMenu(true));
        menuFile.add(menuExport);
        menuFile.add(BuildImportCalibrationItem("Import heater calibration"));
        menuFile.add(menuFile.BuildEraseSeriesDataItem("Delete data", true));
        menuFile.add(menuFile.BuildDuplicateItem(true));

        JMenuBar menubar = new JMenuBar();
        menubar.add(menuFile);
        menubar.add(DisplayMenu);
        menubar.add(DeviceMenu);
        menubar.add(mr);

        setJMenuBar(menubar);
    }

    public static void main(String[] args) {
        IcaroApp app = new IcaroApp();

        if (Boolean.valueOf(app._configFile.search("Autosave"))) {
            AutosaveRunnable.getInstance().addDataset(app, "IcaroMonitor");
            Thread AutosaveThread = new Thread(AutosaveRunnable.getInstance());
            AutosaveThread.start();
        }

        app.setVisible(true);
        app.DisplayXYSeries(app._heaterMonitor.getTemperatureSeries());
    }

    public JMenuItem BuildImportCalibrationItem(String name) {
        JMenuItem item = new JMenuItem(new AbstractAction(name) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Import calibration");
                fileChooser.setApproveButtonText("Import");
                fileChooser.setFileFilter(new FileNameExtensionFilter(".txt", "txt"));

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
                    _ica.set_LUTHeater(new LookUpTable(fileChooser.getSelectedFile().getAbsolutePath()));
            }
        });

        return item;
    }
}
