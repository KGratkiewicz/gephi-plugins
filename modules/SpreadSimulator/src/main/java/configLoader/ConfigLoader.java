package configLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();
    private static final String DEFAULT_CONFIG_FILE = "simulation.properties";
    private static final String FALLBACK_CONFIG_FILE = "simulationWindows.properties";
    private static final String GEPHI_CONF_PATH_ENV = "GEPHI_CONF_PATH";

    static {
        loadProperties();
    }

    // Constants loaded from properties
    public static final String folderSimulationTmp = properties.getProperty("folder.simulation.tmp");
    public static final String folderSimulationTmpFilename = properties.getProperty("folder.simulation.tmp.filename");
    public static final String componentNameSimulationComponent = properties.getProperty("component.name.simulationComponent");
    public static final String modelBuilderToolInfoStatusCreate = properties.getProperty("modelBuilderTool.infoStatus.create");
    public static final String modelBuilderToolInfoStatusLink = properties.getProperty("modelBuilderTool.infoStatus.link");
    public static final String colNameModelBuilderNodeState = properties.getProperty("colName.modelBuilder.nodeState");
    public static final String colNameModelBuilderProbability = properties.getProperty("colName.modelBuilder.probability");
    public static final String colNameModelBuilderTransitionType = properties.getProperty("colName.modelBuilder.transitionType");
    public static final String buttonLabelRunSimulation = properties.getProperty("button.label.runSimulation");
    public static final String buttonLabelRunSimulationSeries = properties.getProperty("button.label.runSimulationSeries");
    public static final String colNameModelBuilderProvocativeNeighbours = properties.getProperty("colName.modelBuilder.provocativeNeighbours");
    public static final String colNameNewNodeState = properties.getProperty("colName.newNodeState");
    public static final String colNameRootState = properties.getProperty("colName.rootState");
    public static final String colNameNodeState = properties.getProperty("colName.nodeState");
    public static final String colNameInitialNodeState = properties.getProperty("colName.initialNodeState");
    public static final String colNameHeatMapValue = properties.getProperty("colName.heatMapValue");
    public static final String colNameTempNodeState = properties.getProperty("colName.tempNodeState");
    public static final String colNameRuleNodeState = properties.getProperty("colName.ruleNodeState");
    public static final String messageErrorUnknownTransitionType = properties.getProperty("message.error.unknownTransitionType", "Unknown transition type");
    public static final String colNameNodeRole = properties.getProperty("colName.nodeRole");
    public static final String colNameModelBuilderDescription = properties.getProperty("colName.modelBuilder.description");
    public static final String modelBuilderLabelTransition = properties.getProperty("modelBuilder.label.transition");
    public static final String folderSimulationBuilderModels = properties.getProperty("folder.simulationBuilder.models");
    public static final String modelBuilderLabelState = properties.getProperty("modelBuilder.label.state");
    public static final String folderReports = properties.getProperty("folder.reports");
    public static final String folderSimulationBuilderSimulations = properties.getProperty("folder.simulationBuilder.simulations");

    private static void loadProperties() {
        String gephiConfPath = System.getenv(GEPHI_CONF_PATH_ENV);

        if (gephiConfPath != null) {
            File configFile = new File(gephiConfPath, DEFAULT_CONFIG_FILE);
            if (configFile.exists()) {
                try (FileInputStream config = new FileInputStream(configFile)) {
                    properties.load(config);
                    return;
                } catch (IOException e) {
                    System.err.println("Failed to load configuration from " + configFile.getAbsolutePath());
                }
            } else {
                System.err.println("Configuration file not found at specified GEPHI_CONF_PATH: " + configFile.getAbsolutePath());
            }
        }

        try (FileInputStream config = new FileInputStream(DEFAULT_CONFIG_FILE)) {
            properties.load(config);
        } catch (IOException e) {
            File fallbackFile = new File(new File(System.getProperty("user.dir")).getParentFile().getParentFile(), FALLBACK_CONFIG_FILE);
            try (FileInputStream fallbackConfig = new FileInputStream(fallbackFile)) {
                properties.load(fallbackConfig);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load configuration from " + DEFAULT_CONFIG_FILE + " or " + fallbackFile.getAbsolutePath(), ex);
            }
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
