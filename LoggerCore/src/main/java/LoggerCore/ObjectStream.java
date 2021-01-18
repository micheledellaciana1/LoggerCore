package LoggerCore;

public abstract class ObjectStream extends StoppableRunnable {

    protected LinkedAnalysisCollection _linkedAnalyzes;

    public LinkedAnalysisCollection getLinkedAnalysisCollection() {
        return _linkedAnalyzes;
    }

    public ObjectStream() {
        super();
        _linkedAnalyzes = new LinkedAnalysisCollection();
    }

    @Override
    public void MiddleLoop() {
        Object _newData = acquireObject();
        if (!getBREAK())
            _linkedAnalyzes.fireAnalyzesExecution(_newData);
    }

    public abstract Object acquireObject();
}
