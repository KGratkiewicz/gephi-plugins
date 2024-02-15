package components.reverseSimulation.buttons.reverseStep.visibility;

import components.reverseSimulation.ReverseSimulationComponent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VisibilityOptionsButton extends JButton {
    private final ReverseSimulationComponent reverseSimulationComponent;
    private VisibilityOptionDialog dialog;

    public VisibilityOptionsButton(ReverseSimulationComponent reverseSimulationComponent) {
        this.setText("Visibility Options");
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.addActionListener(new ResultsListener());
    }

    private class ResultsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dialog = new VisibilityOptionDialog(reverseSimulationComponent, "Visibility Options");
            dialog.setVisible(true);
        }
    }
}
