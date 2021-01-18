package LoggerCore.Menu;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.apache.jorphan.gui.MenuScroller;

import LoggerCore.LoggerApp;

public class MenuLoggerAppDisplay extends BasicMenu {

    protected LoggerApp _logger;

    public MenuLoggerAppDisplay(LoggerApp Logger, String name) {
        super(name);
        _logger = Logger;
        _logAction = false;
    }

    public MenuLoggerAppDisplay BuildDefault() {
        MenuScroller.setScrollerFor(this, 10);

        removeAll();
        for (int i = 0; i < _logger.getLoadedDataset().getSeriesCount(); i++)
            add(BuildDisplaySeriesItem(i));

        add(BuildHideJMenuItem());
        return this;
    }

    public JMenuItem BuildHideJMenuItem() {
        JMenuItem menuItem = new JMenuItem(new AbstractAction("Hide graphs") {

            @Override
            public void actionPerformed(ActionEvent e) {
                _logger.HideAllXYSeries();
                if (_logAction)
                    _logBook.log("Hide graphs of " + _logger.getTitle());
            }
        });

        return menuItem;
    }

    public JMenu BuildSubMenu(String name, int[] indexSeries) {
        JMenu subMenu = new JMenu(name);

        for (int i = 0; i < indexSeries.length; i++)
            subMenu.add(BuildDisplaySeriesItem(indexSeries[i]));

        return subMenu;
    }

    public JMenu BuildSubMenu(String name, Comparable<?>[] keys) {
        JMenu subMenu = new JMenu(name);

        for (int i = 0; i < keys.length; i++)
            subMenu.add(BuildDisplaySeriesItem(keys[i]));

        return subMenu;
    }

    public JMenuItem BuildDisplaySeriesItem(final int index) {
        return BuildNoArgMenuItem(new NoInputAction(_logger.getLoadedDataset().getSeriesKey(index).toString()) {
            @Override
            public void actionPerformed() {
                _logger.DisplayXYSeries(index);
            }
        });
    }

    public JMenuItem BuildDisplaySeriesItem(final Comparable<?> key) {
        return BuildNoArgMenuItem(new NoInputAction(key.toString()) {
            @Override
            public void actionPerformed() {
                _logger.DisplayXYSeries(key);
            }
        });
    }
}
