package components.simulationBuilder;

import simulationModel.SimulationModel;
import simulationModel.interaction.*;
import simulationModel.node.NodeRoleDecorator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.stream.Collectors;

public class DefaultStateDropdown {
    public JComboBox generate(SimulationBuilderComponent simulationBuilderComponent, NodeRoleDecorator nodeRoleDecorator){
        var nodeStates = nodeRoleDecorator.getNodeStates().stream().map(x -> x.getNodeState()).collect(Collectors.toList());
        var defaultStateOptions = nodeStates.stream().map(state -> state.getName().toString()).toArray();
        nodeRoleDecorator.setDefaultStateName(defaultStateOptions[0].toString());
        var combobox = new JComboBox<>(defaultStateOptions);
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
