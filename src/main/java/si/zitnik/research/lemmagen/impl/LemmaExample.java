package si.zitnik.research.lemmagen.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;


public class LemmaExample implements Comparable<LemmaExample>, Serializable {
    private static final long serialVersionUID = 2661374883177703452L;

    private String sWord;
    private String sLemma;
    private String sSignature;
    private String sMsd;
    private double dWeight;
    private LemmaRule lrRule;
    private LemmatizerSettings lsett;

    private String sWordRearCache;
    private String sWordFrontCache;
    private String sLemmaFrontCache;

    public LemmaExample(String sWord, String sLemma, double dWeight, String sMsd, RuleList rlRules, LemmatizerSettings lsett) {
        this.lsett = lsett;

        this.sWord = sWord;
        this.sLemma = sLemma;
        this.sMsd = sMsd;
        this.dWeight = dWeight;
        this.lrRule = rlRules.AddRule(this);

        switch (lsett.eMsdConsider) {
            case Ignore:
            case JoinAll:
            case JoinDistinct:
            case JoinSameSubstring:
                sSignature = "[" + sWord + "]==>[" + sLemma + "]";
                break;
            case Distinct:
            default:
                sSignature = "[" + sWord + "]==>[" + sLemma + "](" + (sMsd != null ? sMsd : "") + ")";
                break;
        }

        this.sWordRearCache = null;
        this.sWordFrontCache = null;
        this.sLemmaFrontCache = null;

    }

    public String getWord() {
        return sWord;
    }
    public String getLemma() {
        return sLemma;
    }
    public String getMsd() {
        return sMsd;
    }
    public String getSignature() {
        return sSignature;
    }
    public double getWeight() {
        return dWeight;
    }
    public LemmaRule getRule() {
        return lrRule;
    }

    /// <summary>
    /// Word to be pre-lemmatized with Front-Lemmatizer into LemmaFront which is then lemmatized by standard Rear-Lemmatizer (Warning it is reversed)
    /// </summary>
    public String getWordFront() {
        if (sWordFrontCache == null)
            sWordFrontCache = StringReverse(sWord);
        return sWordFrontCache;
    }
    /// <summary>
    /// Lemma to be produced by pre-lemmatizing with Front-Lemmatizer (Warning it is reversed)
    /// </summary>
    public String getLemmaFront() {
        if (sLemmaFrontCache == null)
            sLemmaFrontCache = StringReverse(getWordRear());
        return sLemmaFrontCache;
    }
    /// <summary>
    /// word to be lemmatized by standard Rear-Lemmatizer (it's beggining has been already modified by Front-Lemmatizer)
    /// </summary>
    public String getWordRear() {
        if (sWordRearCache == null) {
            LCSREsult lcsResult = LongestCommonSubString(sWord, sLemma);
            String common = lcsResult.longestCommonSubString;
            int wordPos = lcsResult.iPosInStr1;
            int lemmaPos = lcsResult.iPosInStr2;
            sWordRearCache = lemmaPos == -1 ? sLemma : (sLemma.substring(0, lemmaPos + common.length()) + sWord.substring(wordPos + common.length()));
        }
        return sWordRearCache;
    }
    /// <summary>
    /// lemma to be produced by standard Rear-Lemmatizer from WordRear
    /// </summary>
    public String getLemmaRear() {
        return sLemma;
    }

    //TODO - this function is not totaly ok because sMsd should not be changed since it could be included in signature
    public void Join(LemmaExample leJoin) {
        dWeight += leJoin.dWeight;

        if (sMsd != null)
            switch (lsett.eMsdConsider) {
                case Ignore:
                    sMsd = null;
                    break;
                case Distinct:
                    break;
                case JoinAll:
                    sMsd += "|" + leJoin.sMsd;
                    break;
                case JoinDistinct:
                    HashSet<String> tempSet = new HashSet<String>(
                            Arrays.asList(sMsd.split("\\|"))
                    );
                    if (!tempSet.contains(leJoin.sMsd))
                        sMsd += "|" + leJoin.sMsd;
                    break;
                case JoinSameSubstring:
                    int iPos = 0;
                    int iMax = Math.min(sMsd.length(), leJoin.sMsd.length());
                    while (iPos < iMax && sMsd.charAt(iPos) == leJoin.sMsd.charAt(iPos)) iPos++;
                    sMsd = sMsd.substring(0, iPos);
                    break;
                default:
                    break;
            }

    }



    public int Similarity(LemmaExample le) {
        return Similarity(this, le);
    }
    public static int Similarity(LemmaExample le1, LemmaExample le2) {
        String sWord1 = le1.sWord;
        String sWord2 = le2.sWord;
        int iLen1 = sWord1.length();
        int iLen2 = sWord2.length();
        int iMaxLen = Math.min(iLen1, iLen2);

        for (int iPos = 1; iPos <= iMaxLen; iPos++)
            if (sWord1.charAt(iLen1 - iPos) != sWord2.charAt(iLen2 - iPos)) return iPos - 1;

        //TODO similarity should be bigger if two words are totaly equal
        //if (sWord1 == sWord2)
        //    return iMaxLen + 1;
        //else
        return iMaxLen;
    }


