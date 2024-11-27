package components.simulationLogic;

import components.simulation.Simulation;

import javax.swing.*;

public class RunSimulationButton extends JButton {
    public SimulationComponent simulationComponent;
    public Simulation simulation;
    public Integer simulationsNumber;
    public Integer conductSteps;
    public Boolean visualization;
    public Integer delay;
}
