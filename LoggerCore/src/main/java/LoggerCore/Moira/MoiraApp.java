package LoggerCore.Moira;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

import LoggerCore.Configuration;
import LoggerCore.LoggerFrame;
import LoggerCore.ThreadManager;
import LoggerCore.Menu.MenuDevice;
import LoggerCore.Menu.MenuLoggerFrameDisplay;
import LoggerCore.Menu.MenuLoggerFrameExportTxtFile;
import LoggerCore.Menu.MenuLoggerFrameFile;
import LoggerCore.Menu.MenuRoutine;
import LoggerCore.Moira.Analyzer.LissajousAnalysis;
import LoggerCore.Moira.Analyzer.Frame.AverangeValueLogger;
import LoggerCore.Moira.Analyzer.Frame.HarmonicAnalyzerLogger;
import LoggerCore.Moira.Analyzer.Frame.LifeTimeAnalysisLogger;
import LoggerCore.Moira.Analyzer.Frame.SpectrumAnalyzerLogger;
import LoggerCore.Moira.SoftwareFollower.CurrentFollower;
import LoggerCore.Moira.SoftwareFollower.VoltageFollower;
import LoggerCore.Moira.rouintes.FrequencySweep;
import LoggerCore.Moira.rouintes.IVCharacteristic;
import LoggerCore.Moira.rouintes.ONOFF;
import LoggerCore.themal.FeedBackController_type1;

public class MoiraApp extends LoggerFrame {
        private MoiraApp() {
                setTitle("Moira");
        }

