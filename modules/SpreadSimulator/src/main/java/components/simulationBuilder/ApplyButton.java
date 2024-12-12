package components.simulationBuilder;

import configLoader.ConfigLoader;
import helper.ApplySimulationHelper;
import simulationModel.node.NodeRoleDecorator;
import simulationModel.SimulationModel;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.gephi.statistics.plugin.*;
import org.openide.util.Lookup;
import org.openide.util.NotImplementedException;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.stream.Collectors;

public class ApplyButton extends JButton {

    private final SimulationBuilderComponent simulationBuilderComponent;

    public ApplyButton(SimulationBuilderComponent simulationBuilderComponent) {
        this.simulationBuilderComponent = simulationBuilderComponent;
        this.setText("Apply");
        this.addActionListener(new ApplyButtonActionListener(this.simulationBuilderComponent, simulationBuilderComponent.getNodeRoles()));
    }

    private class ApplyButtonActionListener implements ActionListener {

        private final SimulationBuilderComponent component;
        private final List<NodeRoleDecorator> nodeRoles;
        private final SimulationModel simulationModel;

        private ApplyButtonActionListener(SimulationBuilderComponent component, List<NodeRoleDecorator> nodeRoles) {
            this.component = component;
            this.nodeRoles = nodeRoles;
            this.simulationModel = component.getSimulationModel();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            simulationModel.setNodeRoles(nodeRoles);
            simulationModel.setName(ApplySimulationHelper.GenerateName(nodeRoles));
            try {
                Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
                ApplySimulationHelper.ClearModel(graph);
                ApplySimulationHelper.CrateModelColumns(graph);
                ApplySimulationHelper.Apply(graph, simulationModel);
                var rules = component.advancedRules.keySet();
                for (var rule: rules) {
                    var names = rule.split("_");
                    var rulesList =component.advancedRules.get(rule);
                    for(var rul : rulesList){
                        var nodeRoleDecorator = nodeRoles.stream().filter(role -> role.getNodeRole().getName().toString().equals(names[0])).collect(Collectors.toList()).get(0);
                        ExecuteRule(rul, nodeRoleDecorator, names[1]);
                    }
                }
                ApplySimulationHelper.PaintGraph(Arrays.asList(graph.getNodes().toArray()), nodeRoles);
            }
            catch (NullPointerException ex){
                JOptionPane.showMessageDialog(null, "Setup graph model first");
            }
        }
        private void ExecuteRule(AdvancedRule rule, NodeRoleDecorator nodeRoleDecorator, String stateName){
            var graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
            var graph = graphModel.getGraph();
            String centralityMethod = rule.rule;
            Integer numOfNodes = rule.coverage;
            boolean ascending = rule.ascending;
            switch (centralityMethod) {
                case "Random":
                    RandomNStrategy(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Random-Random":
                    RandomRandomStrategy(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Equal Degree":
                    throw new NotImplementedException();
                case "Closeness":
                    GraphDistanceClosenessStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Harmonic Closeness":
                    GraphDistanceHarmonicClosenessStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Betwenness":
                    GraphDistanceBetweenessStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Degree":
                    DegreeStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Eigenvector":
                    EigenvectorStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "HITS - authority":
                    HITSAuthorityStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                case "HITS - hub":
                    HITSHubStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Eccentricity":
                    GraphDistanceEccentricityStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                case "Modularity":
                    GraphDistanceModularityStatisticOption(graph, numOfNodes, ascending, nodeRoleDecorator, stateName);
                    break;
                default:
                    throw new NotImplementedException();
            }
        }

        private void RandomNStrategy(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var nodes = Arrays.stream(graph.getNodes().toArray())
                    .filter(node -> node.getAttribute(ConfigLoader.colNameNodeState.toString())
                            .toString()
                            .equals(nodeRoleName.getDefaultStateName().toString()))
                    .collect(Collectors.toList());
            if (nodes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select correct default state.");
                return;
            }
            var rnd = new Random();
            var selectedNodes = new HashSet<Node>();
            for (int i = 0; i < numOfNodes; i++) {
                int index = rnd.nextInt(nodes.size());
                var selectedNode = nodes.get(index);
                if (!selectedNodes.add(selectedNode)) {
                    i--;
                    continue;
                }
                selectedNode.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName.getNodeRole().getName().toString());
                selectedNode.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
            }
        }


        private void RandomRandomStrategy(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var nodes = Arrays.stream(graph.getNodes().toArray())
                    .filter(node -> node.getAttribute(ConfigLoader.colNameNodeState.toString())
                            .toString()
                            .equals(nodeRoleName.getDefaultStateName().toString()))
                    .collect(Collectors.toList());
            if (nodes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Select correct default state.");
                return;
            }
            var rnd = new Random();
            var selectedNodes = new HashSet<Node>();
            for (int i = 0; i < numOfNodes; i++) {
                int index = rnd.nextInt(nodes.size());
                var selectedNode = nodes.get(index);
                if (!selectedNodes.add(selectedNode)) {
                    i--;
                    continue;
                }
                var neighbours = Arrays.asList(graph.getNeighbors(selectedNode).toArray());
                if (!neighbours.isEmpty()) {
                    int neighbourIndex = rnd.nextInt(neighbours.size());
                    var neighbourNode = neighbours.get(neighbourIndex);
                    neighbourNode.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName.getNodeRole().getName().toString());
                    neighbourNode.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
                }
            }
        }


