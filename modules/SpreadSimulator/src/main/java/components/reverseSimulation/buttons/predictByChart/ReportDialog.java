package components.reverseSimulation.buttons.predictByChart;

import components.simulationLogic.report.SimulationStepReport;
import configLoader.ConfigLoader;
import it.unimi.dsi.fastutil.Pair;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.statistics.plugin.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Lookup;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static components.simulationLogic.report.ReportGeneratorHelper.getValuesFromReport;

public class ReportDialog {

    List<SimulationStepReport> actualSimulationReport;
    List<List<SimulationStepReport>> simulatedListOfSimulationReports;
    List<Pair<Integer, Double>> diffList;
    int intervalStart;
    int intervalEnd;
    String stateAndRoleName;

    public ReportDialog(Pair<List<SimulationStepReport>, List<List<SimulationStepReport>>> pair, int predictionStartInterval, int predictionEndInterval, String stateAndRoleName, List<Pair<Integer, Double>> diffList) {
        this.actualSimulationReport = pair.first();
        this.simulatedListOfSimulationReports = pair.second();
        this.intervalStart = predictionStartInterval;
        this.intervalEnd = predictionEndInterval;
        this.stateAndRoleName = stateAndRoleName;
        this.diffList = diffList;
        initComponents();
    }

    public ReportDialog(Pair<List<SimulationStepReport>, List<List<SimulationStepReport>>> pair, int predictionStartInterval, int predictionEndInterval, String stateAndRoleName) {
        this.actualSimulationReport = pair.first();
        this.simulatedListOfSimulationReports = pair.second();
        this.intervalStart = predictionStartInterval;
        this.intervalEnd = predictionEndInterval;
        this.stateAndRoleName = stateAndRoleName;
        initComponents();
    }

    private void initComponents() {
        JFrame mainFrame = new JFrame("Frame");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        getFullChartPanelFromStepsReport(actualSimulationReport).forEach(mainPanel::add);

        mainPanel.add(getLabelFromStringAndSize(
                22,
                true,
                "Examined node state: " + stateAndRoleName.split(":")[1] + " from: " + intervalStart + " to: " + intervalEnd));
        getChartPanelFromStepsReport(actualSimulationReport, intervalStart, intervalEnd, stateAndRoleName).forEach(mainPanel::add);

        mainPanel.add(Box.createVerticalStrut(100));

        if (diffList != null) {
            var bestSolutionDiff = diffList.get(diffList.size() - 1);
            mainPanel.add(getLabelFromStringAndSize(
                    22,
                    true,
                    "Found best solution for strategy with: " + bestSolutionDiff.first() + " nodes" + " and difference: " + String.format("%.2f", bestSolutionDiff.second())));

            mainPanel.add(getLabelFromStringAndSize(
                    22,
                    true,
                    "Difference for each iteration "));

            diffList.subList(0, diffList.size() - 2).forEach(e ->
                    mainPanel.add(getLabelFromStringAndSize(
                            16,
                            true,
                            "Number of initial nodes: " + e.first() + " difference: " + String.format("%.2f", e.second())))
            );
        }

        mainPanel.add(getLabelFromStringAndSize(
                22,
                true,
                "Predicted avg value compared to actual"));
        getChartPanelComparingFromAvg(actualSimulationReport, simulatedListOfSimulationReports, intervalStart, intervalEnd, stateAndRoleName).forEach(mainPanel::add);

        mainPanel.add(getLabelFromStringAndSize(
                22,
                true,
                "Predicted best simulation compared to actual"));
        getChartPanelComparingFromBestSimulation(actualSimulationReport, simulatedListOfSimulationReports, intervalStart, intervalEnd, stateAndRoleName).forEach(mainPanel::add);

        JButton showOnGraphButton = new JButton("Show On Grpah");
        showOnGraphButton.addActionListener(e -> showOnGraph(mainFrame));
        mainPanel.add(showOnGraphButton);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainFrame.add(scrollPane, BorderLayout.CENTER);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(mainFrame);
        mainFrame.setVisible(true);
    }

