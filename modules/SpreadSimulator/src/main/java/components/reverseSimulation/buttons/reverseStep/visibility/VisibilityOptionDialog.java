package components.reverseSimulation.buttons.reverseStep.visibility;

import components.reverseSimulation.ReverseSimulationComponent;
import components.simulationLogic.SimulationComponent;
import configLoader.ConfigLoader;
import helper.ApplySimulationHelper;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import simulationModel.node.NodeRoleDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class VisibilityOptionDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private String examinedStateAndRole;
    private JComboBox<String> rolesChoseFromList;
    private boolean successful = false;
    private int option;

    public VisibilityOptionDialog(ReverseSimulationComponent reverseSimulationComponent, String name) {
        this.reverseSimulationComponent = reverseSimulationComponent;
        JFrame frame = new JFrame(name);
        JPanel mainPanel = new JPanel(new BorderLayout());

        List<Pair<String, String>> stateAndRoleNames = reverseSimulationComponent
                .getSimulationModel()
                .getNodeRoles()
                .stream()
                .flatMap(e -> e.getNodeStates()
                        .stream()
                        .map(nodeStateDecorator -> Pair.of(e.getNodeRole().getName(), nodeStateDecorator.getNodeState().getName())))
                .collect(Collectors.toList());
        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel<>();
        comboBoxModel.addAll(stateAndRoleNames.stream().map(e -> e.first() + ":" + e.second()).collect(Collectors.toList()));

        rolesChoseFromList = new JComboBox<>(comboBoxModel);
        mainPanel.add(new JLabel("Choose a value:"));
        mainPanel.add(rolesChoseFromList, BorderLayout.NORTH);

        Panel buttonPanel = new Panel();
        JButton resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(e -> resetViewAction());
        buttonPanel.add(resetViewButton);

        JButton predictButton = new JButton("Paint single simulation");
        predictButton.addActionListener(e -> predictAction());
        buttonPanel.add(predictButton);

        JButton heatMapButton = new JButton("Paint heat map");
        heatMapButton.addActionListener(e -> heatMapAction());
        buttonPanel.add(heatMapButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(frame);
        frame.setVisible(true);
    }

    private void resetViewAction() {
        onOk();
        paintByState();
    }

    private void predictAction() {
        onOk();
        paintPredictSimulation();
    }

    private void heatMapAction() {
        onOk();
        paintHeatMap();
    }

    private void onOk() {
        try {
            examinedStateAndRole = rolesChoseFromList.getSelectedItem().toString();
            successful = true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Some error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void paintByState() {
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        var nodes = List.of(graph.getNodes().toArray());
        List<NodeRoleDecorator> nodeRoles = SimulationComponent.getInstance().getSimulationModel().getNodeRoles();
        ApplySimulationHelper.GenerateColorPaintings(nodeRoles);
        ApplySimulationHelper.PaintGraph(nodes, nodeRoles);
    }

    private void paintPredictSimulation() {
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        var nodes = List.of(graph.getNodes().toArray());
        String stateName = getExaminedStateAndRole().split(":")[1];

        nodes.forEach(node -> {
            if (node.getAttribute(ConfigLoader.colNameRootState).equals(stateName)
                    && node.getAttribute(ConfigLoader.colNameTempNodeState).equals(stateName)) {
                node.setColor(Color.green);
            } else if (node.getAttribute(ConfigLoader.colNameRootState).equals(stateName)) {
                node.setColor(Color.red);
            } else if (node.getAttribute(ConfigLoader.colNameTempNodeState).equals(stateName)) {
                node.setColor(Color.yellow);
            } else {
                node.setColor(Color.GRAY);
            }
        });
    }

    private void paintHeatMap() {
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        var table = graph.getModel().getNodeTable();
        if(table.getColumn(ConfigLoader.colNameHeatMapValue) == null)
            table.addColumn(ConfigLoader.colNameHeatMapValue, String.class);
        var nodes = List.of(graph.getNodes().toArray());

//        TODO nie bierze ostatniej symulacji trzeba dodac
        var simulationReport = reverseSimulationComponent.getSimulationStatesList();
        simulationReport.forEach(oneSimulationReport -> oneSimulationReport.forEach(nodeData -> {
            if (nodeData.getNodeTempState().equals(getExaminedStateAndRole().split(":")[1])) {
                var nodeToIncrease = nodes.stream().filter(node -> node.getStoreId() == nodeData.getNodeStoreId()).findFirst();
                nodeToIncrease.ifPresent(node -> node.setAttribute(ConfigLoader.colNameHeatMapValue, String.valueOf(getNodeIntValue(node, ConfigLoader.colNameHeatMapValue) + 1)));
            }
        }));

        paintNodes(nodes);
    }

    private void paintNodes(List<Node> nodes) {
        var highestHeatValue = nodes.stream().map(node -> getNodeIntValue(node, ConfigLoader.colNameHeatMapValue)).sorted(Comparator.reverseOrder()).findFirst().get();
        nodes.forEach(node -> node.setColor(getColorForValue(getNodeIntValue(node, ConfigLoader.colNameHeatMapValue), highestHeatValue, Color.lightGray, new Color(255, 0, 0))));
    }

    private int getNodeIntValue(Node node, String colName) {
        var stringValue = String.valueOf(node.getAttribute(colName));
        return stringValue.equals("null") ? 0 : Integer.parseInt(stringValue);
    }

    public static Color getColorForValue(int value, int maxValue, Color lowColor, Color highColor) {
        value = Math.max(0, Math.min(value, maxValue));

        double factor = Math.pow((double) value / maxValue, 0.4);
        int red = (int) (lowColor.getRed() + factor * (highColor.getRed() - lowColor.getRed()));
        int green = (int) (lowColor.getGreen() + factor * (highColor.getGreen() - lowColor.getGreen()));
        int blue = (int) (lowColor.getBlue() + factor * (highColor.getBlue() - lowColor.getBlue()));

        red = Math.max(0, Math.min(red, 255));
        green = Math.max(0, Math.min(green, 255));
        blue = Math.max(0, Math.min(blue, 255));

        return new Color(red, green, blue);
    }

}
