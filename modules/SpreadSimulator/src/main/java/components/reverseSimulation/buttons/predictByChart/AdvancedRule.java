package components.reverseSimulation.buttons.predictByChart;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class AdvancedRule {
    public String rule;
    public Integer numberOfNodes;
    public boolean ascending;

    @Override
    public String toString(){
        return  rule + " " +
                numberOfNodes.toString() + " "
                + (ascending ? "ascending" : "descending");
    }
}