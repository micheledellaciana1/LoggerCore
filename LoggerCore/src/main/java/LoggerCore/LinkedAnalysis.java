package LoggerCore;

import java.util.ArrayList;

public abstract class LinkedAnalysis {

    protected String _name;
    protected ArrayList<LinkedAnalysisCollection> _linkedAnalysisCollections; // collections of listener linked to this
                                                                              // analysis
    protected LinkedAnalysisCollection _linkedAnalyzes;

    public boolean verbose = true;

    public LinkedAnalysisCollection getLinkedAnalysisCollection() {
        return _linkedAnalyzes;
    }

    public LinkedAnalysis(String Name) {
        _name = Name;
        _linkedAnalyzes = new LinkedAnalysisCollection();
        _linkedAnalysisCollections = new ArrayList<LinkedAnalysisCollection>();
    }

    public void Execute(final Object newData) {
        if (newData == null)
            return;

        Object DataAnalyzed = ExecuteAnalysis(newData);
        _linkedAnalyzes.fireAnalyzesExecution(DataAnalyzed);
    }

    public void removeFromAllCollections() {
        for (LinkedAnalysisCollection linkedAnalysisCollection : _linkedAnalysisCollections)
            linkedAnalysisCollection.remove(this);
    }

    public boolean setup(Object arg) {
        return true;
    }

    public abstract Object ExecuteAnalysis(Object newData);

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }
}
