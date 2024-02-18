package components.reverseSimulation.buttons.predictByChart;

import components.reverseSimulation.ReverseSimulationComponent;
import it.unimi.dsi.fastutil.Pair;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class AutomaticPredictionOptionDialog extends JDialog {

    private ReverseSimulationComponent reverseSimulationComponent;
    private JComboBox<String> rolesChoseFromList;
    private JComboBox centralityRateDropdown;
    private String stateAndRoleName;
    private JPanel rulesPanel;
    private List<AdvancedRule> rulesList;
    private JFrame mainFrame = new JFrame("Automatic Prediction Option");

    public AutomaticPredictionOptionDialog(ReverseSimulationComponent reverseSimulationComponent, String stateAndRoleName) {
        rulesList = new ArrayList<>();
        this.stateAndRoleName = stateAndRoleName;
        this.reverseSimulationComponent = reverseSimulationComponent;
        initComponents();
    }

    void initComponents() {
        mainFrame.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;

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
        mainPanel.add(scrollPane, gbc);
        gbc.gridy++;

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
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(mainFrame);
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
            JLabel label = new JLabel(advancedRule.rule + " " + (advancedRule.ascending? "ascending" : "descending"));
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

    private void createSimulationConfigurationDialog() {
        try {
            mainFrame.setVisible(false);
            mainFrame.dispose();
            new SimulationAutomaticConfigurationOptionDialog(reverseSimulationComponent, getStateAndRoleName(), rulesList);
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(this, "Please enter strategy.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Getter
    @Setter
    private class AddRuleDialog extends JDialog {

        private boolean successful = false;
        private JComboBox centralityRateDropdown;
        private JCheckBox descendingCheckbox;
        private AdvancedRule advancedRule;

        public AddRuleDialog(JFrame parent) {
            super(parent, "Add rule", true);
            JPanel mainPanel = new JPanel();

            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
                var centralityMethod = centralityRateDropdown.getSelectedItem().toString();
                advancedRule = new AdvancedRule(centralityMethod, 0, descendingCheckbox.isSelected());
                successful = true;
                setVisible(false);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Some error.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
