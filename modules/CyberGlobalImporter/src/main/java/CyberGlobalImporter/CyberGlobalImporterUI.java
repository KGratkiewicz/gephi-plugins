package CyberGlobalImporter;

import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterUI;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;

@ServiceProvider(service = ImporterUI.class)
public class CyberGlobalImporterUI implements ImporterUI {

        private JPanel panel;
        private JCheckBox option;
        private CyberGlobalImporter importer;
        private boolean dynamic;

    @Override
    public void setup(Importer[] importers) {
        this.importer = (CyberGlobalImporter)importers[0];
    }

    public JPanel getPanel() {
        panel = new JPanel();
        option = new JCheckBox("Dynamic");
        panel.add(option);
        return panel;
        }

        public void unsetup(boolean update) {
            if(update) {
                importer.setDynamic(option.isSelected());
            }
            panel = null;
            importer = null;
            option = null;
        }

        public String getDisplayName() {
            return "Importer Cyber Global";
        }

        public boolean isUIForImporter(Importer importer) {
            return importer instanceof CyberGlobalImporter;
        }
}
