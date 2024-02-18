package components.reverseSimulation;

import components.simulationLogic.SimulationComponent;
import it.unimi.dsi.fastutil.Pair;
import org.gephi.statistics.plugin.ChartUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import simulationModel.node.NodeRoleDecorator;
import simulationModel.node.NodeStateDecorator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static components.simulationLogic.report.ReportGeneratorHelper.getValuesFromReport;

public class SimulationChart {

    public ReverseSimulationComponent component;

    public SimulationChart(ReverseSimulationComponent component){
        this.component = component;
    }

    public JPanel generate(){
        JPanel graphPanel = new JPanel();

//        graphPanel.setLayout(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.weighty = 1.0;
//        gbc.gridy++;

        var report = SimulationComponent.getInstance().getCurrentSimulation().getReport();
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.PAGE_AXIS));

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
                    report.get(0).getRoleReports().get(i).getNodeRoleName(),
                    "Time",
                    "Count",
                    dataset,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            chartList.add(chart);

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(250, 100));
            graphPanel.add(chartPanel);
        }

//        panel.add(new JSeparator(), gbc);
//        JScrollPane scrollPane = new JScrollPane(panel);
//        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//        return scrollPane;
        return graphPanel;
    }
}
