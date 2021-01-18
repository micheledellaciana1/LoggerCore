package LoggerCore;

import java.io.File;
import java.util.ArrayList;

public class AutosaveRunnable extends StoppableRunnable {

    static private AutosaveRunnable _instance = new AutosaveRunnable();

    private ArrayList<LoggerApp> _loggerApps;
    private ArrayList<File> _CorFolder;

    private File _AutosaveDirectory;

    static public AutosaveRunnable getInstance() {
        return _instance;
    }

    private AutosaveRunnable() {
        super();
        _loggerApps = new ArrayList<LoggerApp>();
        _CorFolder = new ArrayList<File>();
    }

    public void setAutosavePath(File AutosaveDirectory) {
        _AutosaveDirectory = AutosaveDirectory;
    }

    public void addDataset(LoggerApp loggerApp, String RelativePath) {
        File CorrespondingFolder = new File(_AutosaveDirectory, RelativePath);
        _CorFolder.add(CorrespondingFolder);
        _loggerApps.add(loggerApp);
    }

    @Override
    public void MiddleLoop() {
        System.out.println("Autosave ...");

        for (int i = 0; i < _loggerApps.size(); i++) {
            try {
                if (!_CorFolder.get(i).exists())
                    _CorFolder.get(i).mkdir();
                _loggerApps.get(i).ExportEveryLoadeXYSeries(_CorFolder.get(i));
            } catch (Exception e) {
                if (verbose)
                    e.printStackTrace();
            }
        }
    }
}
