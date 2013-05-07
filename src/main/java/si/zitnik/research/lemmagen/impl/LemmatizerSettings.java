package si.zitnik.research.lemmagen.impl;

import java.io.Serializable;


public class LemmatizerSettings implements Serializable {
    private static final long serialVersionUID = -1773211074438614276L;

    public LemmatizerSettings() { }



    public LemmatizerSettings(
            Boolean bUseFromInRules,
            MsdConsideration eMsdConsider,
            int iMaxRulesPerNode,
            Boolean bBuildFrontLemmatizer) {
        super();
        this.bUseFromInRules = bUseFromInRules;
        this.eMsdConsider = eMsdConsider;
        this.iMaxRulesPerNode = iMaxRulesPerNode;
        this.bBuildFrontLemmatizer = bBuildFrontLemmatizer;
    }






    /// <summary>
    /// True if from string should be included in rule identifier ([from]->[to]). False if just length of from string is used ([#len]->[to]).
    /// </summary>
    public Boolean bUseFromInRules = true;
    /// <summary>
    /// Specification how algorithm considers msd tags.
    /// </summary>
    public MsdConsideration eMsdConsider = MsdConsideration.Distinct;
    /// <summary>
    /// How many of the best rules are kept in memory for each node. Zero means unlimited.
    /// </summary>
    public int iMaxRulesPerNode = 0;
    /// <summary>
    /// If true, than build proccess uses few more hevristics to build first left to right lemmatizer (lemmatizes front of the word)
    /// </summary>
    public Boolean bBuildFrontLemmatizer = false;


    public LemmatizerSettings CloneDeep() {
        return new LemmatizerSettings(
                this.bUseFromInRules,
                this.eMsdConsider,
                this.iMaxRulesPerNode,
                this.bBuildFrontLemmatizer
        );
    }

    public String toString() {
        return String.format("%s\t%s\t%d\t%s",
                this.bUseFromInRules.toString(),
                this.eMsdConsider.toString(),
                this.iMaxRulesPerNode,
                this.bBuildFrontLemmatizer.toString());
    }

}
