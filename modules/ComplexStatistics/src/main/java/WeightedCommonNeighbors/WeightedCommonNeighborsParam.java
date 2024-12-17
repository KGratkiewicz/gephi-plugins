package WeightedCommonNeighbors;

import GenericParamForm.Params;

import java.util.ArrayList;
import java.util.List;

public class WeightedCommonNeighborsParam extends Params<WeightedCommonNeighbors> {

    private Boolean directed = false;

    @Override
    protected String ShortDescription() {
        return "Weighted Common Neighbors";
    }

    @Override
    protected List<String> Descritpion() {
        var description = new ArrayList<String>();
        description.add("Parameters description:");
        description.add("directed - is graph directed (default undirected)");
        return description;
    }

    @Override
    public void SetGeneratorParams(WeightedCommonNeighbors weightedCommonNeighbors) {
        weightedCommonNeighbors.setDirected(directed);
    }

}
