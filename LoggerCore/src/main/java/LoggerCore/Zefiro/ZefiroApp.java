package LoggerCore.Zefiro;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import LoggerCore.AutosaveRunnable;
import LoggerCore.Configuration;
import LoggerCore.LoggerFrame;
import LoggerCore.ThreadManager;
import LoggerCore.Menu.MenuDevice;
import LoggerCore.Menu.MenuFeedBackController;
import LoggerCore.Menu.MenuLoggerFrameDisplay;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;
import LoggerCore.Menu.MenuRoutine;
import LoggerCore.Zefiro.FastChannelMonitor.FastChannelMonitor;
import LoggerCore.Zefiro.HeaterCalibration.HeaterCalibration;
import LoggerCore.Zefiro.ITCharacteristic.ITCharacteristic;
import LoggerCore.Zefiro.IVCharacteristic.IVCharacteristic;
import LoggerCore.Zefiro.SoftwareFollower.CurrentFollower;
import LoggerCore.Zefiro.SoftwareFollower.VoltageFollower;
import LoggerCore.themal.FeedBackController_type1;
import LoggerCore.themal.FeedBackController_type2;
import LoggerCore.themal.LookUpTable;

public class ZefiroApp extends LoggerFrame {

        private Zefiro _zef;
        private Configuration _configFile;
        private VoltageCurrentSensorMonitor _sensorMonitor;
        private FeedBackController_type2 _feedBackController;
        private HeaterTemperatureMonitor _heaterMonitor;
        private LEDCurrentMonitor _LEDcurrentMonitor;
        private ChamberMonitor _ChamberMonitor;
        private ThreadManager _tm;