    private void showOnGraph(JFrame mainFrame) {
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        var nodes = List.of(graph.getNodes().toArray());
        String stateName = stateAndRoleName.split(":")[1];

        nodes.forEach(node -> {
            if (node.getAttribute(ConfigLoader.colNameRootState).equals(stateName)
                    && node.getAttribute(ConfigLoader.colNameInitialNodeState).equals(stateName)) {
                node.setColor(Color.green);
            } else if (node.getAttribute(ConfigLoader.colNameRootState).equals(stateName)) {
                node.setColor(Color.red);
            } else if (node.getAttribute(ConfigLoader.colNameInitialNodeState).equals(stateName)) {
                node.setColor(Color.yellow);
            } else {
                node.setColor(Color.GRAY);
            }
        });
        mainFrame.setVisible(false);
        mainFrame.dispose();
    }

    private List<ChartPanel> getChartPanelComparingFromBestSimulation(List<SimulationStepReport> reportToCompare, List<List<SimulationStepReport>> predictedReports, int intervalStart, int intervalEnd, String stateAndRoleName) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        List<JFreeChart> chartList = new ArrayList<>(List.of());
        String nodeStateName = stateAndRoleName.split(":")[0];
        String nodeRoleName = stateAndRoleName.split(":")[1];

        Map<Integer, Double> actualMapValues = new HashMap<>();
        reportToCompare.stream()
                .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                        .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                        .forEach(stepReport -> stepReport.getStatesReport().stream()
                                .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                .forEach(stateElement -> actualMapValues.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));

        XYSeries actualSeries = new XYSeries("actual " + nodeRoleName + " value");
        for (Integer step : actualMapValues.keySet()) {
            actualSeries.add(step, actualMapValues.get(step));
        }
        dataset.addSeries(actualSeries);

        List<Map<Integer, Double>> predictedMapValues = new ArrayList<>();
        predictedReports.stream()
                .forEach(oneReport -> {
                    Map<Integer, Double> newMap = new HashMap<>();
                    oneReport.stream()
                            .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                            .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                                    .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                                    .forEach(stepReport -> stepReport.getStatesReport().stream()
                                            .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                            .forEach(stateElement -> newMap.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));
                    predictedMapValues.add(newMap);
                });

        Map<Integer, Double> bestSimulationValues = getBestValuesComparing(predictedMapValues, actualMapValues);
        XYSeries bestSeries = new XYSeries("best simulation " + nodeRoleName + " value");
        for (Integer step : bestSimulationValues.keySet()) {
            bestSeries.add(step, bestSimulationValues.get(step));
        }
        dataset.addSeries(bestSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chartList.add(chart);

        var listOfPanels = chartList.stream().map(ChartPanel::new).collect(Collectors.toList());
        listOfPanels.forEach(panel -> panel.setPreferredSize(new Dimension(800, 500)));

        return listOfPanels;
    }

    private Map<Integer, Double> getBestValuesComparing(List<Map<Integer, Double>> predictedMapValues, Map<Integer, Double> actualMapValues) {
        Double lowestDiff = (double) -1;
        Map<Integer, Double> bestSimulation = null;
        for (Map<Integer, Double> onePredict : predictedMapValues) {
            double diff = 0;
            for (Integer step : onePredict.keySet()) {
                diff += Math.abs(onePredict.get(step) - actualMapValues.get(step));
            }
            if (lowestDiff == -1 || diff < lowestDiff) {
                lowestDiff = diff;
                bestSimulation = onePredict;
            }
        }
        return bestSimulation;
    }

    private List<ChartPanel> getChartPanelComparingFromAvg(List<SimulationStepReport> reportToCompare, List<List<SimulationStepReport>> predictedReports, int intervalStart, int intervalEnd, String stateAndRoleName) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        List<JFreeChart> chartList = new ArrayList<>(List.of());
        String nodeStateName = stateAndRoleName.split(":")[0];
        String nodeRoleName = stateAndRoleName.split(":")[1];

        Map<Integer, Double> actualMapValues = new HashMap<>();
        reportToCompare.stream()
                .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                        .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                        .forEach(stepReport -> stepReport.getStatesReport().stream()
                                .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                .forEach(stateElement -> actualMapValues.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));

        XYSeries actualSeries = new XYSeries("actual " + nodeRoleName + " value");
        for (Integer step : actualMapValues.keySet()) {
            actualSeries.add(step, actualMapValues.get(step));
        }
        dataset.addSeries(actualSeries);

