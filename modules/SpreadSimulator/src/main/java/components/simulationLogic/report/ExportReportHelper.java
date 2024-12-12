package components.simulationLogic.report;

import configLoader.ConfigLoader;
import it.unimi.dsi.fastutil.Pair;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExportReportHelper {
    public static void generateCSV(List<SimulationStepReport> reports, String filename) {
        Map<String, String> csvData = prepareHeaders(reports);

        File directory = createDirectory(filename);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directory + "/" + filename + ".csv"))) {
            writer.write("step," + String.join(",", csvData.keySet()));
            writer.newLine();

            for (SimulationStepReport report : reports) {
                Map<String, String> rowData = new LinkedHashMap<>(csvData);
                populateRowData(report, rowData);
                writer.write(report.getStep() + "," + String.join(",", rowData.values()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateCSVAvg(List<Pair<String, Map<Integer, Double>>> data, String filename) {
        try {
            File directory = createDirectory(filename);
            File file = new File(directory + "/" + filename + ".csv");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Step");

                for (Pair<String, Map<Integer, Double>> entry : data) {
                    writer.write("," + entry.left());
                }
                writer.newLine();

                if (!data.isEmpty()) {
                    Map<Integer, Double> firstMap = data.get(0).right();
                    for (Integer step : firstMap.keySet()) {
                        writer.write(step.toString());

                        for (Pair<String, Map<Integer, Double>> entry : data) {
                            writer.write("," + entry.right().getOrDefault(step, 0.0));
                        }
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateExcelJXLAvg(List<Pair<String, Map<Integer, Double>>> data, String filename) {
        try {
            File directory = createDirectory(filename);
            File file = new File(directory + "/" + filename + ".xls");
            WritableWorkbook workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("Data", 0);

            sheet.addCell(new Label(0, 0, "Step"));
            for (int i = 0; i < data.size(); i++) {
                sheet.addCell(new Label(i + 1, 0, data.get(i).left()));
            }

            if (!data.isEmpty()) {
                Map<Integer, Double> firstMap = data.get(0).right();
                int row = 1;
                for (Integer step : firstMap.keySet()) {
                    sheet.addCell(new Number(0, row, step));

                    for (int col = 0; col < data.size(); col++) {
                        Pair<String, Map<Integer, Double>> entry = data.get(col);
                        Double value = entry.right().getOrDefault(step, 0.0);
                        sheet.addCell(new Number(col + 1, row, value));
                    }
                    row++;
                }
            }

            workbook.write();
            workbook.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateExcelJXL(List<SimulationStepReport> reports, String filename) {
        try {
            File directory = createDirectory(filename);
            WritableWorkbook workbook = Workbook.createWorkbook(new File(directory + "/" + filename + ".xls"));
            WritableSheet sheet = workbook.createSheet("Simulation Report", 0);

            Map<String, String> csvData = prepareHeaders(reports);

            writeExcelHeaders(sheet, csvData);
            writeExcelData(reports, sheet, csvData);

            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveChartsAsImages(List<JFreeChart> charts, String fileName) {
        try {
            File directory = createDirectory(fileName);
            for (JFreeChart chart : charts) {
                ChartUtilities.saveChartAsPNG(new File(directory + "/chart_" + (charts.indexOf(chart) + 1) + ".png"), chart, 1200, 800);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static Map<String, String> prepareHeaders(List<SimulationStepReport> reports) {
        Map<String, String> csvData = new LinkedHashMap<>();
        for (SimulationStepReport report : reports) {
            for (SimulationStepReport.NodeRoleReport roleReport : report.getRoleReports()) {
                for (SimulationStepReport.NodeRoleReport.StateElement stateElement : roleReport.getStatesReport()) {
                    String header = roleReport.getNodeRoleName() + "-" + stateElement.getNodeStateName();
                    csvData.putIfAbsent(header + "_num", "");
                    csvData.putIfAbsent(header + "_coverage", "");
                }
            }
        }
        return csvData;
    }

    private static File createDirectory(String filename) {
        File directory = new File(ConfigLoader.folderReports + filename);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private static void populateRowData(SimulationStepReport report, Map<String, String> rowData) {
        for (SimulationStepReport.NodeRoleReport roleReport : report.getRoleReports()) {
            for (SimulationStepReport.NodeRoleReport.StateElement stateElement : roleReport.getStatesReport()) {
                String headerBase = roleReport.getNodeRoleName() + "-" + stateElement.getNodeStateName();
                rowData.put(headerBase + "_num", stateElement.getNumberOfNodes().toString());
                rowData.put(headerBase + "_coverage", stateElement.getCoverage().toString());
            }
        }
    }

    private static void writeExcelHeaders(WritableSheet sheet, Map<String, String> csvData) throws Exception {
        Label label = new Label(0, 0, "step");
        sheet.addCell(label);
        int columnIndex = 1;
        for (String header : csvData.keySet()) {
            label = new Label(columnIndex, 0, header);
            sheet.addCell(label);
            columnIndex++;
        }
    }

    private static void writeExcelData(List<SimulationStepReport> reports, WritableSheet sheet, Map<String, String> csvData) throws Exception {
        int rowIndex = 1;
        for (SimulationStepReport report : reports) {
            Label label = new Label(0, rowIndex, report.getStep().toString());
            sheet.addCell(label);

            Map<String, String> rowData = new LinkedHashMap<>(csvData);
            populateRowData(report, rowData);

            int columnIndex = 1;
            for (String value : rowData.values()) {
                label = new Label(columnIndex, rowIndex, value);
                sheet.addCell(label);
                columnIndex++;
            }
            rowIndex++;
        }
    }
}
