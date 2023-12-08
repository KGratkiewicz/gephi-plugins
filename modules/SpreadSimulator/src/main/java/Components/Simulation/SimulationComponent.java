package Components.Simulation;

import Components.Simulation.Simulation.*;
import ConfigLoader.ConfigLoader;
import Helper.ApplySimulationHelper;
import Helper.ObjectMapperHelper;
import SimulationModel.Interaction.RelativeEdgesInteraction;
import SimulationModel.Interaction.RelativeFreeEdgesInteraction;
import SimulationModel.Interaction.RelativeFreeNodesInteraction;
import SimulationModel.Interaction.RelativeNodesInteraction;
import SimulationModel.Node.NodeRoleDecorator;
import SimulationModel.Node.NodeStateDecorator;
import SimulationModel.SimulationModel;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@ConvertAsProperties(dtd = "-//Simulation//Simulation//EN", autostore = false)
@TopComponent.Description(preferredID = "Simulation",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "layoutmode", openAtStartup = true)
@ActionID(category = "Window", id = "Simulation")
@ActionReference(path = "Menu/Window", position = 0)
@TopComponent.OpenActionRegistration(displayName = "#CTL_Simulation",
        preferredID = "Simulation")
public class SimulationComponent extends TopComponent {

    private Graph graph;
    @Getter
    private Simulation currentSimulation;
    private List<Simulation> simulationList;
    private Integer simulationSeries;
    private SimulationModel simulationModel;

    public SimulationComponent() {
        initComponents();
        setName(ConfigLoader.componentNameSimulationComponent);
        setToolTipText(ConfigLoader.componentNameSimulationComponent);
    }

    public void initComponents() {
        this.removeAll();
        setLayout(new FlowLayout());
        JButton initButton = new JButton("Init");
        initButton.addActionListener(this::initButtonActionPerformed);
        add(initButton);


        if (!isGraphValid()) {
            return;
        }
        JButton seriesButton = new JButton("New series");
        seriesButton.addActionListener(this::seriesButtonActionPerformed);
        add(seriesButton);
        add(generateInfoFieldsForRolesAndStates());
        add(new StepButton(currentSimulation, this));
        add(new SimulationButton(currentSimulation, this));
        add(new SimulationSeriesButton(currentSimulation, this));
        add(new GetReportButton(this));
        add(new GetSeriesReportButton(this));
    }

    private boolean isGraphValid() {
        return graph != null && ApplySimulationHelper.ValidateGraph(graph);
    }

    private void seriesButtonActionPerformed(ActionEvent e) {
        NewSeries();
    }

    public void NewSeries() {
        if(currentSimulation != null)
        {
            simulationList.add(currentSimulation);
            simulationSeries = simulationList.size() + 1;
            var nodes =  new ArrayList<> (List.of(graph.getNodes().toArray()));
            nodes.forEach(node -> node.setAttribute(ConfigLoader.colNameNodeState.toString(), node.getAttribute(ConfigLoader.colNameRootState).toString()));
            ApplySimulationHelper.PaintGraph(nodes, currentSimulation.getNodeRoleDecoratorList());
        }
        switch (simulationModel.getInteraction().getInteractionType()){
            case All:
                currentSimulation = new SimulationAll(graph, simulationModel);
                break;
            case RelativeEdges:
                currentSimulation = new SimulationRelativeEdges(graph, simulationModel);
                break;
            case RelativeFreeEdges:
                currentSimulation = new SimulationRelativeFreeEdges(graph, simulationModel);
                break;
            case RelativeNodes:
                currentSimulation = new SimulationRelativeNodes(graph, simulationModel);
                break;
            case RelativeFreeNodes:
                currentSimulation = new SimulationRelativeFreeNodes(graph, simulationModel);
                break;
            default:
                break;
        }
        initComponents();
        revalidate();
        repaint();
    }

