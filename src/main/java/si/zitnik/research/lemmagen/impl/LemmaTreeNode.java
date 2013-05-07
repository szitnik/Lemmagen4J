package si.zitnik.research.lemmagen.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import si.zitnik.research.lemmagen.abst.ILemmatizerModel;


public class LemmaTreeNode implements ILemmatizerModel, Serializable {
    private static final long serialVersionUID = 8055503193911216366L;

    //settings
    private LemmatizerSettings lsett;

    //tree structure references
    private HashMap<Character, LemmaTreeNode> dictSubNodes;
    private LemmaTreeNode ltnParentNode;

    //essential node properties
    private int iSimilarity; //similarity among all words in this node
    private String sCondition; //suffix that must match in order to lemmatize
    private Boolean bWholeWord; //true if condition has to match to whole word

    //rules and weights;
    private LemmaRule lrBestRule; //the best rule to be applied when lemmatizing
    private RuleWeighted[] aBestRules; //list of best rules
    private double dWeight;

    //source of this node
    private int iStart;
    private int iEnd;
    private ExampleList elExamples;

    private LemmaTreeNode(LemmatizerSettings lsett) {
        this.lsett = lsett;
    }
    public LemmaTreeNode(LemmatizerSettings lsett, ExampleList elExamples) {
        this(lsett, elExamples, 0, elExamples.getCount()-1, null);
    }

    /// <summary>
    ///
    /// </summary>
    /// <param name="lsett"></param>
    /// <param name="elExamples"></param>
    /// <param name="iStart">Index of the first word of the current group</param>
    /// <param name="iEnd">Index of the last word of the current group</param>
    /// <param name="ltnParentNode"></param>
    private LemmaTreeNode(LemmatizerSettings lsett, ExampleList elExamples, int iStart, int iEnd, LemmaTreeNode ltnParentNode) {
        this(lsett);

        this.ltnParentNode = ltnParentNode;
        this.dictSubNodes = null;

        this.iStart = iStart;
        this.iEnd = iEnd;
        this.elExamples = elExamples;

        if (iStart >= elExamples.getCount() || iEnd >= elExamples.getCount() || iStart > iEnd) {
            lrBestRule = elExamples.getRules().getDefaultRule();
            aBestRules = new RuleWeighted[1];
            aBestRules[0] = new RuleWeighted(lrBestRule, 0);
            dWeight = 0;
            return;
        }


        int iConditionLength = Math.min(ltnParentNode == null ? 0 : ltnParentNode.iSimilarity + 1, elExamples.getExampleAt(iStart).getWord().length());
        this.sCondition = elExamples.getExampleAt(iStart).getWord().substring(elExamples.getExampleAt(iStart).getWord().length() - iConditionLength);
        this.iSimilarity = elExamples.getExampleAt(iStart).Similarity(elExamples.getExampleAt(iEnd));
        this.bWholeWord = ltnParentNode == null ? false : elExamples.getExampleAt(iEnd).getWord().length() == ltnParentNode.iSimilarity;

        FindBestRules();
        AddSubAll();


        //TODO check this heuristics, can be problematic when there are more applicable rules
        if (dictSubNodes != null) {
            HashMap<Character, LemmaTreeNode> lReplaceNodes = new HashMap<Character, LemmaTreeNode>();
            for (Entry<Character, LemmaTreeNode> kvpChild : dictSubNodes.entrySet())
                if (kvpChild.getValue().dictSubNodes != null && kvpChild.getValue().dictSubNodes.size() == 1) {
                    Iterator<LemmaTreeNode> enumChildChild = kvpChild.getValue().dictSubNodes.values().iterator();
                    LemmaTreeNode ltrChildChild = enumChildChild.next();
                    if (kvpChild.getValue().lrBestRule == lrBestRule)
                        lReplaceNodes.put(kvpChild.getKey(), ltrChildChild);
                }
            for (Character kvpChild : lReplaceNodes.keySet()) {
                dictSubNodes.put(kvpChild, lReplaceNodes.get(kvpChild));
                lReplaceNodes.get(kvpChild).ltnParentNode = this;
            }

        }

    }

    public int getTreeSize() {
        int iCount = 1;
        if (dictSubNodes != null)
            for (LemmaTreeNode ltnChild : dictSubNodes.values())
                iCount += ltnChild.getTreeSize();
        return iCount;
    }
    public double getWeight() {
        return dWeight;
    }

