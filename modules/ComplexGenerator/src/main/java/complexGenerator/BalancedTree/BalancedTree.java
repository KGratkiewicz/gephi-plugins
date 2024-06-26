/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 *
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package complexGenerator.BalancedTree;

import java.util.ArrayList;
import java.util.List;

import org.gephi.io.generator.spi.Generator;
import org.gephi.io.generator.spi.GeneratorUI;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDirectionDefault;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Generates a perfectly balanced r-tree of height h (edges are undirected).
 *
 * r >= 2
 * h >= 1
 *
 * O(r^h)
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Generator.class)
public class BalancedTree implements Generator {
    private boolean cancel = false;
    private ProgressTicket progressTicket;
    private int r = 3;
    private int h = 5;

    @Override
    public void generate(ContainerLoader container) {

        int n = ((int)Math.pow(r, h + 1) - 1) / (r - 1);

        Progress.start(progressTicket, n - 1);
        container.setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);

        // Timestamps
        // int vt = 1;
        // int et = 1;

        // Creating a root of degree r
        int v = 1;
        NodeDraft root = container.factory().newNodeDraft();
        root.setLabel("Node 0");
        root.addInterval("0", h + "");
        container.addNode(root);
        List<NodeDraft> newLeaves = new ArrayList<NodeDraft>();
        for (int i = 0; i < r && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + v++);
            // node.addTimeInterval(vt + "", h + "");
            newLeaves.add(node);
            container.addNode(node);

            EdgeDraft edge = container.factory().newEdgeDraft();
            edge.setSource(root);
            edge.setTarget(node);
            // edge.addTimeInterval(et + "", h + "");
            container.addEdge(edge);

            Progress.progress(progressTicket);
        }
        // vt++;
        // et++;

        // Creating internal nodes
        for (int height = 1; height < h && !cancel; ++height/* , ++vt, ++et */) {
            List<NodeDraft> leaves = newLeaves;
            newLeaves = new ArrayList<NodeDraft>();
            for (NodeDraft leave : leaves)
                for (int i = 0; i < r; ++i) {
                    NodeDraft node = container.factory().newNodeDraft();
                    node.setLabel("Node " + v++);
                    // node.addTimeInterval(vt + "", h + "");
                    newLeaves.add(node);
                    container.addNode(node);

                    EdgeDraft edge = container.factory().newEdgeDraft();
                    edge.setSource(leave);
                    edge.setTarget(node);
                    // edge.addTimeInterval(et + "", h + "");
                    container.addEdge(edge);

                    Progress.progress(progressTicket);
                }
        }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    public int getr() {
        return r;
    }

    public int geth() {
        return h;
    }

    public void setr(int r) {
        this.r = r;
    }

    public void seth(int h) {
        this.h = h;
    }

    @Override
    public String getName() {
        return "Balanced Tree";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(IBalancedTreeUI.class);
    }

    @Override
    public boolean cancel() {
        cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
