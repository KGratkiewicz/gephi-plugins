/*
 * Copyright 2008-2012 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 *
 * This file is part of Gephi.
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2012 Gephi Consortium. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 3 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://gephi.org/about/legal/license-notice/
 * or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License files at
 * /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 3, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 3] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 3 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 3 code and therefore, elected the GPL
 * Version 3 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2011 Gephi Consortium.
 */
package RandomWeightedEdges;

import lombok.Setter;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static Utils.Utils.randomWeightedEdgesIndirect;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class RandomWeightedEdges implements Statistics, LongTask {
    private Boolean cancel = false;
    private ProgressTicket progressTicket;
    private double value = 0.0;
    private boolean isDirected = false;
    private Double mean = 0.5;
    private final String statisticColName = "WEIGHT";

    @Override
    public void execute(GraphModel graphModel) {
        Graph graph;
        var edgeTable = graphModel.getEdgeTable();
        var column = edgeTable.getColumn(statisticColName);
        if(column == null){
            edgeTable.addColumn(statisticColName, float.class);
        }
        if (isDirected)
            graph = graphModel.getDirectedGraph();
        else {
            graph = graphModel.getUndirectedGraph();
        }
        execute(graph);
    }

    @Override
    public String getReport() {
        NumberFormat f = new DecimalFormat("#0.0000");

        String report = "<html><body><h1>WeightedCommonNeighbors</h1>"
                + "<hr>"
                + "<br>"
                + "<br><h2>Results:</h2>"
                + "Average metric: " + f.format(value)
                + "Metrics added individual metric for all edges in column: " + statisticColName
                + "</body></html>";

        return report;
    }
    public void execute(Graph graph) {
        cancel = false;

        value = 0.0;

        graph.readLock();

        int n = graph.getEdgeCount();
        Progress.start(progressTicket, n);

        value = randomWeightedEdgesIndirect(graph, isDirected, cancel, progressTicket, statisticColName, mean);

        graph.readUnlock();
    }

    public double getValue() {
        return value;
    }

    public void setDirected(boolean isDirected) {
        this.isDirected = isDirected;
    }

    public void setMean(Double mean){
        this.mean = mean;
    }

    public boolean isDirected() {
        return isDirected;
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }
}
