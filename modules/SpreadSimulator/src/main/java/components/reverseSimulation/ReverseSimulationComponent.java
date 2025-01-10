package components.reverseSimulation;

import components.reverseSimulation.buttons.predictByChart.StartPredictionButton;
import components.reverseSimulation.buttons.predictByChart.PredictSimulationByChartButton;
import components.reverseSimulation.buttons.reverseStep.ChangeModelButton;
import components.reverseSimulation.buttons.reverseStep.SimulationSeriesButton;
import components.reverseSimulation.buttons.reverseStep.StartSimulationButton;
import components.reverseSimulation.buttons.reverseStep.StepButton;
import components.reverseSimulation.buttons.reverseStep.PredictSimulationByReverseStepButton;
import components.reverseSimulation.buttons.reverseStep.report.ReportButton;
import components.reverseSimulation.buttons.reverseStep.visibility.VisibilityOptionsButton;
import components.reverseSimulation.model.NodeData;
import components.simulation.*;
import components.simulationLogic.SimulationComponent;
import configLoader.ConfigLoader;
import helper.ApplySimulationHelper;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Graph;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import simulationModel.SimulationModel;
import simulationModel.interaction.WeightedCommonNeigboursInteraction;
import simulationModel.node.NodeRoleDecorator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

@ConvertAsProperties(dtd = "-//Simulation//ReverseSimulation//EN", autostore = false)
@TopComponent.Description(preferredID = "ReverseSimulation",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "layoutmode", openAtStartup = true)
@ActionID(category = "Window", id = "ReverseSimulation")
@ActionReference(path = "Menu/Window", position = 0)
@TopComponent.OpenActionRegistration(displayName = "#CTL_ReverseSimulation",
        preferredID = "Simulation")
@Getter
@Setter
public class ReverseSimulationComponent extends TopComponent {

    private String modelName;
    private Graph graph;
    private Simulation currentSimulation;
    private Simulation previousStepSimulation;
    private List<Simulation> simulationList;
    private List<List<NodeData>> simulationStatesList;
    private Integer simulationSeries;
    private SimulationModel simulationModel;
    private List<NodeRoleDecorator> nodeRoles;
    private int state = 0;

    public ReverseSimulationComponent() {
        initComponents();
        setName("Reverse Simulation");
        setToolTipText("Revers Simulation");
    }

    public void initComponents() {
        this.removeAll();
        setLayout(new FlowLayout());
        Label simulationName = new Label();
        switch (state) {
            case 1:
                simulationName.setText("Reverse Series Simulation");
                break;
            case 2:
                simulationName.setText("Predict Simulation");
                break;
            default:
                break;
        }
        simulationName.setBounds(5,5, 400,75);


        JButton initButton = new JButton("Init");
        initButton.addActionListener(this::initButtonActionPerformed);
        add(initButton);
        switch (state) {
            case 0:
                add(simulationName);
                if (simulationSeries != null) {
                    add(new PredictSimulationByChartButton(this));
                    add(new PredictSimulationByReverseStepButton(this));
                }
                break;
            case 1:
                JButton resetButton = new JButton("Reset");
                resetButton.addActionListener(this::initButtonActionPerformed);
                add(resetButton);
                add(simulationName);
                add(new PredictSimulationByChartButton(this));
                add(new ChangeModelButton(this));

                if (nodeRoles == null || nodeRoles.isEmpty()) {
                    return;
                }
                var modelName = new JLabel("Current model: " + getModelName());
                add(modelName);

                var modelStatisticInput = new ModelSimpleStatisticsDynamicInput(this).generate(nodeRoles);
                add(modelStatisticInput);

                add(new StepButton(currentSimulation, this));
                add(new StartSimulationButton(currentSimulation, this));
                JButton seriesButton = new JButton("New series");
                seriesButton.addActionListener(this::seriesButtonActionPerformed);
                add(seriesButton);
                add(new SimulationSeriesButton(currentSimulation, this));
                add(new ReportButton(this));
                add(new VisibilityOptionsButton(this));

                var stepLabel = new JLabel("Step: " + currentSimulation.getStep().toString());
                var seriesLabel = new JLabel("Series: " + (getSimulationSeries() == null ? 0 : getSimulationSeries().toString()));
                add(stepLabel);
                add(seriesLabel);
                break;
            case 2:
                add(simulationName);
                add(new PredictSimulationByReverseStepButton(this));
                add(new StartPredictionButton(this));
                break;
            default:
                break;
        }
    }

