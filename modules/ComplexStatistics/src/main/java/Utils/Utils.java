package Utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;

public class Utils {

    public static double[][] floydWarshall(Graph graph, Boolean cancel, ProgressTicket progressTicket) {
        int n = graph.getNodeCount();
        double[][] d = new double[n][n];
        Node[] nodes = graph.getNodes().toArray();
        for (int i = 0; i < n && !cancel; i++)
            for (int j = 0; j < n && !cancel; j++) {
                if (i == j)
                    d[i][j] = 0.0;
                else if (graph.isAdjacent(nodes[i], nodes[j]))
                    d[i][j] = 1.0; // TODO: consider edge weight
                else d[i][j] = Double.POSITIVE_INFINITY;
                Progress.progress(progressTicket);
            }
        for (int k = 0; k < n && !cancel; k++)
            for (int i = 0; i < n && !cancel; i++) {
                for (int j = 0; j < n && !cancel; j++)
                    d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
                Progress.progress(progressTicket);
            }
        return d;
    }

    public static int connectedComponents(Graph graph, int[] componentIndex,
                                   Boolean cancel, ProgressTicket progressTicket) {
        int n = graph.getNodeCount();
        int count = 0;

        int index = 0;
        HashMap<Node, Integer> indices = new HashMap<Node, Integer>();
        for (Node node : graph.getNodes()) {
            if (cancel)
                break;
            indices.put(node, index++);
        }

        boolean[] visited = new boolean[n];
        for (Node node : graph.getNodes()) {
            if (cancel)
                break;
            if (!visited[indices.get(node)]) {
                Stack<Node> stack = new Stack<Node>();
                stack.push(node);
                while (!stack.empty()) {
                    if (cancel)
                        break;
                    Progress.progress(progressTicket);
                    Node v = stack.pop();
                    visited[indices.get(v)] = true;
                    componentIndex[indices.get(v)] = count;
                    for (Node w : graph.getNeighbors(v)) {
                        if (cancel)
                            break;
                        if (!visited[indices.get(w)])
                            stack.push(w);
                    }
                }
                count++;
            }
        }
        return count;
    }

    public static double disjoinMetricSimple(int n, int count) {
        return (double)count / n;
    }

    public static double disjoinMetricSize1(int n, double[][] d, Boolean cancel, ProgressTicket progressTicket) {
        double sum = 0.0;
        for (int i = 0; i < n && !cancel; i++)
            for (int j = 0; j < n && !cancel; j++) {
                if (i != j)
                    sum += d[i][j] < Double.POSITIVE_INFINITY ? 1 : 0;
                Progress.progress(progressTicket);
            }
        return 1 - sum / (double)(n * (n - 1));
    }

    public static double disjoinMetricSize2(int n, int count, int[] componentIndex,
                                            Boolean cancel, ProgressTicket progressTicket) {
        int[] size = new int[count];
        for (int i = 0; i < n; i++) {
            if (cancel)
                break;
            Progress.progress(progressTicket);
            size[componentIndex[i]]++;
        }

        double sum = 0.0;
        for (int k = 0; k < count && !cancel; k++)
            sum += size[k] * (size[k] - 1);
        return 1 - sum / (double)(n * (n - 1));
    }

    public static double disjoinMetricDistance(int n, double[][] d, Boolean cancel, ProgressTicket progressTicket) {
        double sum = 0.0;
        for (int i = 0; i < n && !cancel; i++)
            for (int j = 0; j < n && !cancel; j++) {
                if (i != j)
                    sum += 1.0 / d[i][j];
                Progress.progress(progressTicket);
            }
        return 1 - sum / (double)(n * (n - 1));
    }

