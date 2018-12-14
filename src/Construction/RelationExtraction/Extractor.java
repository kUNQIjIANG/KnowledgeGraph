package Construction.RelationExtraction;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.model.crf.CRFNERecognizer;

import java.util.ArrayList;
import java.util.HashMap;

public class Extractor {

    private static String subvobj(String input){
        String relation = "";
        CoNLLSentence sentence = HanLP.parseDependency(input);
        System.out.println(sentence);
        for (CoNLLWord word : sentence){
            if (word.CPOSTAG.equals(""))
            if (word.DEPREL.equals("主谓关系")){
                String part = word.LEMMA + "," + word.HEAD.LEMMA + ",";
                relation += part;
            }
            else if (word.DEPREL.equals("动宾关系")) {
                relation += word.LEMMA;
            }
        }
        return "("+relation+")";

    }



    private static ArrayList<HashMap> getChildrenDict (CoNLLSentence sentence){
        ArrayList<HashMap> childDict = new ArrayList<>();
        for (int i = 0; i < sentence.word.length; i++){
            CoNLLWord curWord = sentence.word[i];
            HashMap<String,ArrayList<Integer>> children = new HashMap<>();
            for (CoNLLWord word : sentence){
                if (word.HEAD.equals(curWord)){
                    System.out.println("true");
                    if (children.containsKey(word.DEPREL)){
                        children.get(word.DEPREL).add(word.ID);
                    }else{
                        ArrayList<Integer> child = new ArrayList<>();
                        child.add(word.ID);
                        children.put(word.DEPREL,child);
                    }
                }
            }
            childDict.add(children);
        }
        return childDict;
    }

    private static String trackEntity(CoNLLSentence sentence, ArrayList<HashMap> childrenDict, int wordId){
        if (!childrenDict.get(wordId).isEmpty()) {
            HashMap<String, ArrayList<Integer>> childDict = childrenDict.get(wordId);
            String prefix = "";
            if (childDict.containsKey("定中关系")) {
                for (int i : childDict.get("定中关系")) {
                    prefix += trackEntity(sentence, childrenDict, i);
                }
            }

            String postfix = "";
            if (sentence.word[wordId].CPOSTAG.equals("v")) {
                if (childDict.containsKey("动宾关系")) {
                    postfix += trackEntity(sentence, childrenDict, childDict.get("动宾关系").get(0));
                }
                if (childDict.containsKey("主谓关系")) {
                    postfix = trackEntity(sentence, childrenDict, childDict.get("主谓关系").get(0)) + postfix;
                }
            }

            return prefix + sentence.word[wordId].LEMMA + postfix;
        }
        return sentence.word[wordId].LEMMA;
    }

    private static void SVO(CoNLLSentence sentence, ArrayList<HashMap> childrenDict){
        for (int i = 0; i < sentence.word.length; i++){
            CoNLLWord curWord = sentence.word[i];
            if (curWord.CPOSTAG.equals("v")){
                HashMap<String,ArrayList<Integer>> childDict = childrenDict.get(i);
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
                        if (temp.equals(e1.substring(0,temp.length()))){
                            e1 = e1.substring(temp.length());
                        }
                        if (!e1.contains(temp)){
                            System.out.printf("定语后置动宾关系\t(%s,%s,%s)\n", e1,relation,e2);
                        }
                    }
                }

                if (childDict.containsKey("主谓关系") && childDict.containsKey("动补结构")){
                    String e1 = trackEntity(sentence, childrenDict, childDict.get("主谓关系").get(0));
                    int cmp_index = childDict.get("动补结构").get(0);
                    String relation = curWord.LEMMA + sentence.word[cmp_index].LEMMA;
                    if (childrenDict.get(cmp_index).containsKey("介宾关系")){
                        HashMap<String,ArrayList<Integer>> pos = childrenDict.get(cmp_index);
                        String e2 = trackEntity(sentence,childrenDict,pos.get("介宾关系").get(0));
                        System.out.printf("介宾关系主谓动补\t(%s,%s,%s)\n",e1,relation,e2);
                    }

                }
            }
        }

    }


    public static void main(String[] args) {
        //String test = "徐先生还具体帮助他确定了把画雄鹰、松鼠和麻雀作为主攻目标。";
        String test = "城建成为外商投资新热点，莫言是第一个诺贝尔文学奖作家。";
        //String test = "管理局发现该企业生产存在记录造假";
        //等严重违反《药品生产质量管理规范》行为。
        CoNLLSentence sent = HanLP.parseDependency(test);
        ArrayList<HashMap> childDict = getChildrenDict(sent);
        SVO(sent,childDict);

        for ( int i = 0; i < childDict.size();  i++){
            System.out.println(childDict.get(i));
        }
        String[] test_set = test.split("，");
        for (String s : test_set){
            System.out.println(s);
            //System.out.println(subvobj(s));

        }


        //String[] word_list = new String[sentence.word.length];
        //String[] pos_list = new String[sentence.word.length];

        try {
            CRFNERecognizer ner = new CRFNERecognizer();
            //System.out.println(ner.recognize(word_list,pos_list));
        } catch (Exception e){
            System.out.println("exp");
        }
    }

}
