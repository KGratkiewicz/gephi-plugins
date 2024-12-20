package components.simulationBuilder;

import simulationModel.SimulationModel;
import simulationModel.interaction.*;
import simulationModel.node.NodeRoleDecorator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultStateDropdown {
    public JComboBox<String> generate(SimulationBuilderComponent simulationBuilderComponent, NodeRoleDecorator nodeRoleDecorator) {
        var nodeStates = nodeRoleDecorator.getNodeStates()
                .stream()
                .map(x -> x.getNodeState())
                .collect(Collectors.toList());

        List<String> defaultStateOptions = nodeStates.stream()
                .map(state -> state.getName().toString())
                .collect(Collectors.toList());

        if (nodeStates.stream()
                .map(x -> x.getName().toString())
                .noneMatch(x -> x.equals(nodeRoleDecorator.getDefaultStateName()))) {
            nodeRoleDecorator.setDefaultStateName(defaultStateOptions.get(0));
        } else {
            var selectedOption = defaultStateOptions.stream()
                    .filter(x -> x.equals(nodeRoleDecorator.getDefaultStateName()))
                    .findFirst();

            if (selectedOption.isPresent()) {
                int selectedIndex = defaultStateOptions.indexOf(selectedOption.get());
                if (selectedIndex > 0) {
                    String firstElement = defaultStateOptions.get(0);
                    defaultStateOptions.set(0, selectedOption.get());
                    defaultStateOptions.set(selectedIndex, firstElement);
                }
            }
        }

        var combobox = new JComboBox<>(defaultStateOptions.toArray(new String[0]));
        combobox.addActionListener(new InteractionDropdownListener(combobox, nodeRoleDecorator, simulationBuilderComponent));

        return combobox;
    }

    private class InteractionDropdownListener implements ActionListener {
        private final SimulationBuilderComponent simulationBuilderComponent;
        private final JComboBox rootState;
        private final SimulationModel simulationModel;
        private NodeRoleDecorator nodeRoleDecorator;
        public InteractionDropdownListener(JComboBox rootState, NodeRoleDecorator nodeRoleDecorator, SimulationBuilderComponent simulationBuilderComponent) {
            this.nodeRoleDecorator = nodeRoleDecorator;
            this.rootState = rootState;
            this.simulationModel = simulationBuilderComponent.getSimulationModel();
            var allInteraction =  new AllInteraction();
            allInteraction.setInteractionType(InteractionType.All);
            this.simulationModel.setInteraction(allInteraction);
            this.simulationBuilderComponent = simulationBuilderComponent;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            var rootStateName = rootState.getSelectedItem().toString();
            nodeRoleDecorator.setDefaultStateName(rootStateName);
            }
        }


    private abstract class CustomListener implements DocumentListener {
        protected final SimulationModel simulationModel;
        protected final JTextField percentageField;
        public CustomListener(SimulationModel simulationModel, JTextField percentageField) {
            this.simulationModel = simulationModel;
            this.percentageField = percentageField;
        }
        protected abstract void Change();
        @Override
        public void insertUpdate(DocumentEvent e) {
            Change();
        }
        @Override
        public void removeUpdate(DocumentEvent e) {
            Change();
        }
        @Override
        public void changedUpdate(DocumentEvent e) {
            Change();
        }
    }

    private class RelativeNodesInteractionListener extends CustomListener {
        public RelativeNodesInteractionListener(SimulationModel simulationModel, JTextField percentageField) {
            super(simulationModel, percentageField);
        }
        @Override
        protected void Change() {
            var percentage = Double.parseDouble(percentageField.getText());
            var interaction = (RelativeNodesInteraction) simulationModel.getInteraction();
            interaction.setPercentage(percentage);
            simulationModel.setInteraction(interaction);
        }
    }

    private class RelativeEdgesInteractionListener extends CustomListener {
        public RelativeEdgesInteractionListener(SimulationModel simulationModel, JTextField percentageField) {
            super(simulationModel, percentageField);
        }
        @Override
        protected void Change() {
            var percentage = Double.parseDouble(percentageField.getText());
            var interaction = (RelativeEdgesInteraction) simulationModel.getInteraction();
            interaction.setPercentage(percentage);
            simulationModel.setInteraction(interaction);
        }
    }

    private class RelativeFreeNodesInteractionListener extends CustomListener {
        public RelativeFreeNodesInteractionListener(SimulationModel simulationModel, JTextField percentageField) {
            super(simulationModel, percentageField);
        }
        @Override
        protected void Change() {
            var number = Integer.parseInt(percentageField.getText());
            var interaction = (RelativeFreeNodesInteraction) simulationModel.getInteraction();
            interaction.setNumber(number);
            simulationModel.setInteraction(interaction);
        }
    }

    private class RelativeFreeEdgesInteractionListener extends CustomListener {
        public RelativeFreeEdgesInteractionListener(SimulationModel simulationModel, JTextField percentageField) {
            super(simulationModel, percentageField);
        }
        @Override
        protected void Change() {
            var number = Integer.parseInt(percentageField.getText());
            var interaction = (RelativeFreeEdgesInteraction) simulationModel.getInteraction();
            interaction.setNumber(number);
            simulationModel.setInteraction(interaction);
        }
    }
}