    private void FindBestRules() {
		/*
		 *  LINQ SPEED TEST (Slower than current metodology)
		 * 

            List<LemmaExample> leApplicable = new List<LemmaExample>();
            for (int iExm = iStart; iExm <= iEnd; iExm++)
                if (elExamples[iExm].Rule.IsApplicableToGroup(sCondition.Length))
                    leApplicable.Add(elExamples[iExm]);

            List<KeyValuePair<LemmaRule, double>> lBestRules = new List<KeyValuePair<LemmaRule,double>>();
            lBestRules.AddRange(
            leApplicable.
                GroupBy<LemmaExample, LemmaRule, double, KeyValuePair<LemmaRule, double>>(
                    le => le.Rule,
                    le => le.Weight,
                    (lr, enumDbl) => new KeyValuePair<LemmaRule, double>(lr, enumDbl.Aggregate((acc, curr) => acc + curr))
                ).
                OrderBy(kvpLrWght=>kvpLrWght.Value)
            );

            if (lBestRules.Count > 0)
                lrBestRule = lBestRules[0].Key;
            else {
                lrBestRule = elExamples.Rules.DefaultRule;

            }
		 */

        dWeight = 0;

        //calculate dWeight of whole node and calculates qualities for all rules
        HashMap<LemmaRule, Double> dictApplicableRules = new HashMap<LemmaRule,Double>();
        //dictApplicableRules.Add(elExamples.Rules.DefaultRule, 0);
        while (dictApplicableRules.size() == 0) {
            for (int iExm = iStart; iExm <= iEnd; iExm++) {
                LemmaRule lr = elExamples.getExampleAt(iExm).getRule();
                double dExmWeight = elExamples.getExampleAt(iExm).getWeight();
                dWeight += dExmWeight;

                if (lr.IsApplicableToGroup(sCondition.length())) {
                    if (dictApplicableRules.containsKey(lr))
                        dictApplicableRules.put(lr, dictApplicableRules.get(lr)+ dExmWeight);
                    else
                        dictApplicableRules.put(lr, dExmWeight);
                }
            }
            //if none found then increase condition length or add some default appliable rule
            if (dictApplicableRules.size() == 0) {
                if (this.sCondition.length() < iSimilarity)
                    this.sCondition = elExamples.getExampleAt(iStart).getWord().substring(elExamples.getExampleAt(iStart).getWord().length() - (sCondition.length()+1));
                else
                    //TODO preveri hevristiko, mogoce je bolje ce se doda default rule namesto rulea od starsa
                    dictApplicableRules.put(ltnParentNode.lrBestRule, 0.);
            }
        }

        //TODO can optimize this step using sorted list (dont add if it's worse than the worst)
        List<RuleWeighted> lSortedRules = new ArrayList<RuleWeighted>();
        for (Entry<LemmaRule, Double> kvp : dictApplicableRules.entrySet())
            lSortedRules.add(new RuleWeighted(kvp.getKey(), kvp.getValue() / dWeight));
        Collections.sort(lSortedRules);

        //keep just best iMaxRulesPerNode rules
        int iNumRules = lSortedRules.size();
        if (lsett.iMaxRulesPerNode > 0) iNumRules = Math.min(lSortedRules.size(), lsett.iMaxRulesPerNode);

        aBestRules = new RuleWeighted[iNumRules];
        for (int iRule = 0; iRule < iNumRules; iRule++) {
            aBestRules[iRule] = lSortedRules.get(iRule);
        }


        //set best rule
        lrBestRule = aBestRules[0].getRule();


        //TODO must check if this hevristics is OK (to privilige parent rule)
        if (ltnParentNode != null)
            for (int iRule = 0; iRule < lSortedRules.size() && lSortedRules.get(iRule).getWeight()==lSortedRules.get(0).getWeight(); iRule++) {
                if (lSortedRules.get(iRule).getRule() == ltnParentNode.lrBestRule) {
                    lrBestRule = lSortedRules.get(iRule).getRule();
                    break;
                }
            }

    }
    private void AddSubAll() {
        int iStartGroup = iStart;
        char chCharPrev = '\0';
        Boolean bSubGroupNeeded = false;
        for (int iWrd = iStart; iWrd <= iEnd; iWrd++) {
            String sWord = elExamples.getExampleAt(iWrd).getWord();

            char chCharThis = sWord.length() > iSimilarity ? sWord.charAt(sWord.length() - 1 - iSimilarity) : '\0';

            if (iWrd != iStart && chCharPrev != chCharThis) {
                if (bSubGroupNeeded) {
                    AddSub(iStartGroup, iWrd - 1, chCharPrev);
                    bSubGroupNeeded = false;
                }
                iStartGroup = iWrd;
            }

            //TODO check out bSubGroupNeeded when there are multiple posible rules (not just lrBestRule)
            if (elExamples.getExampleAt(iWrd).getRule() != lrBestRule)
                bSubGroupNeeded = true;

            chCharPrev = chCharThis;
        }
        if (bSubGroupNeeded && iStartGroup != iStart) AddSub(iStartGroup, iEnd, chCharPrev);
    }
    private void AddSub(int iStart, int iEnd, char chChar) {
        LemmaTreeNode ltnSub = new LemmaTreeNode(lsett, elExamples, iStart, iEnd, this);

        //TODO - maybe not realy appropriate because loosing statisitcs from multiple possible rules
        if (ltnSub.lrBestRule == lrBestRule && ltnSub.dictSubNodes == null) return;

        if (dictSubNodes == null) dictSubNodes = new HashMap<Character, LemmaTreeNode>();
        dictSubNodes.put(chChar, ltnSub);
    }

