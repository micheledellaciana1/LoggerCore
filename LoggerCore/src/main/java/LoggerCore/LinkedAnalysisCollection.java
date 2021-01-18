package LoggerCore;

import java.util.ArrayList;

public class LinkedAnalysisCollection extends ArrayList<LinkedAnalysis> {
    public LinkedAnalysisCollection() {
        super();
    }

    public void fireAnalyzesExecution(Object newData) {
        for (int i = 0; i < size(); i++)
            get(i).Execute(newData);
    }

    @Override
    public boolean add(LinkedAnalysis e) {
        e._linkedAnalysisCollections.add(this);
        return super.add(e);
    }
}