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

    @Override
    public void setup(Importer[] importers) {
        this.importer = (CyberGlobalImporter) importer;
    }

    public JPanel getPanel() {
            panel = new JPanel();
            return panel;
        }

        public void unsetup(boolean update) {
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
