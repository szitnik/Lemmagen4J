package si.zitnik.research.lemmagen.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import si.zitnik.research.lemmagen.abst.ILemmatizerModel;
import si.zitnik.research.lemmagen.abst.ITrainableLemmatizer;

/**
 * This implementation has been recoded from C# to Java
 * on 18th October 2010 by Slavko Zitnik.
 *
 * slavko.zitnik@fri.uni-lj.si
 *
 * @author slavkoz
 *
 */
public class Lemmatizer extends ITrainableLemmatizer {
    private static final long serialVersionUID = -4627649140108882172L;

    protected LemmatizerSettings lsett;
    protected ExampleList elExamples;
    protected LemmaTreeNode ltnRootNode;
    protected LemmaTreeNode ltnRootNodeFront;

    public Lemmatizer() {
        this(new LemmatizerSettings());
    }
    public Lemmatizer(LemmatizerSettings lsett) {
        this.lsett = lsett;
        this.elExamples = new ExampleList(lsett);
        this.ltnRootNode = null;
        this.ltnRootNodeFront = null;
    }

    public Lemmatizer(BufferedReader srIn, String sFormat, LemmatizerSettings lsett) throws NumberFormatException, IOException {
        this(lsett);
        AddMultextFile(srIn, sFormat);
    }


    private LemmaTreeNode getltrRootNodeSafe() {
        if (ltnRootNode == null) BuildModel();
        return ltnRootNode;
    }
    private LemmaTreeNode getltrRootNodeFrontSafe() {
        if (ltnRootNodeFront == null && lsett.bBuildFrontLemmatizer) BuildModel();
        return ltnRootNodeFront;
    }

    public LemmatizerSettings getSettings() {
        return lsett.CloneDeep();
    }
    public ExampleList getExamples() {
        return elExamples;
    }
    public RuleList getRules() {
        return elExamples.getRules();
    }
    public LemmaTreeNode getRootNode() {
        return getltrRootNodeSafe();
    }
    public LemmaTreeNode getRootNodeFront() {
        return getltrRootNodeFrontSafe();
    }
    public ILemmatizerModel getModel() {
        return getltrRootNodeSafe();
    }

    public void AddMultextFile(BufferedReader srIn, String sFormat) throws NumberFormatException, IOException {
        this.elExamples.AddMultextFile(srIn, sFormat);
        ltnRootNode = null;
    }
    public void AddExample(String sWord, String sLemma) {
        AddExample(sWord, sLemma, 1, null);
    }
    public void AddExample(String sWord, String sLemma, double dWeight) {
        AddExample(sWord, sLemma, dWeight, null);
    }
    public void AddExample(String sWord, String sLemma, double dWeight, String sMsd) {
        elExamples.AddExample(sWord, sLemma, dWeight, sMsd);
        ltnRootNode = null;
    }

    public void DropExamples() {
        //elExamples.DropExamples();

        //Dropping all
        elExamples = null;
        ltnRootNode.DropExamples();
        if (ltnRootNodeFront != null) {
            ltnRootNodeFront.DropExamples();
        }
    }
    public void FinalizeAdditions() {
        elExamples.FinalizeAdditions();
    }


    public void BuildModel() {
        if (ltnRootNode != null) return;

        if (!lsett.bBuildFrontLemmatizer) {
            //TODO remove: elExamples.FinalizeAdditions();
            elExamples.FinalizeAdditions();
            ltnRootNode = new LemmaTreeNode(lsett, elExamples);
        }
        else {
            ltnRootNode = new LemmaTreeNode(lsett, elExamples.GetFrontRearExampleList(false));
            ltnRootNodeFront = new LemmaTreeNode(lsett, elExamples.GetFrontRearExampleList(true));
        }
    }

    public String Lemmatize(String sWord) {
        if (!lsett.bBuildFrontLemmatizer)
            return getltrRootNodeSafe().Lemmatize(sWord);
        else {
            String sWordFront = LemmaExample.StringReverse(sWord);
            String sLemmaFront = getltrRootNodeFrontSafe().Lemmatize(sWordFront);
            String sWordRear = LemmaExample.StringReverse(sLemmaFront);
            return getltrRootNodeSafe().Lemmatize(sWordRear);
        }
    }

    /* If we would do manual serialization ...
    public void serialize() {
        //protected LemmatizerSettings lsett;
        System.out.println(lsett);

        //protected ExampleList elExamples;
        //nothing to do, need to be empty

        //protected LemmaTreeNode ltnRootNode;
        ltnRootNode.serialize();

        //protected LemmaTreeNode ltnRootNodeFront;
        if (ltnRootNodeFront != null) {
            ltnRootNodeFront.serialize();
        }
    }*/
}
