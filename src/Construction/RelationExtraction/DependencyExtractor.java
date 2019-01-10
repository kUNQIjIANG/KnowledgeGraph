package Construction.RelationExtraction;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletionService;

class DependencyExtractor extends Extractor {

    private String trackEntity(CoNLLSentence sentence, ArrayList<HashMap> childrenDict, int wordId){

        if (sentence.word[wordId-1].LEMMA.equals("他") || sentence.word[wordId-1].LEMMA.equals("她")){
            int person = personPronounResolution(sentence,wordId);
            return trackEntity(sentence, childrenDict, person);
        }

        if (!childrenDict.get(wordId-1).isEmpty()) {

            String origin = sentence.word[wordId-1].LEMMA;

            HashMap<String, ArrayList<Integer>> childDict = childrenDict.get(wordId-1);
            StringBuffer prefix = new StringBuffer("");
            if (childDict.containsKey("定中关系")) {
                for (int i : childDict.get("定中关系")) {
                    prefix.append(trackEntity(sentence, childrenDict, i));
                    // 并列 医学，免疫学，病原生物学
                    HashMap<String, ArrayList<Integer>> dChildDict = childrenDict.get(i-1);
                    if (dChildDict.containsKey("并列关系")){
                        for (int j : dChildDict.get("并列关系")){
                            prefix.append(trackEntity(sentence,childrenDict,j));
                        }
                    }
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
                if (childDict.containsKey("右附加关系")) {
                    // 国务院 批准 的
                    int d = childDict.get("右附加关系").get(0);
                    String de = sentence.word[d-1].LEMMA;
                    origin = origin + de;
                }
            }

            if (prefix.length() == 0) return postfix + origin;
            else return prefix + origin + postfix;
        }
        return sentence.word[wordId-1].LEMMA;
    }

    private String trackRelation(CoNLLSentence sentence, ArrayList<HashMap> childrenDict, int wordId){
        // 批准 -> 被人事部批准为
        if (!childrenDict.get(wordId-1).isEmpty()) {
            HashMap<String, ArrayList<Integer>> childDict = childrenDict.get(wordId-1);
            StringBuffer prefix = new StringBuffer("");
            if (childDict.containsKey("状中结构")){
                int pId = getNearest(childDict.get("状中结构"),wordId);
                //int pId = childDict.get("状中结构").get(0);
                String p = sentence.word[pId-1].LEMMA;
                prefix.append(p);

                HashMap<String,ArrayList<Integer>> pChildDict = childrenDict.get(pId-1);
                if (pChildDict.containsKey("介宾关系")){
                    int nId = pChildDict.get("介宾关系").get(0);
                    String n = trackEntity(sentence, childrenDict, nId);
                    //String n = sentence.word[nId-1].LEMMA;
                    prefix.append(n);
                }
            }

            String postfix = "";
            if (childDict.containsKey("动补结构")){
                int dId = childDict.get("动补结构").get(0);
                String d = sentence.word[dId-1].LEMMA;
                postfix = d;
            }
            return prefix + sentence.word[wordId-1].LEMMA + postfix;
        }
        return sentence.word[wordId-1].LEMMA;
    }

    static double entityPairScore(int e1_pos, int r_pos, int e2_pos){
        assert e2_pos > e1_pos : "论元一应该在论元二之前。";
        return 1.0/(e1_pos + e2_pos) + 1.0/(e1_pos - r_pos) + 1.0/(e2_pos - r_pos + 1);
    }


    void SVO(CoNLLSentence sentence, ArrayList<HashMap> childrenDict){
        for (int i = 0; i < sentence.word.length; i++){
            CoNLLWord curWord = sentence.word[i];
            HashMap<String,ArrayList<Integer>> childDict = childrenDict.get(i);
            if (curWord.CPOSTAG.equals("v")){
                System.out.println("vword: " + curWord.LEMMA);
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

                    int id = getNearest(childDict.get("主谓关系"),curWord.ID);
                    String e1 = trackEntity(sentence, childrenDict, id);
                    //String relation = curWord.LEMMA;
                    String relation = trackRelation(sentence, childrenDict, curWord.ID);
                    String e2 = trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                    System.out.printf("主谓宾关系\t(%s,%s,%s)\n",e1,relation,e2);
                    int sub = childDict.get("主谓关系").get(childDict.get("主谓关系").size()-1);
                    HashMap<String,ArrayList<Integer>> subChild = childrenDict.get(sub-1);
                    if (subChild.containsKey("并列关系")){
                        for ( int c : subChild.get("并列关系")) {
                            String coo = trackEntity(sentence, childrenDict, c);
                            System.out.printf("并列主语\t(%s,%s,%s)\n", coo, relation, e2);
                        }
                    }

                    int ob = childDict.get("动宾关系").get(0);
                    HashMap<String,ArrayList<Integer>> obChild = childrenDict.get(ob-1);
                    if (obChild.containsKey("并列关系")){
                        for ( int c : obChild.get("并列关系")) {
                            String coo = trackEntity(sentence, childrenDict, c);
                            System.out.printf("并列宾语\t(%s,%s,%s)\n", e1, relation, coo);
                        }
                    }

                    /*
                    if (childDict.containsKey("并列关系")){
                        for (int v : childDict.get("并列关系")){
                            String cooRel = trackRelation(sentence,childrenDict,v);
                            System.out.printf("并列谓语\t(%s,%s,%s)\n",e1,cooRel,e2);
                        }
                    }
                    */
                }

                else if (curWord.DEPREL.equals("并列关系") && curWord.HEAD.DEPREL.equals("核心关系")){
                    HashMap<String,ArrayList<Integer>> headChildDict = childrenDict.get(curWord.HEAD.ID-1);
                    // 相同主语，不同宾语
                    if (headChildDict.containsKey("主谓关系") && childDict.containsKey("动宾关系")){
                        int id = getNearest(headChildDict.get("主谓关系"),curWord.HEAD.ID);
                        String e1 = trackEntity(sentence, childrenDict, id);
                        String relation = curWord.LEMMA;
                        String e2 = trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                        System.out.printf("并列谓语2\t(%s,%s,%s)\n",e1,relation,e2);
                    } else if ( headChildDict.containsKey("主谓关系") && childDict.containsKey("动补结构")) {

                        int d = childDict.get("动补结构").get(0);
                        // "为” -> 被人事部批准为,企业博士后科研工作站
                        HashMap<String,ArrayList<Integer>> dChildDict = childrenDict.get(d-1);
                        if (dChildDict.containsKey("动宾关系")){
                            String e1 = trackEntity(sentence, childrenDict, headChildDict.get("主谓关系").get(0));
                            String cooRel = trackRelation(sentence,childrenDict,curWord.ID);
                            String e2 = trackEntity(sentence,childrenDict,dChildDict.get("动宾关系").get(0));
                            System.out.printf("被动补关系\t(%s,%s,%s)\n",e1,cooRel,e2);
                        }
                    }

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
            else if (curWord.DEPREL.equals("主谓关系") || curWord.DEPREL.equals("动宾关系")){
                // 德国总统高克 -> ns, n, nr
                System.out.println("zword: " + curWord.LEMMA);
                if (childDict.containsKey("定中关系")){
                    int child = childDict.get("定中关系").get(0);
                    if ( sentence.word[child-1].POSTAG.charAt(0) == 'n') {
                        HashMap<String, ArrayList<Integer>> grandchildDict = childrenDict.get(child - 1);
                        if (grandchildDict.containsKey("定中关系")) {
                            if (sentence.word[grandchildDict.get("定中关系").get(0)-1].POSTAG.charAt(0) == 'n') {
                                String e2 = curWord.LEMMA;
                                String relation = sentence.word[child - 1].LEMMA;
                                String e1 = sentence.word[grandchildDict.get("定中关系").get(0) - 1].LEMMA;
                                System.out.printf("定中关系属性值\t(%s,%s,%s)\n", e1, relation, e2);
                            }
                        }
                    }
                }
            }
        }
    }
}