        List<Map<Integer, Double>> predictedMapValues = new ArrayList<>();
        predictedReports.stream()
                .forEach(oneReport -> {
                    Map<Integer, Double> newMap = new HashMap<>();
                    oneReport.stream()
                            .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                            .forEach(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                                    .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                                    .forEach(stepReport -> stepReport.getStatesReport().stream()
                                            .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                            .forEach(stateElement -> newMap.put(simulationStepReport.getStep(), stateElement.getNumberOfNodes()))));
                    predictedMapValues.add(newMap);
                });

        Map<Integer, Double> predictedAvgValues = getAvgValues(predictedMapValues);
        XYSeries avgValuesSeries = new XYSeries("predicted avg " + nodeRoleName + " value");
        for (Integer step : predictedAvgValues.keySet()) {
            avgValuesSeries.add(step, predictedAvgValues.get(step));
        }
        dataset.addSeries(avgValuesSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chartList.add(chart);

        var listOfPanels = chartList.stream().map(ChartPanel::new).collect(Collectors.toList());
        listOfPanels.forEach(panel -> panel.setPreferredSize(new Dimension(800, 500)));

        return listOfPanels;
    }

    private Map<Integer, Double> getAvgValues(List<Map<Integer, Double>> listOfFilteredValueseFromPredictedReports) {
        return listOfFilteredValueseFromPredictedReports.stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.averagingDouble(Map.Entry::getValue)));
    }

    private JLabel getLabelFromStringAndSize(int size, boolean bold, String text) {
        JLabel label = new JLabel(text);
        var font = label.getFont();
        label.setFont(font.deriveFont(font.getStyle() | (bold ? Font.BOLD : Font.PLAIN), size));
        return label;
    }

    private List<ChartPanel> getFullChartPanelFromStepsReport(List<SimulationStepReport> report) {
        int roleCount = report.get(0).getRoleReports().size();
        List<JFreeChart> chartList = new ArrayList<>(List.of());
        for (int i = 0; i < roleCount; i++) {
            List<Pair<String, Map<Integer, Double>>> resultForRole = getValuesFromReport(report, i);

            List<XYSeries> listOfSeries = resultForRole.stream().map(pair -> ChartUtils.createXYSeries(pair.value(), pair.key())).collect(Collectors.toList());
            XYSeriesCollection dataset = new XYSeriesCollection();
            for (XYSeries series : listOfSeries) {
                dataset.addSeries(series);
            }

            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Acctual graph",
                    "Time",
                    "Count",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            chartList.add(chart);
        }

        var listOfPanels = chartList.stream().map(ChartPanel::new).collect(Collectors.toList());
        listOfPanels.forEach(panel -> panel.setPreferredSize(new Dimension(800, 500)));

        return listOfPanels;
    }

    private List<ChartPanel> getChartPanelFromStepsReport(List<SimulationStepReport> report, int intervalStart, int intervalEnd, String stateAndRoleName) {
        String nodeStateName = stateAndRoleName.split(":")[0];
        String nodeRoleName = stateAndRoleName.split(":")[1];

        List<Map<Integer, Double>> filteredValuesFromReports = report.stream()
                .filter(simulationStepReport -> simulationStepReport.getStep() >= intervalStart && simulationStepReport.getStep() <= intervalEnd)
                .flatMap(simulationStepReport -> simulationStepReport.getRoleReports().stream()
                        .filter(stepReport -> stepReport.getNodeRoleName().equals(nodeStateName))
                        .flatMap(stepReport -> stepReport.getStatesReport().stream()
                                .filter(stateElement -> stateElement.getNodeStateName().equals(nodeRoleName))
                                .map(stateElement -> Map.of(simulationStepReport.getStep(), stateElement.getNumberOfNodes())))
                ).collect(Collectors.toList());

        List<JFreeChart> chartList = new ArrayList<>(List.of());

        XYSeries seriesForState = new XYSeries(nodeStateName);
        filteredValuesFromReports.forEach(entrySet ->
                entrySet.forEach(seriesForState::add)
        );

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(seriesForState);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Time",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        chartList.add(chart);

        var listOfPanels = chartList.stream().map(ChartPanel::new).collect(Collectors.toList());
        listOfPanels.forEach(panel -> panel.setPreferredSize(new Dimension(800, 500)));

        return listOfPanels;
    }
}