        public ZefiroApp() {
                super();
                _tm = new ThreadManager();

                setTitle("Zefiro2");

                _configFile = new Configuration("ZefiroConfig", System.getProperty("user.dir") + "\\config.txt");

                /*
                 * _configFile = new Configuration("ZefiroConfig",
                 * "C:\\Users\\utente\\Documents\\Visual Studio 2019\\projects\\LoggerCore\\LoggerCore_gui\\LoggerCore\\src\\main\\java\\LoggerCore\\Zefiro\\config.txt"
                 * );
                 */

                AutosaveRunnable.getInstance().setAutosavePath(new File(_configFile.search("AutosavePath")));
                int AutosavePeriod = _configFile.searchInteger("AutosavePeriodSec") * 1000;
                AutosaveRunnable.getInstance().setExecutionDelay(AutosavePeriod);

                boolean isASimulation = _configFile.searchBoolean("Simulate");

                if (isASimulation)
                        _zef = new Zefiro("Zefiro", null, null);
                else
                        _zef = new Zefiro("Zefiro", null, SerialPort.getCommPorts()[0]);

                _zef.isASimulation = isASimulation;
                _zef.executeCommand("Open", null);

                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                                int option = JOptionPane.showOptionDialog(null, "Close Zefiro?", "Exit",
                                                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                                                new String[] { "Yes", "Cancel", "Force close" }, "Cancel");
                                switch (option) {
                                        case 0:
                                                _zef.executeCommand("Close", null);
                                                System.exit(0);
                                                break;
                                        case 2:
                                                JOptionPane.showMessageDialog(null,
                                                                "Manually turn off Zefiro (disconnect USB)",
                                                                "Force close", JOptionPane.WARNING_MESSAGE);
                                                System.exit(1);
                                                break;
                                        default:
                                                break;
                                }
                        }
                });

                _sensorMonitor = new VoltageCurrentSensorMonitor(_zef, "Voltage Sensor", "Current Sensor",
                                "Resistance Sensor");
                _sensorMonitor.setExecutionDelay(_configFile.searchInteger("DelaySensorAcq"));
                _sensorMonitor.setName("Sensing film monitor");

                double FB1 = _configFile.searchDouble("FBPar1");
                double FB2 = _configFile.searchDouble("FBPar2");
                double FB3 = _configFile.searchDouble("FBPar3");
                double TolleratederrorDeg = _configFile.searchDouble("TolleratedErrorDegree");

                _feedBackController = new FeedBackController_type2(FB1, FB2, FB3);
                _feedBackController.MIN_responce = _configFile.searchDouble("MinCurrentHeater");
                _feedBackController.MAX_responce = _configFile.searchDouble("MaxCurrentHeater");
                _feedBackController.setTolleratedError(TolleratederrorDeg);

                _heaterMonitor = new HeaterTemperatureMonitor(_zef, _feedBackController, "Heater temperature",
                                "Heater current", "Target Temperature");
                _heaterMonitor.setExecutionDelay(_configFile.searchInteger("DelayHeaterAcq"));
                _heaterMonitor.setName("Heater monitor");

                _LEDcurrentMonitor = new LEDCurrentMonitor("LED current", _zef);
                _LEDcurrentMonitor.setExecutionDelay(_configFile.searchInteger("DelayChamberAcq"));
                _LEDcurrentMonitor.setName("LED monitor");

                _ChamberMonitor = new ChamberMonitor(_zef, "Chamber Temperature", "Chamber Relative Humidity",
                                "Chamber Oxygen");
                _ChamberMonitor.setExecutionDelay(_configFile.searchInteger("DelayChamberAcq"));
                _ChamberMonitor.setName("Chamber monitor");

                addXYSeries(_sensorMonitor.getVoltageSeries(), "time[sec]", "Voltage[V]");
                addXYSeries(_sensorMonitor.getCurrentSeries(), "time[sec]", "Current[mA]");
                addXYSeries(_sensorMonitor.getResistanceSeries(), "time[sec]", "Resistance[kOhm]");

                addXYSeries(_heaterMonitor.getTemperatureSeries(), "time[sec]", "Temperature[°C]");
                addXYSeries(_heaterMonitor.getCurrentHeaterSeries(), "time[sec]", "Current[mA]");
                addXYSeries(_heaterMonitor.getTargetTemperatureSeries(), "time[sec]", "Temperature[°C]");

                addXYSeries(_ChamberMonitor.getTempSeries(), "time[sec]", "Temperature[°C]");
                addXYSeries(_ChamberMonitor.getRHSeries(), "time[sec]", "RH[%]");
                addXYSeries(_ChamberMonitor.getOxygenSeries(), "time[sec]", "Oxygen[%]");

                addXYSeries(_LEDcurrentMonitor.getSeries(), "time[sec]", "Current[mA]");

                MenuLoggerFrameDisplay DisplayMenu = new MenuLoggerFrameDisplay(this, "Display");
                JMenu subMenuCharacterization = DisplayMenu.BuildSubMenu("Characterization", new int[] { 0, 1, 2 });
                // subMenuCharacterization.add(BasicMenu.BuildHelpMenu("Help", "HelpMessage"));
                JMenu subMenuHeater = DisplayMenu.BuildSubMenu("Heater", new int[] { 3, 4, 5 });
                // subMenuHeater.add(BasicMenu.BuildHelpMenu("Help", "HelpMessage"));
                JMenu subMenuChamber = DisplayMenu.BuildSubMenu("Chamber", new int[] { 6, 7, 8, 9 });
                // subMenuChamber.add(BasicMenu.BuildHelpMenu("Help", "HelpMessage"));

                DisplayMenu.add(subMenuCharacterization);
                DisplayMenu.add(subMenuHeater);
                DisplayMenu.add(subMenuChamber);

                DisplayMenu.add(DisplayMenu.BuildHideJMenuItem());

                MenuFeedBackController menuFeedback = new MenuFeedBackController("Temperature Feedback",
                                _feedBackController);
                menuFeedback.BuildDefault();

                /*
                 * menuFeedback.add(menuFeedback.BuildSetTargetValueItem("Set temperature",
                 * "Enter: <Temperature[°C]>", "100"));
                 * menuFeedback.add(menuFeedback.BuildSetParametersItem("Feedback parameters",
                 * "Enter: <Int.> <Der.> <Sens.>", FB1 + " " + FB2 + " " + FB3));
                 * menuFeedback.add(menuFeedback.BuildFeedBackOnCheckBox("Active feedback"));
                 */

                double FBPar_SoftwareFollower = _configFile.searchDouble("FBPar_SoftwareFollower");
                FeedBackController_type1 FBSoftFollower = new FeedBackController_type1(FBPar_SoftwareFollower, 0, 0);
                FBSoftFollower.MAX_responce = _configFile.searchDouble("MaxVoltageDAC");
                FBSoftFollower.MIN_responce = _configFile.searchDouble("MinVoltageDAC");

                MenuRoutine MenuSoftwareFollower = new MenuRoutine("Software Follower");
                MenuSoftwareFollower.add(MenuSoftwareFollower.BuildStringRoutineItem("Voltage Follower",
                                new VoltageFollower(_zef, FBSoftFollower, 100), "Enter: <VoltageToFollow[V]>", "0"));
                MenuSoftwareFollower.add(MenuSoftwareFollower.BuildStringRoutineItem("Current Follower",
                                new CurrentFollower(_zef, FBSoftFollower, 100), "Enter: <CurrentToFollow[mA]>", "0"));
                MenuSoftwareFollower.add(MenuSoftwareFollower.BuildStopRoutineItem());

                MenuDevice DeviceMenu = new MenuDevice("Zefiro", _zef);
                JMenu subMenuGeneral = new JMenu("General");
                JMenu subMenuSet = new JMenu("Set");
                subMenuGeneral.add(DeviceMenu.BuildNoArgCommandItem("Reset", "Reset"));
                subMenuGeneral.add(DeviceMenu.BuildDeviceMonitorFrame("Zefiro status", "Zefiro"));
                JMenu subMenuSetAcq = new JMenu("Acquisition");

                subMenuSetAcq.add(DeviceMenu.BuildStringCommandItem("Set average time", "Enter: <AverageTimeMillis>",
                                "10"));
                subMenuSetAcq.add(DeviceMenu.BuildStringCommandItem("Set AmpMeter Idx",
                                "Enter: <IdxRefResistorAmpCurrent>", "0"));

                subMenuGeneral.add(subMenuSetAcq);
                DeviceMenu.add(subMenuGeneral);
                subMenuSet.add(DeviceMenu.BuildSliderCommandItem("Set DAC voltage sensor [V]", "Set DAC voltage sensor",
                                _configFile.searchDouble("MinVoltageDAC"), _configFile.searchDouble("MaxVoltageDAC"),
                                0));
                subMenuSet.add(DeviceMenu.BuildSliderCommandItem("Set current LED [mA]", "Set_current_LED", 0,
                                _configFile.searchDouble("LEDMaxCurrent"), 0));
                subMenuSet.add(DeviceMenu.BuildSliderCommandItem("Set current heater [mA]", "Set current heater",
                                _feedBackController.MIN_responce, _feedBackController.MAX_responce,
                                _feedBackController.MIN_responce));

                subMenuSet.add(MenuSoftwareFollower);
                DeviceMenu.add(subMenuSet);
                DeviceMenu.add(menuFeedback);

                MenuRoutine mr = new MenuRoutine("Routine");
                String DefIVChArgs = _configFile.search("DefIVChArgs");
                mr.add(mr.BuildStringRoutineItem("IVCharacteristic", new IVCharacteristic(_zef),
                                "Enter: <VStart> <VStop> <VStep> <NCycle> <Up&Down> <delay>", DefIVChArgs));
                String DefITChArgs = _configFile.search("DefITChArgs");
                mr.add(mr.BuildStringRoutineItem("ITCharacteristic", new ITCharacteristic(_zef, _feedBackController),
                                "Enter: <TStart> <TStop> <TStep> <NCycle> <Up&Down> <delay>", DefITChArgs));
                mr.add(mr.BuildStringRoutineItem("Calibrate Heater", new HeaterCalibration(_zef, _feedBackController),
                                "Enter: <ExitationCurrent> <NAverage> <Delay> <Alpha> <Beta>",
                                _configFile.search("DefHeaterCalibArgs")));
                mr.add(mr.BuildStringRoutineItem("Fast Channel Monitor", new FastChannelMonitor(_zef),
                                "Enter: <AnalogChannel> <NPoints>", "31 1000"));
                mr.add(mr.BuildStopRoutineItem());

                MenuLoggerFrameExportTxtFile menuExport = new MenuLoggerFrameExportTxtFile(this, "Export .txt");
                JMenu subMenuCharacterizationExp = menuExport.BuildSubMenu("Characterization", new int[] { 0, 1, 2 });
                JMenu subMenuHeaterExp = menuExport.BuildSubMenu("Heater", new int[] { 3, 4, 5 });
                JMenu subMenuChamberExp = menuExport.BuildSubMenu("Chamber", new int[] { 6, 7, 8, 9 });
                menuExport.add(subMenuCharacterizationExp);
                menuExport.add(subMenuHeaterExp);
                menuExport.add(subMenuChamberExp);
                menuExport.add(menuExport.BuildExportEverySeriesItem("Export every series .txt", " "));

                MenuLoggerFrameFile menuFile = new MenuLoggerFrameFile(this, "file");
                menuFile.add(menuFile.BuildPropertyChartMenu(true));
                menuFile.add(menuExport);
                menuFile.add(menuFile.BuildHideDisplayMenuLogger("Hide/Display log book"));
                menuFile.add(BuildImportCalibrationItem("Import heater calibration"));
                menuFile.add(menuFile.BuildThreadManagerItem("Running threads", _tm));
                menuFile.add(menuFile.BuildEraseSeriesDataItem("Delete data",
                                _configFile.searchBoolean("ZeroTimeOnErase")));
                menuFile.add(menuFile.BuildDuplicateItem(true));

                JMenuBar menubar = new JMenuBar();
                menubar.add(menuFile);
                menubar.add(DisplayMenu);
                menubar.add(DeviceMenu);
                menubar.add(mr);

                setJMenuBar(menubar);

                _tm.add(_sensorMonitor);
                _tm.add(_heaterMonitor);
                _tm.add(_ChamberMonitor);
                _tm.add(_LEDcurrentMonitor);

                if (Boolean.valueOf(_configFile.search("Autosave"))) {
                        AutosaveRunnable.getInstance().addDataset(this, "ZefiroMonitor");
                        _tm.add(AutosaveRunnable.getInstance());
                }

                _tm.runAll();
        }

        public static void main(String[] args) {
                ZefiroApp app = new ZefiroApp();
                app.setVisible(true);
                app.DisplayXYSeries(app._sensorMonitor.getVoltageSeries().getKey());
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
                                        _zef.set_LUTHeater(new LookUpTable(
                                                        fileChooser.getSelectedFile().getAbsolutePath()));
                        }
                });

                item.setIcon(UIManager.getIcon("FileView.fileIcon"));
                return item;
        }
}