        private void GraphDistanceClosenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new GraphDistance();
            eigenvector.setDirected(false);
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "closnesscentrality", nodeRoleName, nodeStateName);
        }

        private void GraphDistanceHarmonicClosenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new GraphDistance();
            eigenvector.setDirected(false);
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "harmonicclosnesscentrality", nodeRoleName, nodeStateName);
        }

        private void GraphDistanceBetweenessStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new GraphDistance();
            eigenvector.setDirected(false);
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "betweenesscentrality", nodeRoleName, nodeStateName);
        }

        private void DegreeStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var degree = new Degree();
            degree.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "Degree", nodeRoleName, nodeStateName);
        }

        private void EigenvectorStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new EigenvectorCentrality();
            eigenvector.setDirected(false);
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "eigencentrality", nodeRoleName, nodeStateName);
        }

        private void HITSAuthorityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var hits = new Hits();
            hits.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "Authority", nodeRoleName, nodeStateName);
        }

        private void HITSHubStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var hits = new Hits();
            hits.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "Hub", nodeRoleName, nodeStateName);
        }

        private void GraphDistanceEccentricityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new GraphDistance();
            eigenvector.setDirected(false);
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "eccentricity", nodeRoleName, nodeStateName);
        }

        private void GraphDistanceModularityStatisticOption(Graph graph, Integer numOfNodes, Boolean descending, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var eigenvector = new Modularity();
            eigenvector.execute(graph);
            StatisticsOptions(graph, numOfNodes, descending, "modularity_class", nodeRoleName, nodeStateName);
        }


        private void StatisticsOptions(Graph graph, Integer numOfNodes, Boolean descending, String attributeName, NodeRoleDecorator nodeRoleName, String nodeStateName) {
            var nodes = Arrays.stream(graph.getNodes().toArray()).collect(Collectors.toList());
            nodes = nodes.stream().filter(node -> node.getAttribute(ConfigLoader.colNameNodeState.toString()).toString() == nodeRoleName.getDefaultStateName().toString()).collect(Collectors.toList());

            nodes.sort(Comparator.comparingDouble(node -> Double.parseDouble(node.getAttribute(attributeName).toString())));
            if (descending) {
                Collections.reverse(nodes);
            }

            for (int i = 0; i < numOfNodes; i++) {
                var attributeValue = Double.parseDouble(nodes.get(i).getAttribute(attributeName).toString());
                var equalAttributesNodes = nodes.stream().filter(node -> Double.parseDouble(node.getAttribute(attributeName).toString()) == attributeValue).collect(Collectors.toList());
                Collections.shuffle(equalAttributesNodes);

                for(int j = 0; equalAttributesNodes.size() > j && i < numOfNodes; j++, i++){
                    var chosenOne = equalAttributesNodes.get(j);
                    chosenOne.setAttribute(ConfigLoader.colNameNodeRole, nodeRoleName.getNodeRole().getName().toString());
                    chosenOne.setAttribute(ConfigLoader.colNameNodeState, nodeStateName);
                }
                i--;
            }
        }
    }
}
