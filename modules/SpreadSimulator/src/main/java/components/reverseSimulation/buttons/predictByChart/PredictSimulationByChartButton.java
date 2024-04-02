package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PredictSimulationByChartButton extends JButton {

    private final ReverseSimulationComponent reverseSimulationComponent;

    public PredictSimulationByChartButton(ReverseSimulationComponent reverseSimulationComponent) {
        this.setText("Predict Simulation By Chart");
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(new PredictSimulationByChartButton.PredictSimulationListener());
    }

    private class PredictSimulationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            reverseSimulationComponent.setState(2);
            reverseSimulationComponent.initComponents();
            reverseSimulationComponent.revalidate();
            reverseSimulationComponent.repaint();
        }
    }
}
