package components.simulationLogic;

import javax.swing.*;

public class RefreshButton extends JButton {
    private final SimulationComponent simulationComponent;

    public RefreshButton(SimulationComponent simulationComponent) {
        this.setText("Refresh view");
        this.simulationComponent = simulationComponent;
        this.addActionListener(e -> Refresh());
    }
    public void Refresh() {
        simulationComponent.initComponents();
        simulationComponent.revalidate();
        simulationComponent.repaint();
    }
}
