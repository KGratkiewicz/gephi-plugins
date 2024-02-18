package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPredictionButton extends JButton {

    private final ReverseSimulationComponent reverseSimulationComponent;

    public StartPredictionButton(ReverseSimulationComponent reverseSimulationComponent) {
        this.setText("Start Prediction");
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(new AutomaticPredictionListener());
    }

    private class AutomaticPredictionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new PredictionTypeDialog(reverseSimulationComponent, "Predict Type");
            reverseSimulationComponent.initComponents();
            reverseSimulationComponent.revalidate();
            reverseSimulationComponent.repaint();
        }
    }
}
