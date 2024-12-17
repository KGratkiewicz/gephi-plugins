package WeightedCommonNeighbors;

import GenericParamForm.GenericPanel;

import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedCommonNeighborsPanel extends GenericPanel<WeightedCommonNeighborsParam> {

    @Override
    protected void CreateParamObject() {
        this.setTParams(new WeightedCommonNeighborsParam());
    }

    @Override
    protected void CreateCustomFieldOption(GridBagConstraints constraints, AtomicInteger gridYIterator) {
    }
}