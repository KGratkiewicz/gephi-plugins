package components.simulation;

import components.simulationLogic.RunSimulationButton;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.utils.progress.ProgressTicketProvider;
import org.openide.util.Lookup;

import javax.swing.*;

public class SimulationRunner implements LongTask {
    private final RunSimulationButton simulationButton;
    private ProgressTicket progressTicket;
    private boolean isCanceled;

    public SimulationRunner(RunSimulationButton simulationButton) {
        this.simulationButton = simulationButton;
    }

    private ProgressTicket createProgressTicket(String taskName) {
        ProgressTicketProvider provider = Lookup.getDefault().lookup(ProgressTicketProvider.class);
        if (provider != null) {
            return provider.createTicket(taskName, null);
        }
        return null;
    }

    public  void runSimulation() {
        progressTicket = createProgressTicket("Simulation progress");
        Progress.finish(progressTicket);
        Progress.start(progressTicket,   simulationButton.conductSteps);
        simulate();
        Progress.finish(progressTicket);
    }

    public  void runSimulationSeries() {
        progressTicket = createProgressTicket("Simulation progress");
        Progress.finish(progressTicket);
        Progress.start(progressTicket,   simulationButton.conductSteps * simulationButton.simulationsNumber);
        for(int i =0 ; i < simulationButton.simulationsNumber -1; i++) {
            simulate();
            simulationButton.simulation = simulationButton.simulationComponent.NewSeries(simulationButton.simulation);
        }
        simulate();
        Progress.finish(progressTicket);
    }

    public void simulate(){
        if( simulationButton.visualization){
            for (int i = 0; i <  simulationButton.conductSteps; i++) {
                if (isCanceled) {
                    break;
                }
                simulationButton.simulation.Step();
                Progress.progress(progressTicket);
                simulationButton.simulationComponent.repaint();
                simulationButton.simulationComponent.revalidate();
                try {
                    Thread.sleep( simulationButton.delay);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
        else{
            for (int i = 0; i <  simulationButton.conductSteps; i++) {
                if (isCanceled) {
                    break;
                }
                simulationButton.simulation.Step();
                Progress.progress(progressTicket);
            }
        }
    }

    @Override
    public boolean cancel() {
        isCanceled = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {

    }
}