    private void seriesButtonActionPerformed(ActionEvent e) {
        NewSeries(currentSimulation);
    }

    public Simulation NewSeries(Simulation currentSimulation) {
        if(currentSimulation != null)
        {
            simulationList.add(currentSimulation.clone());
            simulationSeries = simulationList.size() + 1;
            var nodes = new ArrayList<>(List.of(graph.getNodes().toArray()));
            var nodesLastStepState = new ArrayList<NodeData>();
            nodes.forEach(node -> {
                    nodesLastStepState.add(new NodeData(node));
                    node.setAttribute(ConfigLoader.colNameNodeState, node.getAttribute(ConfigLoader.colNameInitialNodeState));
            });
            simulationStatesList.add(nodesLastStepState);
            ApplySimulationHelper.PaintGraph(nodes, simulationModel.getNodeRoles());
        }
        switch (simulationModel.getInteraction().getInteractionType()){
            case All:
                this.currentSimulation = new SimulationAll(graph, simulationModel);
                break;
            case RelativeEdges:
                this.currentSimulation = new SimulationRelativeEdges(graph, simulationModel);
                break;
            case RelativeFreeEdges:
                this.currentSimulation = new SimulationRelativeFreeEdges(graph, simulationModel);
                break;
            case RelativeNodes:
                this.currentSimulation = new SimulationRelativeNodes(graph, simulationModel);
                break;
            case RelativeFreeNodes:
                this.currentSimulation = new SimulationRelativeFreeNodes(graph, simulationModel);
                break;
            case WeighedCommonNeighbors:
                this.currentSimulation = new SimulationWeighedCommonNeighbours(graph, simulationModel);
                break;
            case CommunityPressureInteraction:
                this.currentSimulation = new SimulationCommunityPreasure(graph, simulationModel);
                break;
            case WeightedEdges:
                this.currentSimulation = new SimulationWeighedEdges(graph, simulationModel);
                break;
            default:
                break;
        }
        initComponents();
        revalidate();
        repaint();
        return this.currentSimulation;
    }

    private boolean wasRanSimulation() {
        Simulation simulation = SimulationComponent.getInstance().getCurrentSimulation();
        if (simulation != null) {
            return simulation.getStep() > 0;
        }
        return false;
    }

    private void initButtonActionPerformed(ActionEvent e) {
        if (wasRanSimulation()) {
            simulationSeries = 1;
            simulationList = new ArrayList<>();
            simulationStatesList = new ArrayList<>();
            this.setSimulationModel(SimulationComponent.getInstance().getSimulationModel());
            this.setGraph(SimulationComponent.getInstance().getGraph());
            var table = graph.getModel().getNodeTable();
            if(table.getColumn(ConfigLoader.colNameInitialNodeState) == null)
                table.addColumn(ConfigLoader.colNameInitialNodeState, String.class);
            List.of(graph.getNodes().toArray()).forEach(node -> {
                if (node.getAttribute(ConfigLoader.colNameInitialNodeState) == null) {
                    node.setAttribute(ConfigLoader.colNameInitialNodeState, node.getAttribute(ConfigLoader.colNameNodeState));
                }
            });
            switch (simulationModel.getInteraction().getInteractionType()){
                case All:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationAll(graph, simulationModel);
                    break;
                case RelativeEdges:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationRelativeEdges(graph, simulationModel);
                    break;
                case RelativeFreeEdges:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationRelativeFreeEdges(graph, simulationModel);
                    break;
                case RelativeNodes:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationRelativeNodes(graph, simulationModel);
                    break;
                case RelativeFreeNodes:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationRelativeFreeNodes(graph, simulationModel);
                    break;
                case WeighedCommonNeighbors:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationWeighedCommonNeighbours(graph, simulationModel);
                    break;
                case CommunityPressureInteraction:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationCommunityPreasure(graph, simulationModel);
                    break;
                case WeightedEdges:
                    this.previousStepSimulation = currentSimulation;
                    this.currentSimulation = new SimulationWeighedEdges(graph, simulationModel);
                    break;
                default:
                    break;
            }
            initComponents();
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(null, "Please run normal simulation first.", "Initial error", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_ALWAYS;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
