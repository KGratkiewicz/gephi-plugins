package RandomWeightedEdges;

import GenericParamForm.Params;

import java.util.ArrayList;
import java.util.List;

public class RandomWeightedEdgesParam extends Params<RandomWeightedEdges> {

    public Boolean directed = false;
    public Double mean = 0.5;

    @Override
    protected String ShortDescription() {
        return "Random weighted edges";
    }

    @Override
    protected List<String> Descritpion() {
        var description = new ArrayList<String>();
        description.add("Parameters description:");
        description.add("directed - is graph directed (default undirected)");
        description.add("mean - average value considering all edges in the graph [0-1]");
        return description;
    }

    @Override
    public void SetGeneratorParams(RandomWeightedEdges randomWeightedEdges) {
        randomWeightedEdges.setDirected(directed);
        randomWeightedEdges.setMean(mean);
    }

}
