package LoggerCore.Menu;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import LoggerCore.LinkedAnalysis;
import LoggerCore.LinkedAnalysisCollection;

public class MenuMutexLinkedAnalysis extends BasicMenu {
    protected LinkedAnalysis _linkedAnalysis;

    public MenuMutexLinkedAnalysis(String name) {
        super(name);
        _linkedAnalysis = null;
    }

    public JMenuItem BuildNoArgLinkAnalysisItem(String name, final LinkedAnalysis linkedAnalysis,
            final LinkedAnalysisCollection relativeCollection) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Link " + _linkedAnalysis.get_name()) {
            @Override
            public void actionPerformed() {
                if (_linkedAnalysis != null) {
                    _linkedAnalysis.removeFromAllCollections();
                    if (_logAction)
                        _logBook.log("Unlink " + _linkedAnalysis.get_name());
                }
                if (!linkedAnalysis.setup(null))
                    return;
                _linkedAnalysis = linkedAnalysis;
                relativeCollection.add(linkedAnalysis);
            }
        });
    }

    public JMenuItem BuildStringLinkAnalysisItem(String name, final LinkedAnalysis linkedAnalysis,
            final LinkedAnalysisCollection relativeCollection, final String message, final String initialValue) {
        return BuildArgStringMenuItem(
                new InputStringAction(name, message, name, initialValue, "Link " + _linkedAnalysis.get_name()) {
                    @Override
                    public void actionPerformed() {
                        if (_linkedAnalysis != null) {
                            _linkedAnalysis.removeFromAllCollections();
                            if (_logAction)
                                _logBook.log("Unlink " + _linkedAnalysis.get_name());
                        }

                        if (!linkedAnalysis.setup(_input))
                            return;

                        _linkedAnalysis = linkedAnalysis;
                        relativeCollection.add(linkedAnalysis);
                    }
                });
    }

    public JMenuItem BuildUnlinkAnalysisItem(String name) {
        return BuildNoArgMenuItem(new NoInputAction(name, "Unlink " + _linkedAnalysis.get_name()) {
            @Override
            public void actionPerformed() {
                if (_linkedAnalysis != null) {
                    _linkedAnalysis.removeFromAllCollections();
                }

                _linkedAnalysis = null;
            }
        });

    }

    @Override
    public JMenu BuildDefault() {
        // TODO Auto-generated method stub
        return null;
    }
}