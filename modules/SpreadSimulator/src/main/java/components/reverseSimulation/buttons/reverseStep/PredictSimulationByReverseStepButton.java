package components.reverseSimulation.buttons.reverseStep;


import components.reverseSimulation.ReverseSimulationComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PredictSimulationByReverseStepButton extends JButton {

    private final ReverseSimulationComponent reverseSimulationComponent;

    public PredictSimulationByReverseStepButton(ReverseSimulationComponent reverseSimulationComponent) {
        this.setText("Use Reverse Step Simulation");
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(new PredictSimulationByReverseStepButton.ReverseStepSimulationListener());
    }

    private class ReverseStepSimulationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            reverseSimulationComponent.setState(1);
            reverseSimulationComponent.initComponents();
            reverseSimulationComponent.revalidate();
            reverseSimulationComponent.repaint();
        }
    }
}
