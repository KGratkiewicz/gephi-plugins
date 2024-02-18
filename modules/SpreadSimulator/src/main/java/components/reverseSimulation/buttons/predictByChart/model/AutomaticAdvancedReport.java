package components.reverseSimulation.buttons.predictByChart.model;

import components.simulationLogic.report.SimulationStepReport;
import it.unimi.dsi.fastutil.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AutomaticAdvancedReport {
    private String strategyName;
    private boolean ascending;
    private List<Pair<Integer, Double>> reportList;
    private List<List<SimulationStepReport>> simulationStepReport;
}