    public Boolean ConditionSatisfied(String sWord) {
        //if (bWholeWord)
        //    return sWord == sCondition;
        //else
        //    return sWord.EndsWith(sCondition);

        int iDiff = sWord.length() - sCondition.length();
        if (iDiff < 0 || (bWholeWord && iDiff > 0)) return false;

        int iWrdEnd = sCondition.length() - ltnParentNode.sCondition.length() - 1;
        for (int iChar = 0; iChar < iWrdEnd; iChar++)
            if (sCondition.charAt(iChar) != sWord.charAt(iChar + iDiff))
                return false;

        return true;
    }
    public String Lemmatize(String sWord) {
        if (sWord.length() >= iSimilarity && dictSubNodes != null) {
            char chChar = sWord.length() > iSimilarity ? sWord.charAt(sWord.length() - 1 -iSimilarity) : '\0';
            if (dictSubNodes.containsKey(chChar) && dictSubNodes.get(chChar).ConditionSatisfied(sWord))
                return dictSubNodes.get(chChar).Lemmatize(sWord);
        }

        return lrBestRule.Lemmatize(sWord);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ToString(sb, 0);
        return sb.toString();
    }
    private void ToString(StringBuilder sb, int iLevel) {
        sb.append(new String("\t"+ iLevel));
        sb.append("Suffix=\"" + (bWholeWord?"^":"") + sCondition + "\"; ");
        sb.append("Rule=\"" + lrBestRule.toString() + "\"; ");
        sb.append("Weight=" + dWeight + "\"; ");
        if (aBestRules != null && aBestRules.length>0) sb.append("Cover=" + aBestRules[0].getWeight() + "; ");
        sb.append("Rulles=");
        if (aBestRules != null)
            for (RuleWeighted rw : aBestRules)
                sb.append(" " + rw.toString());
        sb.append("; ");

        sb.append(System.getProperty("line.separator"));

        if (dictSubNodes != null)
            for (LemmaTreeNode ltnChild : dictSubNodes.values())
                ltnChild.ToString(sb, iLevel + 1);
    }

    /* If we would do manual serialization ...
    public void serialize() {


        ArrayList<LemmaTreeNode> allSubNodes = new ArrayList<LemmaTreeNode>();

        ArrayList<LemmaTreeNode> curNodes = new ArrayList<LemmaTreeNode>();
        curNodes.addAll(dictSubNodes.values());
        while (!curNodes.isEmpty()) {
            LemmaTreeNode curNode = curNodes.remove(0);
            //drop examples
            curNode.elExamples = null;
            allSubNodes.add(curNode);
            if (curNode.dictSubNodes != null) {
                curNodes.addAll(curNode.dictSubNodes.values());
            }
        }


        System.out.println("Number of all nodes: " + allSubNodes.size());

    } */

    public void DropExamples() {
        ArrayList<LemmaTreeNode> curNodes = new ArrayList<LemmaTreeNode>();
        curNodes.addAll(dictSubNodes.values());
        while (!curNodes.isEmpty()) {
            LemmaTreeNode curNode = curNodes.remove(0);
            //drop examples
            curNode.elExamples = null;
            if (curNode.dictSubNodes != null) {
                curNodes.addAll(curNode.dictSubNodes.values());
            }
        }
    }
}