    /// <summary>
    /// Function used to comprare current MultextExample (ME) against argument ME.
    /// Mainly used in for sorting lists of MEs.
    /// </summary>
    /// <param name="other"> MultextExample (ME) that we compare current ME against.</param>
    /// <returns>1 if current ME is bigger, -1 if smaler and 0 if both are the same.</returns>
    @Override
    public int compareTo(LemmaExample other) {
        int iComparison;

        iComparison = CompareStrings(this.sWord, other.sWord, false);
        if (iComparison != 0) return iComparison;

        iComparison = CompareStrings(this.sLemma, other.sLemma, true);
        if (iComparison != 0) return iComparison;

        if (lsett.eMsdConsider == MsdConsideration.Distinct &&
                this.sMsd != null && other.sMsd != null) {
            iComparison = CompareStrings(this.sMsd, other.sMsd, true);
            if (iComparison != 0) return iComparison;
        }

        return 0;
    }
    public int Compare(LemmaExample x, LemmaExample y) {
        return x.compareTo(y);
    }
    public static int CompareStrings(String sStr1, String sStr2, Boolean bForward) {
        int iLen1 = sStr1.length();
        int iLen2 = sStr2.length();
        int iMaxLen = Math.min(iLen1, iLen2);

        if (bForward)
            for (int iPos = 0; iPos < iMaxLen; iPos++) {
                if (sStr1.charAt(iPos) > sStr2.charAt(iPos)) return 1;
                if (sStr1.charAt(iPos) < sStr2.charAt(iPos)) return -1;
            }
        else
            for (int iPos = 1; iPos <= iMaxLen; iPos++) {
                if (sStr1.charAt(iLen1 - iPos) > sStr2.charAt(iLen2 - iPos)) return 1;
                if (sStr1.charAt(iLen1 - iPos) < sStr2.charAt(iLen2 - iPos)) return -1;
            }

        if (iLen1 > iLen2) return 1;
        if (iLen1 < iLen2) return -1;
        return 0;
    }
    public static int EqualPrifixLen(String sStr1, String sStr2) {
        int iLen1 = sStr1.length();
        int iLen2 = sStr2.length();
        int iMaxLen = Math.min(iLen1, iLen2);

        for (int iPos = 0; iPos < iMaxLen; iPos++)
            if (sStr1.charAt(iPos) != sStr2.charAt(iPos)) return iPos;

        return iMaxLen;
    }

    public static LCSREsult LongestCommonSubString(String sStr1, String sStr2) {
        int[][] l = new int[sStr1.length() + 1][sStr2.length() + 1];
        int z = 0;
        String ret = "";
        int iPosInStr1 = -1;
        int iPosInStr2 = -1;

        for (int i = 0; i < sStr1.length(); i++)
            for (int j = 0; j < sStr2.length(); j++)
                if (sStr1.charAt(i) == sStr2.charAt(j)) {
                    if (i == 0 || j == 0) l[i][j] = 1;
                    else l[i][j] = l[i - 1][j - 1] + 1;
                    if (l[i][j] > z) {
                        z = l[i][j];
                        iPosInStr1 = i - z + 1;
                        iPosInStr2 = j - z + 1;
                        ret = sStr1.substring(i - z + 1, i - z + 1 + z);
                    }
                }

        return new LCSREsult(ret, iPosInStr1, iPosInStr2);
    }
    public static String StringReverse(String s) {
        if (s == null) return null;
        char[] charArray = s.toCharArray();
        int len = s.length() - 1;

        for (int i = 0; i < len; i++, len--) {
            charArray[i] ^= charArray[len];
            charArray[len] ^= charArray[i];
            charArray[i] ^= charArray[len];
        }

        return new String(charArray);
    }


    @Override
    public String toString() {
        String sThis =
                (sWord == null ? "" : "W:\"" + sWord + "\" ") +
                        (sLemma == null ? "" : "L:\"" + sLemma + "\" ") +
                        (sMsd == null ? "" : "M:\"" + sMsd + "\" ") +
                        (Double.isNaN(dWeight) ? "" : "F:\"" + dWeight + "\" ") +
                        (lrRule == null ? "" : "R:" + lrRule.toString() + " ");

        return (sThis == null || sThis.isEmpty()) ? "" : sThis.substring(0, sThis.length() - 1);
    }


}

class LCSREsult {
    public String longestCommonSubString;
    public int iPosInStr1;
    public int iPosInStr2;

    public LCSREsult(String longestCommonSubString, int iPosInStr1,	int iPosInStr2) {
        this.longestCommonSubString = longestCommonSubString;
        this.iPosInStr1 = iPosInStr1;
        this.iPosInStr2 = iPosInStr2;
    }
}

