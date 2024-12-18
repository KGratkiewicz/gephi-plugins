package components.simulation;

import components.simulationLogic.report.SimulationStepReport;
import configLoader.ConfigLoader;
import helper.ApplySimulationHelper;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.openide.util.NotImplementedException;
import simulationModel.SimulationModel;
import simulationModel.transition.Transition;
import simulationModel.transition.TransitionCondition;
import simulationModel.transition.TransitionNoCondition;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;


public class SimulationCommunityPreasure extends Simulation {

    public SimulationCommunityPreasure(Graph graph, SimulationModel simulationModel) {
        super(graph, simulationModel);
    }

    @Override
    protected boolean Validate(){
        if(!graph.getModel().getEdgeTable().hasColumn("WCN")){
            JOptionPane.showMessageDialog(null, "Run Weighed Common Neighbours statistics first", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public void Step() {
        step += 1;
        var table = graph.getModel().getNodeTable();
        if (!table.hasColumn(ConfigLoader.colNameNewNodeState))
            table.addColumn(ConfigLoader.colNameNewNodeState, String.class);
        var nodes = graph.getNodes();

        var selectedNodes = new ArrayList<>(List.of(nodes.toArray()));

        for (Node node : selectedNodes) {
            node.setAttribute(ConfigLoader.colNameNewNodeState, node.getAttribute(ConfigLoader.colNameNodeState).toString());
            List<Transition> transitions = simulationModel.getNodeRoles().stream().filter(role -> role.getNodeRole().getName().toString().equals(node.getAttribute(ConfigLoader.colNameNodeRole).toString())).findFirst().get().getNodeRole().getTransitionMap();
            var probabilityTransition = transitions.stream().filter(transition -> transition.getSourceState().getName().equals(node.getAttribute(ConfigLoader.colNameNodeState).toString())).collect(Collectors.toList());
            Collections.shuffle(probabilityTransition);
            for (Transition transition : probabilityTransition) {
                switch (transition.getTransitionType()){
                    case zeroProbability:
                        break;
                    case noConditionProbability:
                        NoConditionProbabilityNode(node, transition);
                        break;
                    case conditionProbability:
                        ConditionProbabilityNode(graph, node, transition);
                        break;
                    default:
                        throw new NotImplementedException(ConfigLoader.messageErrorUnknownTransitionType);
                }
            }
        }
        for (Node n : selectedNodes) {
            n.setAttribute(ConfigLoader.colNameNodeState, n.getAttribute(ConfigLoader.colNameNewNodeState).toString());
        }

        ApplySimulationHelper.PaintGraph(List.of(nodes.toArray()), simulationModel.getNodeRoles());
        table.removeColumn(ConfigLoader.colNameNewNodeState);
        GenerateNodeDecoratorList();
        this.report.add(new SimulationStepReport(this.step, this.nodeRoleDecoratorList));
    }

    private void NoConditionProbabilityNode(Node node, Transition transition) {
        Random rnd = new Random();
        var trn = (TransitionNoCondition) transition;
        var x = rnd.nextDouble();
        if (trn.getProbability() < x) {
            return;
        }
        ChangeState(node, trn.getDestinationState());
    }

    private void ConditionProbabilityNode(Graph graph, Node node, Transition transition) {
        var random = new Random();
        var edgesOfNode = graph.getEdges(node);
        var selectedEdges = edgesOfNode.toCollection().stream().filter(edge -> {
            var roll = random.nextDouble();
            var wcnAttribute = edge.getAttribute("WCN");
            double threshold = wcnAttribute != null ? Double.parseDouble(wcnAttribute.toString()) : 0.0;
            return !(roll > threshold);
        }).collect(Collectors.toList());

        var sourceNodes = selectedEdges.stream().map(x -> x.getSource()).collect(Collectors.toList());
        var targetNodes = selectedEdges.stream().map(x -> x.getTarget()).collect(Collectors.toList());


        var resultSet = new LinkedHashSet<Node>();
        resultSet.addAll(sourceNodes);
        resultSet.addAll(targetNodes);

        var trn = (TransitionCondition) transition;
        if(!IsInNeighbourhood(resultSet, trn))
            return;

        var neighbours = graph.getNeighbors(node).toArray();
        var neighboursCount = neighbours.length;
        var destinationStateNeighbours = Arrays.stream(neighbours).filter(x -> x.getAttribute(ConfigLoader.colNameNodeState).toString().equals(trn.getDestinationState().getName())).count();
        var communityPressureVariable = (double) destinationStateNeighbours/neighboursCount;

        Random rnd = new Random();
        if (trn.getProbability() * communityPressureVariable < rnd.nextDouble()) {
            return;
        }
        ChangeState(node, trn.getDestinationState());
    }

    private boolean IsInNeighbourhood(LinkedHashSet<Node> neighbours, TransitionCondition trn) {
        var provListName = neighbours.stream().filter(n -> {
            var nodeStateName = n.getAttribute(ConfigLoader.colNameNodeState).toString();
            return trn.getProvocativeNeighborName().contains(nodeStateName);
        }).collect(Collectors.toList());
        return provListName.stream().count() > 0;
    }
}
