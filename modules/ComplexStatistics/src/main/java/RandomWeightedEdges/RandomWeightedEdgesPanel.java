package RandomWeightedEdges;

import GenericParamForm.GenericPanel;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomWeightedEdgesPanel extends GenericPanel<RandomWeightedEdgesParam> {

    @Override
    protected void CreateParamObject() {
        this.setTParams(new RandomWeightedEdgesParam());
    }

    @Override
    protected void CreateCustomFieldOption(GridBagConstraints constraints, AtomicInteger gridYIterator) {
    }
}