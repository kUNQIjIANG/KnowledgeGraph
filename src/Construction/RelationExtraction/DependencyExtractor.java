package Construction.RelationExtraction;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.ArrayList;
import java.util.HashMap;

public class DependencyExtractor extends Extractor {

    private static String trackEntity(CoNLLSentence sentence, ArrayList<HashMap> childrenDict, int wordId){
        if (!childrenDict.get(wordId-1).isEmpty()) {
            HashMap<String, ArrayList<Integer>> childDict = childrenDict.get(wordId-1);
            String prefix = "";
            if (childDict.containsKey("定中关系")) {
                for (int i : childDict.get("定中关系")) {
                    prefix += trackEntity(sentence, childrenDict, i);
                }
            }

            String postfix = "";
            if (sentence.word[wordId-1].CPOSTAG.equals("v")) {
                if (childDict.containsKey("动宾关系")) {
                    postfix += trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                }
                if (childDict.containsKey("主谓关系")) {
                    postfix = trackEntity(sentence, childrenDict, childDict.get("主谓关系").get(0)) + postfix;
                }
            }

            return prefix + sentence.word[wordId-1].LEMMA + postfix;
        }
        return sentence.word[wordId-1].LEMMA;
    }

    public static double entityPairScore(int e1_pos, int r_pos, int e2_pos){
        assert e2_pos > e1_pos : "论元一应该在论元二之前。";
        return 1.0/(e1_pos + e2_pos) + 1.0/(e1_pos - r_pos) + 1.0/(e2_pos - r_pos + 1);
    }

    void SVO(CoNLLSentence sentence, ArrayList<HashMap> childrenDict){
        for (int i = 0; i < sentence.word.length; i++){
            CoNLLWord curWord = sentence.word[i];
            if (curWord.CPOSTAG.equals("v")){
                HashMap<String,ArrayList<Integer>> childDict = childrenDict.get(i);

                if (childDict.containsKey("主谓关系") && childDict.containsKey("动宾关系")){
                    double score = -Double.MAX_VALUE;
                    String e1 = "";
                    String relation = "";
                    String e2 = "";
                    for (int pe1 : childDict.get("主谓关系")){
                        for (int pe2 : childDict.get("动宾关系")){
                            double new_score = entityPairScore(pe1,i+1,pe2);
                            System.out.println(new_score);
                            if (new_score > score){
                                e1 = sentence.word[pe1-1].LEMMA;
                                relation = curWord.LEMMA;
                                e2 = sentence.word[pe2-1].LEMMA;
                            }
                        }
                    }
                    System.out.printf("最大分主谓关系\t(%s,%s,%s)\n",e1,relation,e2);
                }

                if (childDict.containsKey("主谓关系") && childDict.containsKey("动宾关系")){
                    String e1 = trackEntity(sentence, childrenDict, childDict.get("主谓关系").get(0));
                    String relation = curWord.LEMMA;
                    String e2 = trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                    System.out.printf("主谓宾关系\t(%s,%s,%s)\n",e1,relation,e2);
                }

                if (curWord.DEPREL.equals("定中关系")){
                    if (childDict.containsKey("动宾关系")){
                        String e1 = trackEntity(sentence, childrenDict, curWord.HEAD.ID-1); //？
                        String relation = curWord.LEMMA;
                        String e2 = trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                        String temp = relation + e2;
                        if (e1.length() > temp.length()){
                            if (temp.equals(e1.substring(0, temp.length()))) {
                                e1 = e1.substring(temp.length());
                            }
                        }
                        if (!e1.contains(temp)){
                            System.out.printf("定语后置动宾关系\t(%s,%s,%s)\n", e1,relation,e2);
                        }
                    }
                }

                if (childDict.containsKey("主谓关系") && childDict.containsKey("动补结构")){
                    String e1 = trackEntity(sentence, childrenDict, childDict.get("主谓关系").get(0));
                    int cmp_index = childDict.get("动补结构").get(0);
                    String relation = curWord.LEMMA + sentence.word[cmp_index-1].LEMMA; //做 完
                    if (childrenDict.get(cmp_index-1).containsKey("介宾关系")){
                        HashMap<String,ArrayList<Integer>> pos = childrenDict.get(cmp_index-1);
                        String e2 = trackEntity(sentence,childrenDict,pos.get("介宾关系").get(0));
                        System.out.printf("介宾关系主谓动补\t(%s,%s,%s)\n",e1,relation,e2);
                    }
                }
            }
        }
    }
}
