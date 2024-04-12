package components.reverseSimulation.buttons.reverseStep;

import components.reverseSimulation.ReverseSimulationComponent;
import components.simulation.Simulation;
import configLoader.ConfigLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StepButton extends JButton {

    private final Simulation simulation;
    private final ReverseSimulationComponent reverseSimulationComponent;

    public StepButton(Simulation simulation, ReverseSimulationComponent reverseSimulationComponent) {
        this.setText("Step");
        this.simulation = simulation;
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(new StepButton.StartSimulationReverseSeriesListener());
    }

    private class StartSimulationReverseSeriesListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            var table = simulation.getGraph().getModel().getNodeTable();
            if(table.getColumn(ConfigLoader.colNameTempNodeState) == null)
                table.addColumn(ConfigLoader.colNameTempNodeState, String.class);
            List.of(simulation.getGraph().getNodes().toArray()).forEach(node ->
                    node.setAttribute(ConfigLoader.colNameTempNodeState, node.getAttribute(ConfigLoader.colNameNodeState).toString()));
            simulation.Step();
            reverseSimulationComponent.initComponents();
            reverseSimulationComponent.revalidate();
            reverseSimulationComponent.repaint();
        }
    }
}
