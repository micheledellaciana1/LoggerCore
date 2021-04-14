package LoggerCore;

import java.io.File;
import java.util.ArrayList;

public class AutosaveRunnable extends StoppableRunnable {

    static private AutosaveRunnable _instance = new AutosaveRunnable();

    private ArrayList<LoggerFrame> _LoggerFrames;
    private ArrayList<File> _CorFolder;

    private File _AutosaveDirectory;

    static public AutosaveRunnable getInstance() {
        return _instance;
    }

    private AutosaveRunnable() {
        super();
        _name = "Autosave";
        _LoggerFrames = new ArrayList<LoggerFrame>();
        _CorFolder = new ArrayList<File>();
    }

    public void setAutosavePath(File AutosaveDirectory) {
        _AutosaveDirectory = AutosaveDirectory;
    }

    public void addDataset(LoggerFrame LoggerFrame, String RelativePath) {
        File CorrespondingFolder = new File(_AutosaveDirectory, RelativePath);
        _CorFolder.add(CorrespondingFolder);
        _LoggerFrames.add(LoggerFrame);
    }

    @Override
    public void MiddleLoop() {
        System.out.println("Autosave ...");

        for (int i = 0; i < _LoggerFrames.size(); i++) {
            try {
                if (!_CorFolder.get(i).exists())
                    _CorFolder.get(i).mkdir();
                _LoggerFrames.get(i).ExportEveryLoadeXYSeries(_CorFolder.get(i));
            } catch (Exception e) {
                if (verbose)
                    e.printStackTrace();
            }
        }
    }
}
