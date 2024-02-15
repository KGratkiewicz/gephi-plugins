package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class PredictionTypeDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private JComboBox<String> rolesChoseFromList;

    public PredictionTypeDialog(ReverseSimulationComponent reverseSimulationComponent, String name) {
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
        JButton automaticPredictionButton = new JButton("Automatic Prediction");
        automaticPredictionButton.addActionListener(e -> createAutomaticPredictionDialog(frame));
        buttonPanel.add(automaticPredictionButton);

        JButton manualPredictionButton = new JButton("Manual Prediction");
        manualPredictionButton.addActionListener(e -> createManualPredictionDialog(frame));
        buttonPanel.add(manualPredictionButton);


        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(frame);
        frame.setVisible(true);
    }

    private void createManualPredictionDialog(JFrame frame) {
        try {
            frame.setVisible(false);
            frame.dispose();
            new ManualPredictionOptionDialog(reverseSimulationComponent, rolesChoseFromList.getSelectedItem().toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for steps and delay.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAutomaticPredictionDialog(JFrame frame) {
        try {
            frame.setVisible(false);
            frame.dispose();
            new AutomaticPredictionOptionDialog(reverseSimulationComponent, rolesChoseFromList.getSelectedItem().toString());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for steps and delay.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