    public static double reachMetric(Graph graph, Table nodeTable, double[][] d, Boolean cancel, ProgressTicket progressTicket, String columnName) {
        int n = graph.getNodeCount();
        int size = 0;

        Node[] nodes = graph.getNodes().toArray();
        boolean[] inset = new boolean[n];
        if (nodeTable.hasColumn(columnName) && nodeTable.getColumn(columnName).getTypeClass().equals(Boolean.class))
            for (int i = 0; i < n && !cancel; i++) {
                Object val = nodes[i].getAttribute(columnName);
                if (val != null && Boolean.valueOf(val.toString())) {
                    inset[i] = true;
                    size++;
                }
                Progress.progress(progressTicket);
            }

        double[] dset = new double[n];
        for (int i = 0; i < n && !cancel; i++) {
            dset[i] = Double.POSITIVE_INFINITY;
            Progress.progress(progressTicket);
        }
        for (int i = 0; i < n && !cancel; i++) {
            if (!inset[i])
                for (int j = 0; j < n && !cancel; j++) {
                    if (inset[j]) {
                        if (d[i][j] < dset[i])
                            dset[i] = d[i][j];
                    }
                }
            else dset[i] = 0;
            Progress.progress(progressTicket);
        }

        double sum = 0.0;
        for (int j = 0; j < n && !cancel; j++) {
            if (!inset[j])
                sum += 1 / dset[j];
            Progress.progress(progressTicket);
        }
        return sum / (n - size);
    }

    public static double weightedCommonNeighborsIndirect(Graph graph, boolean isDirected, Boolean cancel, ProgressTicket progressTicket, String columnName) {
        var edges = graph.getEdges().toCollection();
        AtomicReference<Double> totalWCN = new AtomicReference<>(0.0);
        AtomicInteger edgeCount = new AtomicInteger();

        edges.forEach(edge -> {
            var srcNode = edge.getSource();
            var dstNode = edge.getTarget();

            var srcNeighbours = new LinkedHashSet<>(Arrays.asList(graph.getNeighbors(srcNode).toArray()));
            srcNeighbours.add(srcNode);

            var dstNeighbours = new LinkedHashSet<>(Arrays.asList(graph.getNeighbors(dstNode).toArray()));
            dstNeighbours.add(dstNode);

            var resultSet = new LinkedHashSet<>();
            resultSet.addAll(srcNeighbours);
            resultSet.addAll(dstNeighbours);

            var sum = resultSet.toArray();

            var srcSet = new LinkedHashSet<>(srcNeighbours);
            var dstSet = new LinkedHashSet<>(dstNeighbours);

            srcSet.retainAll(dstSet);
            var sub = srcSet.toArray();

            var wcn = (float) sub.length / sum.length;
            edge.setAttribute(columnName, wcn);

            totalWCN.updateAndGet(v -> new Double((double) (v + wcn)));
            edgeCount.getAndIncrement();

            progressTicket.progress(1);
        });

        return edgeCount.get() > 0 ? totalWCN.get() / edgeCount.get() : 0.0;
    }

    public static double randomWeightedEdgesIndirect(Graph graph, boolean isDirected, Boolean cancel, ProgressTicket progressTicket, String columnName, Double mean) {
        var edges = new ArrayList<>(graph.getEdges().toCollection());
        Collections.shuffle(edges);
        AtomicReference<Double> totalWCN = new AtomicReference<>(0.0);
        AtomicInteger edgeCount = new AtomicInteger();
        var random = new Random();
        var first = random.nextDouble();
        edges.get(0).setAttribute(columnName, first);
        edgeCount.getAndIncrement();
        totalWCN.updateAndGet(v -> new Double((double) (v + first)));

        edges.stream().skip(1).forEach(edge -> {
            double wcn;
            if(totalWCN.get()/edgeCount.get() > mean)
                wcn = random.nextDouble() * mean;
            else
                wcn = mean + random.nextDouble() * (1 - mean);
            edge.setAttribute(columnName, wcn);
            totalWCN.updateAndGet(v -> new Double((double) (v + wcn)));
            edgeCount.getAndIncrement();
            progressTicket.progress(1);
        });
        return edgeCount.get() > 0 ? totalWCN.get() / edgeCount.get() : 0.0;
    }

}
