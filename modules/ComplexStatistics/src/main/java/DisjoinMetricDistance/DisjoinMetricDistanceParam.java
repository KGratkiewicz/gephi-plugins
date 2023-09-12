package DisjoinMetricDistance;

import GenericParamForm.Params;
import java.util.ArrayList;
import java.util.List;

public class DisjoinMetricDistanceParam extends Params<DisjoinMetricDistance> {

    private Boolean directed = false;

    @Override
    protected String ShortDescription() {
        return "?";
    }
    @Override
    protected List<String> Descritpion() {
        var description = new ArrayList<String>();
        description.add("Parameters description:");
        description.add("directed - is graph directed (default undirected)");
        return description;
    }

    @Override
    public void SetGeneratorParams(DisjoinMetricDistance disjoinMetricDistance) {
        disjoinMetricDistance.setDirected(directed);
    }

}
