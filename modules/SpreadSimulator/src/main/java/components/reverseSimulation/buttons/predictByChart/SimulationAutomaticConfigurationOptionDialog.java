package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;
import components.reverseSimulation.buttons.predictByChart.model.AutomaticAdvancedReport;
import components.simulation.Simulation;
import components.simulation.SimulationAll;
import components.simulation.SimulationRelativeEdges;
import components.simulation.SimulationRelativeFreeEdges;
import components.simulation.SimulationRelativeFreeNodes;
import components.simulation.SimulationRelativeNodes;
import components.simulationLogic.SimulationComponent;
import components.simulationLogic.report.SimulationStepReport;
import configLoader.ConfigLoader;
import helper.ApplySimulationHelper;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.Degree;
import org.gephi.statistics.plugin.EigenvectorCentrality;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Hits;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;
import org.openide.util.NotImplementedException;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Getter
@Setter
public class SimulationAutomaticConfigurationOptionDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private String stateAndRoleName;
    private List<AdvancedRule> strategyList;
    private List<Pair<Integer, Double>> diffList = new ArrayList<>();
    private int currentNumberOfNodes;
    private List<Pair<String, JTextField>> newNodesCoverage = new ArrayList<>();
    private JTextField numberOfSimulationsInput;
    private JTextField predictionStartInput;
    private JTextField predictionEndInput;
    public SimulationAutomaticConfigurationOptionDialog(ReverseSimulationComponent reverseSimulationComponent,
                                                        String stateAndRoleName,
                                                        List<AdvancedRule> strategyList) {
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.stateAndRoleName = stateAndRoleName;
        this.strategyList = strategyList;
        initComponents();
    }

    private void initComponents() {
        JFrame frame = new JFrame("Simulation Configuration");
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.HORIZONTAL;
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 1;

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;

        var numberOfSimulationsLabel = new JLabel("Number of simulations before apply correction:");
        panel.add(numberOfSimulationsLabel, gbc);
        
        numberOfSimulationsInput = new JTextField(5);
        gbc.gridx = 1;
        panel.add(numberOfSimulationsInput, gbc);

        var nodeRolesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints nrgbc = new GridBagConstraints();
        nrgbc.anchor = GridBagConstraints.LINE_START;
        nrgbc.fill = GridBagConstraints.HORIZONTAL;
        nrgbc.weightx = 1;
        nrgbc.gridy = 0;

        JLabel rolesDescriptionLabel = new JLabel("Please enter initial roles coverage before applied previously setup rules");
        nodeRolesPanel.add(rolesDescriptionLabel, nrgbc);
        nrgbc.gridy++;

        var nodeRoles = SimulationComponent.getInstance().getSimulationModel().getNodeRoles();
        nodeRoles.forEach(nodeRoleDecorator -> nodeRoleDecorator.getNodeStates().forEach(nodeStateDecorator -> {
            nrgbc.gridx = 0;
            String name = nodeRoleDecorator.getNodeRole().getName() + ":" + nodeStateDecorator.getNodeState().getName();
            JLabel label = new JLabel(name);
            nodeRolesPanel.add(label, nrgbc);
            nrgbc.gridx = 1;
            JTextField textField = new JTextField(5);
            textField.setText("0.0");
            nodeRolesPanel.add(textField, nrgbc);
            nrgbc.gridy++;
            newNodesCoverage.add(Pair.of(name, textField));
        }));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        var predictionInfoLabel = new JLabel("Leave steps empty for full interval");
        infoPanel.add(predictionInfoLabel);

        JPanel intervalPanel = new JPanel();
        intervalPanel.setLayout(new GridBagLayout());
        GridBagConstraints igbc = new GridBagConstraints();
        igbc.anchor = GridBagConstraints.LINE_START;
        igbc.fill = GridBagConstraints.HORIZONTAL;
        igbc.weightx = 1;
        igbc.gridx = 0;
        igbc.gridy = 0;

        var predictionStartLabel = new JLabel("Prediction start step:");
        intervalPanel.add(predictionStartLabel, igbc);
        predictionStartInput = new JTextField(5);
        igbc.gridx = 1;
        intervalPanel.add(predictionStartInput, igbc);

        igbc.gridx = 0;
        igbc.gridy = 1;

        var predictionEndLabel = new JLabel("Prediction end step:");
        intervalPanel.add(predictionEndLabel, igbc);
        predictionEndInput = new JTextField(5);
        igbc.gridx = 1;
        intervalPanel.add(predictionEndInput, igbc);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("Ok");
        okButton.addActionListener(e -> onOk());
        buttonPanel.add(okButton);

        mainPanel.add(panel, mainGbc);
        mainGbc.gridy++;
        mainPanel.add(infoPanel, mainGbc);
        mainGbc.gridy++;
        mainPanel.add(intervalPanel, mainGbc);
        mainGbc.gridy++;
        mainPanel.add(Box.createVerticalStrut(10));
        mainGbc.gridy++;
        mainPanel.add(nodeRolesPanel, mainGbc);
        mainGbc.gridy++;
        mainPanel.add(buttonPanel, mainGbc);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(frame);
        frame.setVisible(true);
    }

    private void onOk() {
        try {
            var predictionStartInterval = 0;
            var predictionEndInterval = 0;
            if (Objects.equals(predictionStartInput.getText(), "")) {
//                TODO SETUP PROPERLY
                predictionStartInterval = 0;
            } else {
                predictionStartInterval = Integer.parseInt(predictionStartInput.getText());
            }
            if (Objects.equals(predictionEndInput.getText(), "")) {
                //                TODO SETUP PROPERLY
                predictionEndInterval = 99999;
            } else {
                predictionEndInterval = Integer.parseInt(predictionEndInput.getText());
            }
            var conductSimulations = Integer.parseInt(numberOfSimulationsInput.getText());
            Pair<List<SimulationStepReport>, List<AutomaticAdvancedReport>> actualSimulationAndBestSimulationReport = prepareAndStartSimulation(conductSimulations, predictionStartInterval, predictionEndInterval, strategyList);
            new ReportDialog(predictionStartInterval, predictionEndInterval, stateAndRoleName, actualSimulationAndBestSimulationReport);
            setVisible(false);
            dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for simulations number and doubles for prediction steps interval.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Pair<List<SimulationStepReport>, List<AutomaticAdvancedReport>> prepareAndStartSimulation(
            int conductSimulations,
            int predictionStartInterval,
            int predictionEndInterval,
            List<AdvancedRule> strategyList) {
        SimulationComponent simulationComponent = SimulationComponent.getInstance();
        var actualReport = simulationComponent.getCurrentSimulation().getReport();
        var steps = actualReport.size();

        var nodeRoles = simulationComponent.getSimulationModel().getNodeRoles();
        newNodesCoverage.forEach(pair -> nodeRoles.stream()
                .filter(nodeRoleDecorator -> nodeRoleDecorator.getNodeRole().getName().equals(pair.first().split(":")[0]))
                .flatMap(nodeRoleDecorator -> nodeRoleDecorator.getNodeStates().stream())
                .filter(nodeState -> nodeState.getNodeState().getName().equals(pair.first().split(":")[1]))
                .forEach(nodeState -> nodeState.setCoverage(Double.parseDouble(pair.second().getText()))));

        List<AutomaticAdvancedReport> advancedReportList = new ArrayList<>();
        strategyList.forEach(strategy -> {
            List<List<SimulationStepReport>> simulationReportList;
            boolean bestSolutionFound = false;
            currentNumberOfNodes = 1;
            diffList = new ArrayList<>();

            var table = simulationComponent.getGraph().getModel().getNodeTable();
            simulationReportList = startSimulation(conductSimulations, steps, this.stateAndRoleName, simulationComponent, strategy);
            while (!bestSolutionFound) {
                currentNumberOfNodes++;
                if(!table.hasColumn(ConfigLoader.colNameTempNodeState)) {
                    table.addColumn(ConfigLoader.colNameTempNodeState, String.class);
                }
                Arrays.stream(simulationComponent.getGraph().getNodes().toArray()).forEach(node -> {
                    node.setAttribute(ConfigLoader.colNameTempNodeState, node.getAttribute(ConfigLoader.colNameInitialNodeState));
                } );
                var nextSimulation = startSimulation(conductSimulations, steps, this.stateAndRoleName, simulationComponent, strategy);
                var pairOfDiff = compareSimulation(actualReport, simulationReportList, nextSimulation, predictionStartInterval, predictionEndInterval, this.stateAndRoleName);
                diffList.add(Pair.of(currentNumberOfNodes - 1, pairOfDiff.first()));
                if (pairOfDiff.first() <= pairOfDiff.second()) {
                    bestSolutionFound = true;
                    diffList.add(Pair.of(currentNumberOfNodes, pairOfDiff.second()));
                    if(!table.hasColumn(ConfigLoader.colNameRuleNodeState + "_" + strategyList.indexOf(strategy)))
                        table.addColumn(ConfigLoader.colNameRuleNodeState + "_" + strategyList.indexOf(strategy), String.class);
                    Arrays.stream(simulationComponent.getGraph().getNodes().toArray()).forEach(node -> {
                        node.setAttribute(ConfigLoader.colNameRuleNodeState + "_" + strategyList.indexOf(strategy), node.getAttribute(ConfigLoader.colNameTempNodeState));
                    } );
                } else {
                    simulationReportList = nextSimulation;
                }
            }
            advancedReportList.add(new AutomaticAdvancedReport(strategy.rule, strategy.ascending, diffList, simulationReportList));
        });

        return Pair.of(actualReport, advancedReportList);
    }

    private Pair<Double, Double> compareSimulation(
            List<SimulationStepReport> actualReport,
            List<List<SimulationStepReport>> firstReport,
            List<List<SimulationStepReport>> secondReport,
            int intervalStart,
            int intervalEnd,
            String stateAndRoleName) {
        String nodeStateName = stateAndRoleName.split(":")[0];
        String nodeRoleName = stateAndRoleName.split(":")[1];
        AtomicReference<Double> firstReportSum = new AtomicReference<>((double) 0);
        AtomicReference<Double> secondReportSum = new AtomicReference<>((double) 0);
        Map<Integer, Double> actualMapValues = new HashMap<>();
        List<Map<Integer, Double>> firstReportListMapValues = new ArrayList<>();
        List<Map<Integer, Double>> secondReportListMapValues = new ArrayList<>();

        actualReport.stream()
                .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                        .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                        .forEach(stepReport -> stepReport.getStatesReport().stream()
                                .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                .forEach(stateElement -> actualMapValues.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));

        firstReport
                .forEach(oneReport -> {
                    Map<Integer, Double> newMap = new HashMap<>();
                    oneReport.stream()
                            .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                            .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                                    .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                                    .forEach(stepReport -> stepReport.getStatesReport().stream()
                                            .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                            .forEach(stateElement -> newMap.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));
                    firstReportListMapValues.add(newMap);
                });
        var firstReportMapValues = firstReportListMapValues.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));

        secondReport
                .forEach(oneReport -> {
                    Map<Integer, Double> newMap = new HashMap<>();
                    oneReport.stream()
                            .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                            .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                                    .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                                    .forEach(stepReport -> stepReport.getStatesReport().stream()
                                            .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                            .forEach(stateElement -> newMap.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));
                    secondReportListMapValues.add(newMap);
                });
        var secondReportMapValues = secondReportListMapValues.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));

        actualMapValues.forEach((key, value) -> {
            firstReportSum.updateAndGet(v -> v + Math.abs(firstReportMapValues.get(key) - value));
            secondReportSum.updateAndGet(v -> v + Math.abs(secondReportMapValues.get(key) - value));
        });

        return Pair.of(firstReportSum.get(), secondReportSum.get());
    }

    private List<List<SimulationStepReport>> startSimulation(int conductSimulations, int steps, String stateAndRoleName, SimulationComponent simulationComponent, AdvancedRule strategy) {
        List<Simulation> simulationList = new ArrayList<>();
        for (int i = 0; i < conductSimulations; i++) {
            applyRules(simulationComponent, stateAndRoleName, strategy);
            createNewSimulation(simulationComponent);
            for (int l = 0; l < steps; l++) {
                simulationComponent.getCurrentSimulation().Step();
            }
            simulationList.add(simulationComponent.getCurrentSimulation().clone());
        }
        return simulationList.stream().map(Simulation::getReport).collect(Collectors.toList());
    }

    private void createNewSimulation(SimulationComponent simulationComponent) {
        switch (simulationComponent.getSimulationModel().getInteraction().getInteractionType()){
            case All:
                simulationComponent.setCurrentSimulation(new SimulationAll(simulationComponent.getGraph(), simulationComponent.getSimulationModel()));
                break;
            case RelativeEdges:
                simulationComponent.setCurrentSimulation(new SimulationRelativeEdges(simulationComponent.getGraph(), simulationComponent.getSimulationModel()));
                break;
            case RelativeFreeEdges:
                simulationComponent.setCurrentSimulation(new SimulationRelativeFreeEdges(simulationComponent.getGraph(), simulationComponent.getSimulationModel()));
                break;
            case RelativeNodes:
                simulationComponent.setCurrentSimulation(new SimulationRelativeNodes(simulationComponent.getGraph(), simulationComponent.getSimulationModel()));
                break;
            case RelativeFreeNodes:
                simulationComponent.setCurrentSimulation(new SimulationRelativeFreeNodes(simulationComponent.getGraph(), simulationComponent.getSimulationModel()));
                break;
            default:
                break;
        }
    }

    private void applyRules(SimulationComponent simulationComponent, String stateAndRoleName, AdvancedRule strategy) {
        try {
            Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
            var nodes = List.of(graph.getNodes().toArray());
            var table = nodes.get(0).getTable();
            if(table.hasColumn("NodeRole")) {
                table.removeColumn("NodeRole");
            }
            if(table.hasColumn("NodeState")) {
                table.removeColumn("NodeState");
            }
            ApplySimulationHelper.CrateModelColumns(graph);
            ApplySimulationHelper.Apply(graph, simulationComponent.getSimulationModel());

            var nodeStateAndNameSplited = stateAndRoleName.split(":");
            ExecuteRule(new AdvancedRule(strategy.rule, currentNumberOfNodes, strategy.ascending), nodeStateAndNameSplited[0], nodeStateAndNameSplited[1]);
            nodes = List.of(graph.getNodes().toArray());
            nodes.forEach(node ->
                    node.setAttribute(ConfigLoader.colNameInitialNodeState, node.getAttribute(ConfigLoader.colNameNodeState))
            );
        }
        catch (NullPointerException ex){
            JOptionPane.showMessageDialog(null, "Setup graph model first");
        }
    }

    private void ExecuteRule(AdvancedRule rule, String roleName, String stateName){
        var graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        var graph = graphModel.getGraph();
        String centralityMethod = rule.rule;
        Integer numOfNodes = rule.numberOfNodes;
        boolean ascending = rule.ascending;
        switch (centralityMethod) {
            case "Random":
                RandomNStrategy(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Random-Random":
                RandomRandomStrategy(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Closeness":
                GraphDistanceClosenessStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Harmonic Closeness":
                GraphDistanceHarmonicClosenessStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Betwenness":
                GraphDistanceBetweenessStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Degree":
                DegreeStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Eigenvector":
                EigenvectorStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "HITS - authority":
                HITSAuthorityStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
            case "HITS - hub":
                HITSHubStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Eccentricity":
                GraphDistanceEccentricityStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            case "Modularity":
                GraphDistanceModularityStatisticOption(graph, numOfNodes, ascending, roleName, stateName);
                break;
            default:
                throw new NotImplementedException();
        }
    }

    private void RandomNStrategy(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        Node[] nodes = graph.getNodes().toArray();
        var rnd = new Random();
        for (int i = 0; i < numOfNodes; i++) {
            var index = rnd.nextInt(nodes.length);
            var selectedNode = nodes[index];
            selectedNode.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName);
            selectedNode.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
        }
    }

    private void RandomRandomStrategy(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        Node[] nodes = graph.getNodes().toArray();
        var rnd = new Random();
        for (int i = 0; i < numOfNodes; i++) {
            var index = rnd.nextInt(nodes.length);
            var selectedNode = nodes[index];
            var neighbours = graph.getNeighbors(selectedNode).toArray();
            index = rnd.nextInt(neighbours.length);
            selectedNode = neighbours[index];
            selectedNode.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName);
            selectedNode.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
        }
    }

    private void GraphDistanceClosenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new GraphDistance();
        eigenvector.setDirected(false);
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "closnesscentrality", nodeRoleName, nodeStateName);
    }

    private void GraphDistanceHarmonicClosenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new GraphDistance();
        eigenvector.setDirected(false);
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "harmonicclosnesscentrality", nodeRoleName, nodeStateName);
    }

    private void GraphDistanceBetweenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new GraphDistance();
        eigenvector.setDirected(false);
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "betweenesscentrality", nodeRoleName, nodeStateName);
    }

    private void DegreeStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var degree = new Degree();
        degree.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "Degree", nodeRoleName, nodeStateName);
    }

    private void EigenvectorStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new EigenvectorCentrality();
        eigenvector.setDirected(false);
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "eigencentrality", nodeRoleName, nodeStateName);
    }

    private void HITSAuthorityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var hits = new Hits();
        hits.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "Authority", nodeRoleName, nodeStateName);
    }

    private void HITSHubStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var hits = new Hits();
        hits.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "Hub", nodeRoleName, nodeStateName);
    }

    private void GraphDistanceEccentricityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new GraphDistance();
        eigenvector.setDirected(false);
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "eccentricity", nodeRoleName, nodeStateName);
    }

    private void GraphDistanceModularityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, String nodeRoleName, String nodeStateName) {
        var eigenvector = new Modularity();
        eigenvector.execute(graph);
        StatisticsOptions(graph, numOfNodes, descending, "modularity_class", nodeRoleName, nodeStateName);
    }

    private void StatisticsOptions(Graph graph, Integer numOfNodes, Boolean descending, String attributeName, String nodeRoleName, String nodeStateName) {
        var nodes = Arrays.stream(graph.getNodes().toArray()).collect(Collectors.toList());
        nodes.sort(Comparator.comparingDouble(node -> Double.parseDouble(node.getAttribute(attributeName).toString())));
        if (descending) {
            Collections.reverse(nodes);
        }
        for (int i = 0; i < numOfNodes; i++) {
            var chosenOne = nodes.get(i);
            chosenOne.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName);
            chosenOne.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
        }
    }
}
