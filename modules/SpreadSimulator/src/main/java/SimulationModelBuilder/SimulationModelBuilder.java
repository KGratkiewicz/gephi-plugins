package SimulationModelBuilder;


import Helper.ApplySimulationHelper;
import Helper.ObjectMapperHelper;
import SimulationModel.Node.NodeRoleDecorator;
import SimulationModel.SimulationModel;
import lombok.Setter;
import org.gephi.datalab.api.datatables.DataTablesController;
import org.gephi.datalab.spi.ManipulatorUI;
import org.gephi.datalab.spi.general.PluginGeneralActionsManipulator;
import org.gephi.graph.api.*;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@ServiceProvider(service = PluginGeneralActionsManipulator.class)
public class SimulationModelBuilder implements PluginGeneralActionsManipulator {

    @Setter
    private SimulationModel simulationModel;

    @Override
    public void execute() {
        DataTablesController dtc = Lookup.getDefault().lookup(DataTablesController.class);
        Graph graph = Lookup.getDefault().lookup(GraphController.class).getGraphModel().getGraph();
        ApplySimulationHelper.CrateModelCollumns(graph);
        ApplySimulationHelper.Apply(graph, simulationModel);
    }

    @Override
    public String getName() {
        return "Spread Simulation Builder";
    }

    @Override
    public String getDescription() {
        return "Simulation Model Builder creates empty data model required to conduct spread simulation";
    }

    @Override
    public boolean canExecute() {
        return true;
    }

    @Override
    public ManipulatorUI getUI() {
        return new SimulationModelBuilderUI(this.simulationModel);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

}
