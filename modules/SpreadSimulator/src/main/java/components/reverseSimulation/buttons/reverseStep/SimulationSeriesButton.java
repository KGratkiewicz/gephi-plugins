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
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import simulationModel.node.NodeStateDecorator;

import javax.swing.*;
import java.awt.*;
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
//        TODO tutaj zmiana
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
                runSimulation(stepNumber);
                this.simulation = reverseSimulationComponent.NewSeries(simulation);
            }
            runSimulation(stepNumber);
        }
        reverseSimulationComponent.initComponents();
        reverseSimulationComponent.repaint();
        reverseSimulationComponent.revalidate();
    }

    private void runSimulation(int stepNumber) {
        while(simulation.getStep() < stepNumber - 1) {
            simulation.Step();
        }

        List.of(simulation.getGraph().getNodes().toArray()).forEach(e ->
                e.setAttribute(ConfigLoader.colNameTempNodeState, e.getAttribute(ConfigLoader.colNameNodeState).toString()));
        simulation.Step();
        List.of(simulation.getGraph().getNodes().toArray()).forEach(e -> {
            if (e.getAttribute(ConfigLoader.colNameNodeState).equals(e.getAttribute(ConfigLoader.colNameTempNodeState))) {
                e.setAttribute(ConfigLoader.colNameTempNodeState, "");
            }
        });

        reverseSimulationComponent.initComponents();
        reverseSimulationComponent.revalidate();
        reverseSimulationComponent.repaint();
    }

    private boolean stopCondition(String examinedStateAndRole, int nodesToStop) {
        Optional<Double> coverage = reverseSimulationComponent.getCurrentSimulation().getNodeRoleDecoratorList().stream()
                .filter(nodeRoleDecorator -> examinedStateAndRole.split(":")[0]
                        .equals(nodeRoleDecorator.getNodeRole().getName()))
                .flatMap(nodeRoleDecorator -> nodeRoleDecorator.getNodeStates().stream())
                .filter(nodeStateDecorator -> examinedStateAndRole.split(":")[1]
                        .equals(nodeStateDecorator.getNodeState().getName()))
                .map(NodeStateDecorator::getCoverage)
                .findFirst();

        if (coverage.isPresent()) {
            double numberOfNodesInSimulation = reverseSimulationComponent.getCurrentSimulation().getGraph().getEdgeCount() * coverage.get();
            return numberOfNodesInSimulation <= nodesToStop;
        } else {
            return true;
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
