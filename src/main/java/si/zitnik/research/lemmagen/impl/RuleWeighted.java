package si.zitnik.research.lemmagen.impl;

import java.io.Serializable;


public class RuleWeighted implements Comparable<RuleWeighted>, Serializable {
    private static final long serialVersionUID = -6239039088824830824L;

    private LemmaRule lrRule;
    private double dWeight;




    public RuleWeighted(LemmaRule lrRule, double dWeight) {
        this.lrRule = lrRule;
        this.dWeight = dWeight;
    }



    public LemmaRule getRule() {
        return lrRule;
    }
    public double getWeight() {
        return dWeight;
    }



    @Override
    public int compareTo(RuleWeighted rl) {
        if (this.dWeight < rl.dWeight) return 1;
        if (this.dWeight > rl.dWeight) return -1;
        if (this.lrRule.getId() < rl.lrRule.getId()) return 1;
        if (this.lrRule.getId() > rl.lrRule.getId()) return -1;
        return 0;
    }

}
