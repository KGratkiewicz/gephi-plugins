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
public class AutomaticPredictionOptionDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private JComboBox<String> rolesChoseFromList;
    private JComboBox centralityRateDropdown;
    private String stateAndRoleName;

    public AutomaticPredictionOptionDialog(ReverseSimulationComponent reverseSimulationComponent, String stateAndRoleName) {
        this.stateAndRoleName = stateAndRoleName;
        this.reverseSimulationComponent = reverseSimulationComponent;
        JFrame frame = new JFrame("Automatic Prediction Option");
        frame.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;

        var centralityRateLabel = new JLabel("Select Strategy:");
        centralityRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        var centralityRateOptions = new String[]{
                "Random", "Random-Random","Closeness", "Harmonic Closeness", "Betweenness", "Degree", "Eigenvector", "HITS - hub", "HITS - authority", "Eccentricity", "Modularity"
        };
        centralityRateDropdown = new JComboBox<>(centralityRateOptions);
        centralityRateDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> createSimulationConfigurationDialog(frame));

        mainPanel.add(centralityRateLabel, gbc);
        gbc.gridy++;
        mainPanel.add(centralityRateDropdown, gbc);
        gbc.gridy++;
        mainPanel.add(okButton, gbc);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(frame);
        frame.setVisible(true);
    }

    private void createSimulationConfigurationDialog(JFrame frame) {
        try {
            frame.setVisible(false);
            frame.dispose();
            var centralityMethod = centralityRateDropdown.getSelectedItem().toString();
            new SimulationAutomaticConfigurationOptionDialog(reverseSimulationComponent, getStateAndRoleName(), centralityMethod);
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Please enter strategy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
