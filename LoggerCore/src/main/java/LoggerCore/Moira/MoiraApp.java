package LoggerCore.Moira;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import LoggerCore.LoggerApp;
import LoggerCore.Menu.MenuDevice;
import LoggerCore.Menu.MenuLoggerAppDisplay;
import LoggerCore.Menu.MenuLoggerAppExportTxtFile;
import LoggerCore.Menu.MenuLoggerAppFile;
import LoggerCore.Menu.MenuRoutine;
import LoggerCore.Moira.SoftwareFollower.CurrentFollower;
import LoggerCore.Moira.SoftwareFollower.VoltageFollower;
import LoggerCore.themal.FeedBackController_type1;

public class MoiraApp extends LoggerApp {
        private MoiraApp() {
        }

        public static void main(String[] args) {
                Moira _moira = new Moira();
                _moira.isASimulation = false;
                _moira.executeCommand("Open", null);
                _moira.setOscilloscope(1e-5, 2.5, 2.5);

                VoltageMonitor _VoltageMonitor = new VoltageMonitor("VoltageCh1", "VoltageCh2", _moira);
                AutorangeAnalysis _autorange = new AutorangeAnalysis(_moira);
                _VoltageMonitor.getLinkedAnalysisCollection().add(_autorange);
                AvgCurrentDUTAnalysis _avgCurrent = new AvgCurrentDUTAnalysis("Average Current", _moira);
                _VoltageMonitor.getLinkedAnalysisCollection().add(_avgCurrent);
                AvgVoltageDUTAnalysis _avgVoltage = new AvgVoltageDUTAnalysis("Average Voltage");
                _VoltageMonitor.getLinkedAnalysisCollection().add(_avgVoltage);

                final LifeTimeAnalysis _LTAnalysis = new LifeTimeAnalysis("Life Time");
                _VoltageMonitor.getLinkedAnalysisCollection().add(_LTAnalysis);

                Thread _VoltageMonitorThread = new Thread(_VoltageMonitor);
                _VoltageMonitorThread.start();

                MoiraApp _moiraApp = new MoiraApp();
                _moiraApp.setDefaultCloseOperation(EXIT_ON_CLOSE);

                _moiraApp.addXYSeries(_VoltageMonitor.getVoltageCh1(), "[ms]", "[Volt]");
                _moiraApp.addXYSeries(_VoltageMonitor.getVoltageCh2(), "[ms]", "[Volt]");
                _moiraApp.addXYSeries(_avgCurrent.getSeries(), "[ms]", "A");
                _moiraApp.addXYSeries(_avgVoltage.getSeries(), "[ms]", "V");

                MenuLoggerAppDisplay DisplayMenu = new MenuLoggerAppDisplay(_moiraApp, "Display");
                DisplayMenu.addMenuListener(DisplayMenu.BuildDefaultMenuListener());

                MenuDevice DeviceMenu = new MenuDevice("Set", _moira);
                JMenu AnalogDiscoveryMenu = new JMenu("AnalogDiscovery2");

                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set time Base", "Set_time_Base",
                                "Enter: <TimeBase>", "1e-3"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set DC voltage", "Set_DC_voltage",
                                "Enter: <ChannelIdx> <Voltage>", "0 0"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Start waveform", "Start_wave",
                                "Enter: <ChannelIdx> <WaveformId> <frequency> <Amplitude> <Offset> <DutyCycle>",
                                "0 1 1e5 1 0 50"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Stop waveform", "Stop_wave",
                                "Enter: <ChannelIdx>", "0"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set oscilloscope trigger",
                                "Set_oscilloscope_trigger",
                                "Enter: <SourceChannel> <TriggerLevel> <PosTriggerSec> <TriggerCondition:0/Rising, 1/Falling> <TimeoutSec>",
                                "0 0 0 0 0"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildNoArgCommandItem("Stop oscilloscope trigger",
                                "Stop_oscilloscope_trigger"));

                DeviceMenu.add(AnalogDiscoveryMenu);
                DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Set current Gain Idx", "Set_current_gain",
                                "Enter: <GainIdX>", "0"));
                DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Connect DUT", "Set_switch",
                                "Enter: <Status:1/Connected, 0/Disconneted>", "0"));
                DeviceMenu.add(DeviceMenu.BuildDeviceMonitorFrame("Moira status", "Moira status"));

                MenuRoutine mr = new MenuRoutine("Routine");
                mr.add(mr.BuildNoArgRoutineItem("MesureLifeTime", new MeasureLifeTime(_moira)));

                double FBPar_SoftwareFollower = 10;
                FeedBackController_type1 FBSoftFollower = new FeedBackController_type1(FBPar_SoftwareFollower, 0, 0);
                FBSoftFollower.MAX_responce = 6;
                FBSoftFollower.MIN_responce = -6;

                mr.add(mr.BuildStringRoutineItem("Voltage Follower", new VoltageFollower(_moira, FBSoftFollower, 50),
                                "Enter: <VoltageToFollow[V]>", "0"));
                mr.add(mr.BuildStringRoutineItem("Current Follower", new CurrentFollower(_moira, FBSoftFollower, 50),
                                "Enter: <CurrentToFollow[mA]>", "0"));

                mr.add(mr.BuildStopRoutineItem("Stop routine"));

                MenuLoggerAppFile menuFile = new MenuLoggerAppFile(_moiraApp, "file");
                menuFile.add(menuFile.BuildPropertyChartMenu(true));

                MenuLoggerAppExportTxtFile menuExport = new MenuLoggerAppExportTxtFile(_moiraApp, "Export .txt");
                menuExport.addMenuListener(menuExport.BuildDefaultMenuListener());
                menuFile.add(menuExport);
                menuFile.add(menuFile.BuildEraseSeriesDataItem("Erase data", false));
                menuFile.add(menuFile.BuildDuplicateItem(true));

                JMenuBar menubar = new JMenuBar();
                menubar.add(menuFile);
                menubar.add(DisplayMenu);
                menubar.add(DeviceMenu);
                menubar.add(mr);

                _moiraApp.setJMenuBar(menubar);

                _moiraApp.setVisible(true);
        }
}