        public static void main(String[] args) {

                ThreadManager _tm = new ThreadManager();

                Configuration _configFile = new Configuration("MoiraConfig",
                                "C:\\Users\\utente\\Documents\\Visual Studio 2019\\projects\\LoggerCore\\LoggerCore_gui\\LoggerCore\\src\\main\\java\\LoggerCore\\Moira\\config.txt");

                Moira _moira = new Moira(_configFile);

                _moira.isASimulation = _configFile.searchBoolean("Simulate");
                _moira.executeCommand("Open", null);

                VoltageMonitor _VoltageMonitor = new VoltageMonitor("VoltageCh1", "VoltageCh2", _moira);
                _VoltageMonitor.setName("Voltage Monitor");
                _tm.add(_VoltageMonitor);

                AutorangeAnalysis _autorange = new AutorangeAnalysis(_moira);
                _VoltageMonitor.getLinkedAnalysisCollection().add(_autorange);

                LissajousAnalysis _lissajous = new LissajousAnalysis();
                _VoltageMonitor.getLinkedAnalysisCollection().add(_lissajous);

                MoiraApp _moiraApp = new MoiraApp();
                _moiraApp.setDefaultCloseOperation(EXIT_ON_CLOSE);

                _moiraApp.addXYSeries(_VoltageMonitor.getVoltageCh1(), "[ms]", "[V]");
                _moiraApp.addXYSeries(_VoltageMonitor.getVoltageCh2(), "[ms]", "[V]");
                _moiraApp.addXYSeries(_lissajous.getSeries(), "Ch1 [V]", "Ch2 [V]");

                _moiraApp.DisplayXYSeries(_VoltageMonitor.getVoltageCh1());
                _moiraApp.DisplayXYSeries(_VoltageMonitor.getVoltageCh2());

                MenuLoggerFrameDisplay DisplayMenu = new MenuLoggerFrameDisplay(_moiraApp, "Display");
                DisplayMenu.addMenuListener(DisplayMenu.BuildDefaultMenuListener());

                MenuDevice DeviceMenu = new MenuDevice("Set", _moira);

                JMenu AnalogDiscoveryMenu = new JMenu("AnalogDiscovery2");

                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set time Base", "Set_time_base",
                                "Enter: <TimeBase[sec]>", "1e-3"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set DC voltage", "Set_DC_voltage",
                                "Enter: <ChannelIdx> <Voltage>", "0 0"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Start waveform", "Start_wave",
                                "Enter: <ChannelIdx> <WaveformId> <frequency> <Amplitude> <Offset> <DutyCycle>",
                                "0 1 1e5 1 0 50"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Stop waveform", "Stop_wave",
                                "Enter: <ChannelIdx>", "0"));
                AnalogDiscoveryMenu.add(DeviceMenu.BuildStringCommandItem("Set power supply", "Set_power_supply",
                                "Enter: <IdxChannel> <Value>", "0 0"));

                DeviceMenu.add(AnalogDiscoveryMenu);

                /*
                 * DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Set current Gain Idx",
                 * "Set_current_gain", "Enter: <GainIdX>", "0"));
                 * DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Set shunt resistor Idx",
                 * "Set_shunt_resistor", "Enter: <GainIdX>", "0"));
                 */

                DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Set time Base", "Set_time_base",
                                "Enter: <TimeBase[sec]>", "1e-3"));
                DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Calibrate offsets", "Init_offsets", "Enter: <null>",
                                "null"));
                DeviceMenu.add(TriggerFrame.BuildMenuItem("TriggerFrame", _moira));
                DeviceMenu.add(DCControlFrame.BuildMenuItem("DCControlFrame", _moira));

                DeviceMenu.add(DeviceMenu.BuildStringCommandItem("Connect DUT", "Set_switch",
                                "Enter: <Status:1/Connected, 0/Disconneted>", "0"));
                DeviceMenu.add(DeviceMenu.BuildDeviceMonitorFrame("Moira status", "Moira status"));

                MenuRoutine mr = new MenuRoutine("Routine");
                mr.add(mr.BuildNoArgRoutineItem("ON/OFF switch", new ONOFF(_moira)));

                double FBPar_SoftwareFollower = _configFile.searchDouble("FBPar_SoftwareFollower");
                FeedBackController_type1 FBSoftFollower = new FeedBackController_type1(FBPar_SoftwareFollower, 0, 0);
                FBSoftFollower.MIN_responce = _configFile.searchDouble("MinVoltageWX");
                FBSoftFollower.MAX_responce = _configFile.searchDouble("MaxVoltageWX");

                String DefIVChArgs = _configFile.search("DefIVChArgs");
                mr.add(mr.BuildStringRoutineItem("IVCharacteristic", new IVCharacteristic(_moira),
                                "Enter: <VStart> <VStop> <VStep> <NCycle> <Up&Down> <delay>", DefIVChArgs));

                mr.add(mr.BuildStringRoutineItem("Voltage Follower", new VoltageFollower(_moira, FBSoftFollower, 50),
                                "Enter: <VoltageToFollow[V]>", "0"));
                mr.add(mr.BuildStringRoutineItem("Current Follower", new CurrentFollower(_moira, FBSoftFollower, 50),
                                "Enter: <CurrentToFollow[mA]>", "0"));
                mr.add(mr.BuildStringRoutineItem("Frequency sweep", new FrequencySweep(_moira),
                                "Enter: <Freq. Start> <Freq. End> <Freq. Factor> <N. Cycle> <Up&Down> <delay>",
                                "100 10e6 1.1 -1 true 500"));

                mr.add(mr.BuildStopRoutineItem());

                JMenu AnalysisMenu = new JMenu("Analysis");

                SpectrumAnalyzerLogger spectrumAnalyzer = new SpectrumAnalyzerLogger(_moira, _VoltageMonitor);
                AnalysisMenu.add(spectrumAnalyzer.BuildShowFrameMenuItem());

                AverangeValueLogger averangeValueMenu = new AverangeValueLogger(_moira, _VoltageMonitor);
                AnalysisMenu.add(averangeValueMenu.BuildShowFrameMenuItem());

                HarmonicAnalyzerLogger harmonicAnalyzerLogger = new HarmonicAnalyzerLogger(_moira, _VoltageMonitor);
                AnalysisMenu.add(harmonicAnalyzerLogger.BuildShowFrameMenuItem());

                LifeTimeAnalysisLogger lifeTimeAnalysisLogger = new LifeTimeAnalysisLogger(_moira, _VoltageMonitor);
                AnalysisMenu.add(lifeTimeAnalysisLogger.BuildShowFrameMenuItem());

                MenuLoggerFrameFile menuFile = new MenuLoggerFrameFile(_moiraApp, "file");
                menuFile.add(menuFile.BuildPropertyChartMenu(false));

                MenuLoggerFrameExportTxtFile menuExport = new MenuLoggerFrameExportTxtFile(_moiraApp, "Export .txt");
                menuExport.addMenuListener(menuExport.BuildDefaultMenuListener());
                menuFile.add(menuExport);
                menuFile.add(menuFile.BuildThreadManagerItem("Acquisition thread", _tm));
                menuFile.add(menuFile.BuildEraseSeriesDataItem("Erase data", false));
                menuFile.add(menuFile.BuildDuplicateItem(true));

                JMenuBar menubar = new JMenuBar();
                menubar.add(menuFile);
                menubar.add(DisplayMenu);
                menubar.add(DeviceMenu);
                menubar.add(AnalysisMenu);
                menubar.add(mr);

                _moiraApp.setJMenuBar(menubar);

                _moiraApp.setVisible(true);
                _tm.runAll();
        }
}