    private void initButtonActionPerformed(ActionEvent e) {
        try {
            simulationSeries = 1;
            simulationList = new ArrayList<Simulation>();
            var graphModel = Lookup.getDefault().lookup(GraphController.class).getGraphModel();
            var table = graphModel.getNodeTable();
            if(table.getColumn(ConfigLoader.colNameRootState.toString()) == null)
                table.addColumn(ConfigLoader.colNameRootState.toString(), String.class);
            graph = graphModel.getGraph();
            if (!ApplySimulationHelper.ValidateGraph(graph)) {
                JOptionPane.showMessageDialog(null, "This is not a valid graph model");
            } else {
                var nodes =  new ArrayList<> (List.of(graph.getNodes().toArray()));
                nodes.forEach(node -> node.setAttribute(ConfigLoader.colNameRootState.toString(), node.getAttribute(ConfigLoader.colNameNodeState).toString()));
                var mapper = ObjectMapperHelper.CustomObjectMapperCreator();
                var path = new File(ConfigLoader.folderSimulationTmp + ConfigLoader.folderSimulationTmpFilename);
                var content = new String(Files.readAllBytes(Paths.get(path.getAbsolutePath())));
                this.simulationModel = mapper.readValue(content, SimulationModel.class);
                seriesButtonActionPerformed(e);
            }
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Set up graph model first");
        } catch (JsonMappingException ex) {
            throw new RuntimeException(ex);
        } catch (JsonParseException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }


    private JScrollPane generateInfoFieldsForRolesAndStates() {
        var panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        var simulationSeriesLabel = new JLabel("Simulation Series: " + simulationSeries.toString());
        panel.add(simulationSeriesLabel);

        var stepLabel = new JLabel("Step: " + currentSimulation.getStep().toString());
        panel.add(stepLabel);

        var interaction = currentSimulation.getSimulationModel().getInteraction();
        var interactionMessage = "";
        switch (interaction.getInteractionType()){
            case All:
                interactionMessage = "All";
                break;
            case RelativeNodes:
                interactionMessage = "RelativeNodes: " + ((RelativeNodesInteraction) interaction).getPercentage();
                break;
            case RelativeEdges:
                interactionMessage = "RelativeEdges: " + ((RelativeEdgesInteraction) interaction).getPercentage();
                break;
            case RelativeFreeNodes:
                interactionMessage = "RelativeFreeNodes: " + ((RelativeFreeNodesInteraction) interaction).getNumber();
                break;
            case RelativeFreeEdges:
                interactionMessage = "RelativeFreeEdges: " + ((RelativeFreeEdgesInteraction) interaction).getNumber();
                break;
        }
        var interactionLabel = new JLabel("Interaction: " + interactionMessage);
        panel.add(interactionLabel);

        int row = 1;
        int padding = 4;
        for (NodeRoleDecorator role : currentSimulation.getNodeRoleDecoratorList()) {
            addRoleToPanel(panel, role, gbc, row, padding);
            row += 3;
            for (NodeStateDecorator state : role.getNodeStates()) {
                addStateToPanel(panel, state, gbc, row, padding);
                row += 3;
            }
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }

    private void addRoleToPanel(JPanel panel, NodeRoleDecorator role, GridBagConstraints gbc, int row, int padding) {
        gbc.insets = new Insets(padding, padding, padding, padding);
        gbc.gridy = row++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel nodeLabel = new JLabel("NodeRole: " + role.getNodeRole().getName());
        Font currentFont = nodeLabel.getFont();
        nodeLabel.setFont(currentFont.deriveFont(currentFont.getStyle() | Font.BOLD, currentFont.getSize()+5)); // Wytłuszczenie i zwiększenie rozmiaru o 2 punkty
        panel.add(nodeLabel, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("Coverage:"), gbc);
        gbc.gridx = 1;
        JTextField roleCoverageField = new JTextField(10); // 10 columns wide
        roleCoverageField.setEditable(false);
        roleCoverageField.setText(role.getCoverage().toString());

        panel.add(roleCoverageField, gbc);

        gbc.gridy = row++;
        gbc.gridx = 0;
        panel.add(new JLabel("Count:"), gbc);
        gbc.gridx = 1;
        JTextField roleMinCoverageField = new JTextField(10);
        roleMinCoverageField.setEditable(false);
        roleMinCoverageField.setText(role.getMinCoverage().toString());

        panel.add(roleMinCoverageField, gbc);

    }

    private void addStateToPanel(JPanel panel, NodeStateDecorator state, GridBagConstraints gbc, int row, int padding) {
        gbc.insets = new Insets(padding, padding, padding, padding);
        gbc.gridy = row++;
        gbc.gridx = 0;

        JLabel stateLabel = new JLabel("NodeState: " + state.getNodeState().getName());
        var currentFont = stateLabel.getFont();
        stateLabel.setFont(currentFont.deriveFont(currentFont.getStyle() | Font.BOLD, currentFont.getSize())); // Wytłuszczenie i zwiększenie rozmiaru o 2 punkty
        panel.add(stateLabel, gbc);

        gbc.gridy = row++;
        panel.add(new JLabel("Coverage:"), gbc);
        gbc.gridx = 1;
        JTextField stateCoverageField = new JTextField(10); // 10 columns wide
        stateCoverageField.setEditable(false);
        stateCoverageField.setText(state.getCoverage().toString());
        panel.add(stateCoverageField, gbc);

        gbc.gridy = row++;
        gbc.gridx = 0;
        panel.add(new JLabel("Count:"), gbc);
        gbc.gridx = 1;
        JTextField stateMinCoverageField = new JTextField(10); // 10 columns wide
        stateMinCoverageField.setText(state.getMinCoverage().toString());
        stateMinCoverageField.setEditable(false);

        panel.add(stateMinCoverageField, gbc);
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
