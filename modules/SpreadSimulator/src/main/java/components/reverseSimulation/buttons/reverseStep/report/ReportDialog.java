package components.reverseSimulation.buttons.reverseStep.report;

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
public class ReportDialog extends JDialog {

    private String examinedStateAndRole;
    private JComboBox<String> rolesChoseFromList;
    private int option;
    private boolean successful = false;

    public ReportDialog(Frame parent, ReverseSimulationComponent reverseSimulationComponent, String name) {
        super(parent, name, true);
//        TODO pobawić się z layout jak w visibility
        setLayout(new GridLayout(3, 2));

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
        add(new JLabel("Choose a value:"));
        add(rolesChoseFromList);

        JButton singleSimulationReportButton = new JButton("Single Simulation Report");
        singleSimulationReportButton.addActionListener(e -> singleSimulationAction());
        add(singleSimulationReportButton);

        JButton seriesSimulationReportButton = new JButton("Series Simulation Report");
        seriesSimulationReportButton.addActionListener(e -> seriesSimulationAction());
        add(seriesSimulationReportButton);

        pack();
        setLocationRelativeTo(parent);
    }

    private void singleSimulationAction() {
        this.setOption(1);
        onOk();
    }

    private void seriesSimulationAction() {
        this.setOption(2);
        onOk();
    }

    private void onOk() {
        try {
            examinedStateAndRole = rolesChoseFromList.getSelectedItem().toString();
            successful = true;
            setVisible(false);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Some error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
