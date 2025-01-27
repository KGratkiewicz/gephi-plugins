package CyberGlobalImporter;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;

import CyberGlobalImporter.Model.AeonTimelineRoot;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.ImportUtils;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.FileImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

public class CyberGlobalImporter implements FileImporter, LongTask {
    private Reader reader;
    private ContainerLoader container;
    private Report report;
    private ProgressTicket progressTicket;
    private boolean cancel = false;

    @Override
    public boolean execute(ContainerLoader container){
        this.container = container;
        this.report = new Report();
        //Import
        try{
            importData(reader);
        }catch(IOException e){}
        return !cancel;
    }

    private void importData(Reader reader) throws IOException {
        var objectMapper = new ObjectMapper();
        try (LineNumberReader lineReader = ImportUtils.getTextReader(reader)) {
            AeonTimelineRoot root = objectMapper.readValue(lineReader, AeonTimelineRoot.class);
            var data = root.getData();
            var items = data.getItems();
            var relations = data.getRelationships();
            container.addNodeColumn("type", String.class);
            container.addNodeColumn("start", String.class);
            container.addNodeColumn("latestStart", String.class);
            container.addNodeColumn("earliestEnd", String.class);
            container.addNodeColumn("end", String.class);
            container.addNodeColumn("ongoing", Boolean.class);
            container.addNodeColumn("super", String.class);
            container.addNodeColumn("locationUri", String.class);
            for (var item : items) {
                var node = container.factory().newNodeDraft(item.getId());
                node.setValue("type", item.getType());
                node.setLabel(item.getLabel());
                node.setValue("start", item.getStart());
                node.setValue("latestStart", item.getLatestStart());
                node.setValue("earliestEnd", item.getEarliestEnd());
                node.setValue("end", item.getEnd());
                node.setValue("ongoing", item.isOngoing());
                node.setValue("super", item.getSuperItem());
                node.setValue("locationUri", item.getLocationUri());
                var values = item.getPropertyValues();
                values.entrySet().forEach(x -> container.addNodeColumn(x.getKey(), String.class));
                values.entrySet().forEach(x -> node.setValue(x.getKey(), values.getOrDefault(x.getKey(), x.getValue())));
                container.addNode(node);
            }
            container.addEdgeColumn("relationshipType", String.class);
            for (var relation : relations) {
                var edge = container.factory().newEdgeDraft(relation.getId());
                var source = container.getNode(relation.getSubject());
                var target = container.getNode(relation.getTarget());
                edge.setSource(source);
                edge.setTarget(target);
                edge.setValue("relationshipType", relation.getRelationshipType());
                container.addEdge(edge);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}