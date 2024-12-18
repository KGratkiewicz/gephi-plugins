package components.simulationBuilder;

import simulationModel.interaction.*;
import simulationModel.SimulationModel;
import org.openide.util.NotImplementedException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InteractionDropdown {

    public JComboBox<String> generate(SimulationBuilderComponent simulationBuilderComponent) {
        String[] interactionOptions = {
                "All", "RelativeNodes", "RelativeFreeNodes", "RelativeEdges", "RelativeFreeEdges", "WeighedCommonNeighbours", "CommunityPressure"
        };

        JComboBox<String> combobox = new JComboBox<>(interactionOptions);
        InteractionDropdownHandler handler = new InteractionDropdownHandler(combobox, simulationBuilderComponent);
        combobox.addActionListener(handler);

        return combobox;
    }

    private class InteractionDropdownHandler implements ActionListener {
        private final JComboBox<String> interactionComboBox;
        private final SimulationBuilderComponent simulationBuilderComponent;
        private final SimulationModel simulationModel;
        private final JLabel label = new JLabel();
        private final JTextField numberField = new JTextField(10);
        private final JTextField percentageField = new JTextField(10);

        public InteractionDropdownHandler(JComboBox<String> interactionComboBox, SimulationBuilderComponent simulationBuilderComponent) {
            this.interactionComboBox = interactionComboBox;
            this.simulationBuilderComponent = simulationBuilderComponent;
            this.simulationModel = simulationBuilderComponent.getSimulationModel();
            setupComponents();
            initializeDefaultInteraction();
        }

        private void setupComponents() {
            numberField.getDocument().addDocumentListener(new FieldChangeListener("number"));
            percentageField.getDocument().addDocumentListener(new FieldChangeListener("percentage"));
        }

        private void initializeDefaultInteraction() {
            setInteraction(new AllInteraction(), InteractionType.All);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String interactionType = (String) interactionComboBox.getSelectedItem();
            assert interactionType != null;
            switch (interactionType) {
                case "All":
                    setInteraction(new AllInteraction(), InteractionType.All);
                    resetUI();
                    break;
                case "RelativeNodes":
                    setInteraction(new RelativeNodesInteraction(), InteractionType.RelativeNodes);
                    updateUI("Percentage of nodes:", percentageField);
                    break;
                case "RelativeFreeNodes":
                    setInteraction(new RelativeFreeNodesInteraction(), InteractionType.RelativeFreeNodes);
                    updateUI("Number of nodes:", numberField);
                    break;
                case "RelativeEdges":
                    setInteraction(new RelativeEdgesInteraction(), InteractionType.RelativeEdges);
                    updateUI("Percentage of edges:", percentageField);
                    break;
                case "RelativeFreeEdges":
                    setInteraction(new RelativeFreeEdgesInteraction(), InteractionType.RelativeFreeEdges);
                    updateUI("Number of edges:", numberField);
                    break;
                case "WeighedCommonNeighbours":
                    setInteraction(new WeightedCommonNeigboursInteraction(), InteractionType.WeighedCommonNeighbors);
                    resetUI();
                    break;
                case "CommunityPressure":
                    setInteraction(new CommunityPressureInteraction(), InteractionType.CommunityPressureInteraction);
                    resetUI();
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Not implemented method yet.");
                    throw new NotImplementedException();
            }
            simulationBuilderComponent.revalidate();
            simulationBuilderComponent.repaint();
        }

        private void setInteraction(Interaction interaction, InteractionType type) {
            interaction.setInteractionType(type);
            simulationModel.setInteraction(interaction);
        }

        private void resetUI() {
            simulationBuilderComponent.remove(label);
            simulationBuilderComponent.remove(numberField);
            simulationBuilderComponent.remove(percentageField);
        }

        private void updateUI(String labelText, JTextField fieldToShow) {
            resetUI();
            label.setText(labelText);
            simulationBuilderComponent.add(label);
            simulationBuilderComponent.add(fieldToShow);
        }

        private class FieldChangeListener implements DocumentListener {
            private final String fieldType;

            public FieldChangeListener(String fieldType) {
                this.fieldType = fieldType;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateInteraction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateInteraction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateInteraction();
            }

            private void updateInteraction() {
                try {
                    Interaction interaction = simulationModel.getInteraction();
                    if ("percentage".equals(fieldType)) {
                        double percentage = Double.parseDouble(percentageField.getText());
                        if (interaction instanceof PercentageBasedInteraction) {
                            ((PercentageBasedInteraction) interaction).setPercentage(percentage);
                        }
                    } else if ("number".equals(fieldType)) {
                        int number = Integer.parseInt(numberField.getText());
                        if (interaction instanceof NumberBasedInteraction) {
                            ((NumberBasedInteraction) interaction).setNumber(number);
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid input
                }
            }
        }
    }
}
