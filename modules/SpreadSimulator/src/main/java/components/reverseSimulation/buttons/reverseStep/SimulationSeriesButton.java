package components.reverseSimulation.buttons.reverseStep;

import components.reverseSimulation.ReverseSimulationComponent;
import components.simulation.Simulation;
import components.simulationLogic.SimulationComponent;
import configLoader.ConfigLoader;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.openide.util.Lookup;
import simulationModel.node.NodeStateDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class SimulationSeriesButton extends JButton {

    private final ReverseSimulationComponent reverseSimulationComponent;
    private Simulation simulation;

    public SimulationSeriesButton(Simulation simulation, ReverseSimulationComponent reverseSimulationComponent) {
        this.setText(ConfigLoader.buttonLabelRunSimulationSeries);
        this.simulation = simulation;
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(e -> openInputDialogAndRunSimulation());
    }

    private void openInputDialogAndRunSimulation() {
        OptionDialog dialog = new OptionDialog(null, reverseSimulationComponent, "Stop condition");
        dialog.setVisible(true);
        dialog.dispose();
        var stepNumber = SimulationComponent.getInstance().getCurrentSimulation().getStep();
        if (dialog.isSuccessful()) {
            Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
            var table = graph.getModel().getNodeTable();
            if(table.getColumn(ConfigLoader.colNameTempNodeState) == null)
                table.addColumn(ConfigLoader.colNameTempNodeState, String.class);

            for(int i = 1; i < dialog.getConductSimulations(); i++){
                runSimulation(stepNumber, dialog.examinedStateAndRole);
                this.simulation = reverseSimulationComponent.NewSeries(simulation);
            }
            runSimulation(stepNumber, dialog.examinedStateAndRole);
        }

        InfoDialog infoDialog = new InfoDialog(null, "Done");
        infoDialog.setVisible(true);
        infoDialog.dispose();

        reverseSimulationComponent.initComponents();
        reverseSimulationComponent.repaint();
        reverseSimulationComponent.revalidate();
    }

    private void runSimulation(int stepNumber, String examinedStateAndRole) {
        var table = simulation.getGraph().getModel().getNodeTable();
        if(table.getColumn(ConfigLoader.colNameTempNodeState) == null)
            table.addColumn(ConfigLoader.colNameTempNodeState, String.class);
        while(!(simulation.getStep() > stepNumber || stateNodeCount(examinedStateAndRole) <= 1)) {
            List.of(simulation.getGraph().getNodes().toArray()).forEach(e ->
                    e.setAttribute(ConfigLoader.colNameTempNodeState, e.getAttribute(ConfigLoader.colNameNodeState).toString()));
            simulation.Step();
        }

        reverseSimulationComponent.initComponents();
        reverseSimulationComponent.revalidate();
        reverseSimulationComponent.repaint();
    }

    private Long stateNodeCount (String examinedStateAndRole) {
        Long stateNodesCount = Arrays.stream(reverseSimulationComponent.getCurrentSimulation().getGraph().getNodes().toArray())
                .filter(node -> node.getAttribute(ConfigLoader.colNameNodeState).equals(examinedStateAndRole.split(":")[1])).count();

        return stateNodesCount;
    }

    public class InfoDialog extends JDialog {
        public InfoDialog(Frame parent, String message) {
            super(parent, "InfoDialog", true);

            JLabel infoLabel = new JLabel(message);
            add(infoLabel);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> onOk());
            add(okButton);

            pack();
            setLocationRelativeTo(parent);
        }

        private void onOk() {
            try {
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Some error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Setter
    @Getter
    public class OptionDialog extends JDialog {

        private String examinedStateAndRole;
        private JComboBox<String> rolesChoseFromList;
        private JTextField simulationNumberFiled;
        private int conductSimulations;
        private int nodesNumberToStop;
        private boolean successful = false;

        public OptionDialog(Frame parent, ReverseSimulationComponent reverseSimulationComponent, String name) {
            super(parent, name, true);
            setLayout(new GridLayout(4, 2));

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

            simulationNumberFiled = new JTextField(10);
            add(new JLabel("Conduct Simulations:"));
            add(simulationNumberFiled);

            rolesChoseFromList = new JComboBox<>(comboBoxModel);
            add(new JLabel("Choose a value:"));
            add(rolesChoseFromList);

            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> onOk());
            add(okButton);

            pack();
            setLocationRelativeTo(parent);
        }

        private void onOk() {
            try {
                examinedStateAndRole = Objects.requireNonNull(rolesChoseFromList.getSelectedItem()).toString();
                conductSimulations = Integer.parseInt(simulationNumberFiled.getText());
                successful = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Some error.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

}
