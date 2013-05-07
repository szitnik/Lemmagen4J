package si.zitnik.research.lemmagen.impl;

import java.util.HashMap;


public class RuleList extends HashMap<String, LemmaRule> {
    private static final long serialVersionUID = -8493003011756846000L;

    private LemmatizerSettings lsett;
    private LemmaRule lrDefaultRule;

    public RuleList(LemmatizerSettings lsett) {
        this.lsett = lsett;
        lrDefaultRule = AddRule(new LemmaRule("", "", 0, lsett));
    }


    public LemmaRule getDefaultRule() {
        return lrDefaultRule;
    }


    public LemmaRule AddRule(LemmaExample le) {
        return AddRule(new LemmaRule(le.getWord(), le.getLemma(), this.keySet().size(), lsett));
    }

    private LemmaRule AddRule(LemmaRule lrRuleNew) {
        if (!this.containsKey(lrRuleNew.getSignature())) {
            this.put(lrRuleNew.getSignature(), lrRuleNew);
        }

        return lrRuleNew;
    }


}

