package components.simulationLogic.report;

import configLoader.ConfigLoader;
import it.unimi.dsi.fastutil.Pair;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import simulationModel.SimulationModel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import jxl.write.Number;

public class ReportGeneratorHelper {

    public static void generateReport(List<SimulationStepReport> report, String fileName, SimulationModel simulationModel) {
        JFrame graphFrame = createGraphFrame();
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));

        int roleCount = report.get(0).getRoleReports().size();
        List<JFreeChart> chartList = new ArrayList<>();

        XYSeriesCollection aggregatedDataset = new XYSeriesCollection();


        for (int i = 0; i < roleCount; i++) {
            String roleName = report.get(0).getRoleReports().get(i).getNodeRoleName();
            List<Pair<String, Map<Integer, Double>>> resultForRole = getValuesFromReport(report, i);

            var role = simulationModel.getNodeRoles().stream()
                    .filter(r -> r.getNodeRole().getName().equals(roleName))
                    .findFirst()
                    .orElse(null);

            if (role != null) {
                var nodeStates = role.getNodeStates();
                for (var state : nodeStates) {
                    String stateName = state.getNodeState().getName();
                    Color stateColor = state.getColor();
                    List<XYSeries> seriesForState = createSeriesForState(resultForRole, stateName);

                    for (XYSeries series : seriesForState) {
                        aggregatedDataset.addSeries(series);
                    }

                    JFreeChart chart = createChartForState(seriesForState, stateName, stateColor);
                    chartList.add(chart);
                    ChartPanel chartPanel = new ChartPanel(chart);
                    graphPanel.add(chartPanel);
                }
            }
        }

        JFreeChart aggregatedChart = ChartFactory.createXYLineChart(
                "States", "Time", "Value", aggregatedDataset,
                PlotOrientation.VERTICAL, true, true, false
        );

        chartList.add(aggregatedChart);

        XYPlot plot = aggregatedChart.getXYPlot();
        int seriesIndex = 0;
        for (Object seriesObj : aggregatedDataset.getSeries()) {
            if (seriesObj instanceof XYSeries) {
                XYSeries series = (XYSeries) seriesObj;
                Color stateColor = getStateColor(simulationModel, series.getKey().toString());
                plot.getRenderer().setSeriesPaint(seriesIndex, stateColor);
                seriesIndex++;
            }
        }


        ChartPanel aggregatedChartPanel = new ChartPanel(aggregatedChart);
        graphPanel.add(aggregatedChartPanel, 0);

        addControlButtons(graphPanel, chartList, report, fileName);
        graphFrame.add(graphPanel);
        graphFrame.pack();
        graphFrame.setVisible(true);
    }

    private static Color getStateColor(SimulationModel simulationModel, String stateName) {
        for (var role : simulationModel.getNodeRoles()) {
            for (var state : role.getNodeStates()) {
                if (state.getNodeState().getName().equals(stateName)) {
                    return state.getColor();
                }
            }
        }
        return Color.BLACK;
    }

    private static List<XYSeries> createSeriesForState(List<Pair<String, Map<Integer, Double>>> resultForRole, String stateName) {
        List<XYSeries> seriesList = new ArrayList<>();
        for (Pair<String, Map<Integer, Double>> pair : resultForRole) {
            if (pair.left().equals(stateName)) {
                XYSeries series = new XYSeries(stateName);
                for (Map.Entry<Integer, Double> entry : pair.right().entrySet()) {
                    series.add(entry.getKey(), entry.getValue());
                }
                seriesList.add(series);
            }
        }
        return seriesList;
    }

    private static JFreeChart createChartForState(List<XYSeries> seriesList, String stateName, Color color) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        for (XYSeries series : seriesList) {
            dataset.addSeries(series);
        }
        JFreeChart chart = ChartFactory.createXYLineChart(
                "State: " + stateName, "Time", "Value", dataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        XYPlot plot = chart.getXYPlot();
        plot.getRenderer().setSeriesPaint(0, color); // Ustawienie koloru dla serii
        return chart;
    }



    private static JFrame createGraphFrame() {
        JFrame graphFrame = new JFrame("Report Visualization");
        graphFrame.setSize(400, 400);
        graphFrame.setLocationRelativeTo(null);
        return graphFrame;
    }

    private static void addControlButtons(JPanel panel, List<JFreeChart> chartList, List<SimulationStepReport> report, String fileName) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveAsCSVButton = new JButton("Save as CSV");
        JButton saveAsXLSXButton = new JButton("Save as XLSX");
        JButton saveAsImageButton = new JButton("Save as IMAGE");

        saveAsCSVButton.addActionListener(e -> ExportReportHelper.generateCSV(report, fileName));
        saveAsXLSXButton.addActionListener(e -> ExportReportHelper.generateExcelJXL(report, fileName));
        saveAsImageButton.addActionListener(e -> ExportReportHelper.saveChartsAsImages(chartList, fileName));

        buttonPanel.add(saveAsCSVButton);
        buttonPanel.add(saveAsXLSXButton);
        buttonPanel.add(saveAsImageButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private static void addControlButtonsAvg(JPanel panel, List<JFreeChart> chartList, List<Pair<String, Map<Integer, Double>>> report, String fileName) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveAsCSVButton = new JButton("Save as CSV");
        JButton saveAsXLSXButton = new JButton("Save as XLSX");
        JButton saveAsImageButton = new JButton("Save as IMAGE");

        saveAsCSVButton.addActionListener(e -> ExportReportHelper.generateCSVAvg(report, fileName));
        saveAsXLSXButton.addActionListener(e -> ExportReportHelper.generateExcelJXLAvg(report, fileName));
        saveAsImageButton.addActionListener(e -> ExportReportHelper.saveChartsAsImages(chartList, fileName));

        buttonPanel.add(saveAsCSVButton);
        buttonPanel.add(saveAsXLSXButton);
        buttonPanel.add(saveAsImageButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    }


    public static List<Pair<String, Map<Integer, Double>>> getValuesFromReport(List<SimulationStepReport> report, int roleNumber) {
        List<SimulationStepReport.NodeRoleReport> correctRoleList = report.stream()
                .map(step -> step.getRoleReports().get(roleNumber))
                .collect(Collectors.toList());
        int statesNumber = correctRoleList.get(0).getStatesReport().size();

        List<Pair<String, Map<Integer, Double>>> listOfMapValues = new ArrayList<>();
        for (int i = 0; i < statesNumber; i++) {
            Map<Integer, Double> valuesMap = new HashMap<>();
            String stateName = correctRoleList.get(0).getStatesReport().get(i).getNodeStateName();

            for (int j = 0; j < correctRoleList.size(); j++) {
                valuesMap.put(j + 1, correctRoleList.get(j).getStatesReport().stream()
                        .filter(stateElement -> stateElement.getNodeStateName().equals(stateName))
                        .findFirst()
                        .map(SimulationStepReport.NodeRoleReport.StateElement::getNumberOfNodes)
                        .orElse(0.0));
            }
            listOfMapValues.add(Pair.of(stateName, valuesMap));
        }

        return listOfMapValues;
    }

    public static List<Pair<String, Map<Integer, Double>>> getSeriesReport(
            List<List<SimulationStepReport>> reports, String nodeRoleName, SimulationModel simulationModel) {
        Map<String, Map<Integer, List<Double>>> aggregatedData = new HashMap<>();

        for (List<SimulationStepReport> reportList : reports) {
            for (SimulationStepReport report : reportList) {
                for (SimulationStepReport.NodeRoleReport roleReport : report.getRoleReports()) {
                    if (roleReport.getNodeRoleName().equals(nodeRoleName)) {
                        for (SimulationStepReport.NodeRoleReport.StateElement stateElement : roleReport.getStatesReport()) {
                            String stateName = stateElement.getNodeStateName();
                            int step = report.getStep();
                            double value = stateElement.getNumberOfNodes();

                            aggregatedData.computeIfAbsent(stateName, k -> new HashMap<>())
                                    .computeIfAbsent(step, k -> new ArrayList<>())
                                    .add(value);
                        }
                    }
                }
            }
        }

        List<Pair<String, Map<Integer, Double>>> seriesReport = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, List<Double>>> stateEntry : aggregatedData.entrySet()) {
            String stateName = stateEntry.getKey();
            Map<Integer, Double> averagedSeries = new HashMap<>();

            for (Map.Entry<Integer, List<Double>> stepEntry : stateEntry.getValue().entrySet()) {
                int step = stepEntry.getKey();
                List<Double> values = stepEntry.getValue();
                double average = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                averagedSeries.put(step, average);
            }

            seriesReport.add(Pair.of(stateName, averagedSeries));
        }

        return seriesReport;
    }

    public static void generateSeriesReport(List<List<SimulationStepReport>> reports, String fileName, SimulationModel simulationModel) {
        JFrame graphFrame = createGraphFrame();
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));

        List<Pair<String, Map<Integer, Double>>> averagedSeries = null;

        int roleCount = reports.get(0).get(0).getRoleReports().size();
        List<JFreeChart> chartList = new ArrayList<>();

        XYSeriesCollection combinedDataset = new XYSeriesCollection();

        for (int i = 0; i < roleCount; i++) {
            String roleName = reports.get(0).get(0).getRoleReports().get(i).getNodeRoleName();
            averagedSeries = getSeriesReport(reports, roleName, simulationModel);

            for (Pair<String, Map<Integer, Double>> series : averagedSeries) {
                String stateName = series.left();
                Map<Integer, Double> data = series.right();

                XYSeries xySeries = new XYSeries(stateName);
                for (Map.Entry<Integer, Double> entry : data.entrySet()) {
                    xySeries.add(entry.getKey(), entry.getValue());
                }
                combinedDataset.addSeries(xySeries);

                XYSeriesCollection dataset = new XYSeriesCollection();
                dataset.addSeries(xySeries);

                JFreeChart chart = ChartFactory.createXYLineChart(
                        "State: " + stateName, "Time", "Value", dataset,
                        PlotOrientation.VERTICAL, true, true, false
                );

                XYPlot plot = chart.getXYPlot();
                Color stateColor = getStateColor(simulationModel, stateName);
                plot.getRenderer().setSeriesPaint(0, stateColor);

                chartList.add(chart);
                ChartPanel chartPanel = new ChartPanel(chart);
                graphPanel.add(chartPanel);
            }
        }

        JFreeChart combinedChart = ChartFactory.createXYLineChart(
                "States", "Time", "Value", combinedDataset,
                PlotOrientation.VERTICAL, true, true, false
        );
        XYPlot combinedPlot = combinedChart.getXYPlot();
        for (int i = 0; i < combinedDataset.getSeriesCount(); i++) {
            String stateName = (String) combinedDataset.getSeriesKey(i);
            Color stateColor = getStateColor(simulationModel, stateName);
            combinedPlot.getRenderer().setSeriesPaint(i, stateColor);
        }
        ChartPanel combinedChartPanel = new ChartPanel(combinedChart);
        graphPanel.add(combinedChartPanel, 0);

        chartList.add(combinedChart);

        if(averagedSeries != null)
            addControlButtonsAvg(graphPanel, chartList, averagedSeries, fileName);

        graphFrame.add(graphPanel);
        graphFrame.pack();
        graphFrame.setVisible(true);
    }
}
