package components.simulationLogic;

import components.simulation.Simulation;
import components.simulation.SimulationRunner;
import configLoader.ConfigLoader;
import org.gephi.utils.longtask.api.LongTaskExecutor;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.utils.progress.ProgressTicketProvider;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;

public class SimulationSeriesButton extends RunSimulationButton {

    public SimulationSeriesButton(Simulation simulation, SimulationComponent simulationComponent) {
        this.setText(ConfigLoader.buttonLabelRunSimulationSeries);
        this.simulation = simulation;
        this.simulationComponent = simulationComponent;
        this.addActionListener(e -> openInputDialogAndRunSimulation());
    }

    private void openInputDialogAndRunSimulation() {

        CustomInputDialog dialog = new CustomInputDialog(null);
        dialog.setVisible(true);
        dialog.dispose();
        if (dialog.isSuccessful()) {
            LongTaskExecutor executor = new LongTaskExecutor(true);
            var simulationRunner = new SimulationRunner(this);
            executor.execute(simulationRunner, simulationRunner::runSimulationSeries);

        }
        simulationComponent.initComponents();
        simulationComponent.repaint();
        simulationComponent.revalidate();
    }

    private class CustomInputDialog extends JDialog {
        private JTextField simulationStepField;
        private JTextField stepsField;
        private JCheckBox visualizationCheckbox;
        private JTextField delayField;
        private boolean successful = false;

        public CustomInputDialog(Frame parent) {
            super(parent, "Input Parameters", true);

            setLayout(new GridLayout(5, 2));
            stepsField = new JTextField(10);
            simulationStepField = new JTextField(10);
            visualizationCheckbox = new JCheckBox("Visualization");
            delayField = new JTextField(10);
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> onOk());
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> setVisible(false));

            add(new JLabel("Conduct Simulations:"));
            add(simulationStepField);
            add(new JLabel("Conduct Steps:"));
            add(stepsField);
            add(new JLabel("Visualization:"));
            add(visualizationCheckbox);
            add(new JLabel("Delay:"));
            add(delayField);
            add(okButton);
            add(cancelButton);

            pack();
            setLocationRelativeTo(parent);
        }

        private void onOk() {
            try {
                simulationsNumber = Integer.parseInt(simulationStepField.getText());
                conductSteps = Integer.parseInt(stepsField.getText());
                visualization = visualizationCheckbox.isSelected();
                try{
                    delay = Integer.parseInt(delayField.getText());
                }
                catch(NumberFormatException e){
                    delay = 1;
                }

                if (simulationsNumber <= 0 || conductSteps <= 0 || (delay <= 0 && visualization)) {
                    JOptionPane.showMessageDialog(this, "Values should be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                successful = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid integers for steps and delay.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        public boolean isSuccessful() {
            return successful;
        }
    }
}
