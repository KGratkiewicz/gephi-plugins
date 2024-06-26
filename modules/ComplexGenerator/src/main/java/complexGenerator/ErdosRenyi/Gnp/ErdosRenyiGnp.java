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
package complexGenerator.ErdosRenyi.Gnp;

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

import java.util.Random;

/**
 * Generates an undirected not necessarily connected graph.
 *
 * http://www.math-inst.hu/~p_erdos/1960-10.pdf
 * http://www.inf.uni-konstanz.de/algo/publications/bb-eglrn-05.pdf
 *
 * n > 0
 * 0 <= p <= 1
 *
 * O(n^2)
 *
 * @author Cezary Bartosiak
 */
@ServiceProvider(service = Generator.class)
public class ErdosRenyiGnp implements Generator {
    private boolean cancel = false;
    private ProgressTicket progressTicket;

    private int    n = 50;
    private double p = 0.05;

    @Override
    public void generate(ContainerLoader container) {
        Progress.start(progressTicket, n + n * n);
        Random random = new Random();
        container.setEdgeDefault(EdgeDirectionDefault.UNDIRECTED);

        // Timestamps
        // int vt = 0;
        // int et = 1;

        NodeDraft[] nodes = new NodeDraft[n];

        // Creating n nodes
        for (int i = 0; i < n && !cancel; ++i) {
            NodeDraft node = container.factory().newNodeDraft();
            node.setLabel("Node " + i);
            // node.addTimeInterval(vt + "", n * n + "");
            nodes[i] = node;
            container.addNode(node);
            Progress.progress(progressTicket);
        }

        // Linking every node with each other with probability p (no self-loops)
        for (int i = 0; i < n && !cancel; ++i)
            for (int j =  i + 1; j < n && !cancel; ++j/* , ++et */)
                if (random.nextDouble() <= p) {
                    EdgeDraft edge = container.factory().newEdgeDraft();
                    edge.setSource(nodes[i]);
                    edge.setTarget(nodes[j]);
                    // edge.addTimeInterval(et + "", n * n + "");
                    container.addEdge(edge);
                    Progress.progress(progressTicket);
                }

        Progress.finish(progressTicket);
        progressTicket = null;
    }

    public int getn() {
        return n;
    }

    public double getp() {
        return p;
    }

    public void setn(int n) {
        this.n = n;
    }

    public void setp(double p) {
        this.p = p;
    }

    @Override
    public String getName() {
        return "Erdos-Renyi G(n, p) model";
    }

    @Override
    public GeneratorUI getUI() {
        return Lookup.getDefault().lookup(IErdosRenyiGnpUI.class);
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
