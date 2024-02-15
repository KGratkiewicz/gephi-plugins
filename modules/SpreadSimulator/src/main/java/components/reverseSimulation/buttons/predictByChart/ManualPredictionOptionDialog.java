package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ManualPredictionOptionDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private String stateAndRoleName;
    private List<AdvancedRule> rulesList;
    private JPanel rulesPanel;
    private JFrame mainFrame = new JFrame("Manual Prediction Option");

    public ManualPredictionOptionDialog(ReverseSimulationComponent reverseSimulationComponent, String stateAndRoleName) {
        rulesList = new ArrayList<>();
        this.reverseSimulationComponent = reverseSimulationComponent;
        this.stateAndRoleName = stateAndRoleName;
        initComponents();
    }

    private void initComponents() {
        mainFrame.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

//        dodanie grafu
        JLabel titleLabel = new JLabel("Advanced rules");
        JPanel labelPanel = new JPanel();
        labelPanel.add(titleLabel);
        mainPanel.add(labelPanel, gbc);

//        dodanie scrollPaneList
        rulesPanel = new JPanel();
        rulesPanel.setLayout(new GridBagLayout());
        GridBagConstraints rulesGbc = new GridBagConstraints();
        rulesGbc.fill = GridBagConstraints.NONE;
        rulesGbc.insets = new Insets(4, 4, 4, 4);
        rulesGbc.gridy = 0;
        rulesGbc.anchor = GridBagConstraints.NORTHWEST;

        rulesList.forEach(advancedRule -> {
            JLabel label = new JLabel(advancedRule.rule);
            JButton button = new JButton("Delete");
            button.addActionListener(e -> deleteRule(advancedRule));
            rulesGbc.gridx = 0;
            rulesPanel.add(label, rulesGbc);
            rulesGbc.gridx = 1;
            rulesPanel.add(button, rulesGbc);
            rulesGbc.gridy++;
        });

        JScrollPane scrollPane = new JScrollPane(rulesPanel);
        scrollPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        gbc.gridy = 1;
        mainPanel.add(scrollPane, gbc);

        gbc.gridy = 2;
        gbc.ipady = 0;
        gbc.gridwidth = 1;
        JButton addRuleButton = new JButton("Add rule");
        addRuleButton.addActionListener(e -> addRule());
        mainPanel.add(addRuleButton, gbc);

        gbc.gridx = 1;
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> createSimulationConfigurationDialog());
        mainPanel.add(okButton, gbc);

        mainFrame.add(mainPanel);
        mainFrame.setSize(400, 700);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(this);
        mainFrame.setVisible(true);
    }

    private void deleteRule(AdvancedRule advancedRule) {
        rulesList.remove(advancedRule);
        updateRulesPanel();
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void updateRulesPanel() {
        rulesPanel.removeAll();
        GridBagConstraints rulesGbc = new GridBagConstraints();
        rulesGbc.fill = GridBagConstraints.NONE;
        rulesGbc.insets = new Insets(4, 4, 4, 4);
        rulesGbc.gridy = 0;
        rulesGbc.anchor = GridBagConstraints.NORTHWEST;

        rulesList.forEach(advancedRule -> {
            JLabel label = new JLabel(advancedRule.toString());
            JButton button = new JButton("Delete");
            button.addActionListener(e -> deleteRule(advancedRule));
            rulesGbc.gridx = 0;
            rulesPanel.add(label, rulesGbc);
            rulesGbc.gridx = 1;
            rulesPanel.add(button, rulesGbc);
            rulesGbc.gridy++;
        });
    }

    private void addRule() {
        AddRuleDialog dialog = new AddRuleDialog(null);
        dialog.setVisible(true);
        dialog.dispose();
        if (dialog.isSuccessful()) {
            rulesList.add(dialog.getAdvancedRule());
        }
        updateRulesPanel();
        mainFrame.revalidate();
        mainFrame.repaint();

    }

    @Getter
    @Setter
    private class AddRuleDialog extends JDialog {

        private boolean successful = false;
        private JTextField numOfNodesInput;
        private JComboBox centralityRateDropdown;
        private JCheckBox descendingCheckbox;
        private AdvancedRule advancedRule;

        public AddRuleDialog(JFrame parent) {
            super(parent, "Add rule", true);
            JPanel mainPanel = new JPanel();

            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            var numOfNodesPanel = new JPanel(new GridBagLayout());
            var constraints = new GridBagConstraints();
            constraints.anchor = GridBagConstraints.WEST;
            var numOfNodesLabel = new JLabel("Number of nodes:");
            numOfNodesInput = new JTextField(5);
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.insets = new Insets(0, 0, 0, 5);
            numOfNodesPanel.add(numOfNodesLabel, constraints);
            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.insets = new Insets(0, 0, 0, 0);
            numOfNodesPanel.add(numOfNodesInput, constraints);

            var centralityRateLabel = new JLabel("Select Strategy:");
            centralityRateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            var centralityRateOptions = new String[]{
                    "Random", "Random-Random","Closeness", "Harmonic Closeness", "Betweenness", "Degree", "Eigenvector", "HITS - hub", "HITS - authority", "Eccentricity", "Modularity"
            };
            centralityRateDropdown = new JComboBox<>(centralityRateOptions);
            centralityRateDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);

            descendingCheckbox = new JCheckBox("ascending");
            var applyButton = new JButton("Apply");
            applyButton.addActionListener(e -> apply());

            var buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(applyButton);

            mainPanel.add(numOfNodesPanel);
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(centralityRateLabel);
            mainPanel.add(Box.createVerticalStrut(5));
            mainPanel.add(centralityRateDropdown);
            mainPanel.add(Box.createVerticalStrut(10));
            mainPanel.add(descendingCheckbox);
            mainPanel.add(buttonPanel);

            add(mainPanel);
            pack();
            setLocationRelativeTo(parent);
        }

        private void apply() {
            try {
                Integer.parseInt(numOfNodesInput.getText());
                successful = true;
                var centralityMethod = centralityRateDropdown.getSelectedItem().toString();
                var numOfNodesString = numOfNodesInput.getText();
                var numOfNodes = Integer.valueOf(numOfNodesString);
                advancedRule = new AdvancedRule(centralityMethod, numOfNodes, descendingCheckbox.isSelected());
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number for 'Number of nodes'.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createSimulationConfigurationDialog() {
        try {
            mainFrame.setVisible(false);
            mainFrame.dispose();
            new SimulationMaunualConfigurationOptionDialog(reverseSimulationComponent, getStateAndRoleName(), rulesList);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integers for steps and delay.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
