package components.reverseSimulation.model;

import configLoader.ConfigLoader;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Node;

@Getter
@Setter
public class NodeData {
    int nodeStroeId;
    String nodeCurrnetState;
    String nodeRootState;


    public NodeData(Node node) {
        this.nodeStroeId = node.getStoreId();
        this.nodeCurrnetState = node.getAttribute(ConfigLoader.colNameNodeState).toString();
        this.nodeRootState = node.getAttribute(ConfigLoader.colNameRootState).toString();
    }
}
