package components.reverseSimulation.model;

import configLoader.ConfigLoader;
import lombok.Getter;
import lombok.Setter;
import org.gephi.graph.api.Node;

@Getter
@Setter
public class NodeData {
    int nodeStoreId;
    String nodeTempState;
    String nodeRootState;


    public NodeData(Node node) {
        this.nodeStoreId = node.getStoreId();
        this.nodeTempState = node.getAttribute(ConfigLoader.colNameTempNodeState).toString();
        this.nodeRootState = node.getAttribute(ConfigLoader.colNameRootState).toString();
    }
}
