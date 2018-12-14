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

    // test git
    private static ArrayList<HashMap> getChildrenDict (CoNLLSentence sentence){
        ArrayList<HashMap> childDict = new ArrayList<>();
        for (int i = 0; i < sentence.word.length; i++){
            CoNLLWord curWord = sentence.word[i];
            HashMap<String,ArrayList<Integer>> children = new HashMap<>();
            for (CoNLLWord word : sentence){
                if (word.HEAD.equals(curWord)){
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

    private static void SVO(CoNLLSentence sentence, ArrayList<HashMap> childrenDict){
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

    public static void main(String[] args) {
        //String test = "徐先生还具体帮助李红确定了把画雄鹰、松鼠和麻雀作为主攻目标。";
        //String test = "没有用";
        //String test = "热门报道 金融界 朱邦凌:疫苗生产记录造假的背后是对生命的漠视2016年山东疫苗事件主要是疫苗在流通环节存在漏洞，" +
        //              "而诺贝尔奖生产记录造假说明生产环节更加重要。疫苗生产关系重大，关系到无数人的生命安全。";
        //String test = "莫言是第一个获得诺贝尔文学奖的中国作家。";
        //String test = "城建成为外商投资新热点，莫言是第一个诺贝尔文学奖作家。";
        //String test = "管理局发现该企业造假";
        //等严重违反《药品生产质量管理规范》行为。
        String test = "本所是国家教育部批准的硕士学位授权单位，医学免疫学、病原生物学授权点。迄今为止，共培养硕士研究生66人。2001年度被人事部批准为企业博士后科研工作站。由长春生物所主办的《中国生物制品学杂志》是国家一级专业性杂志，已刊载论文数千篇，被评为中华预防医学会优秀期刊，1999年改为国际版大十六开，有关信息被美国医学会检索系统收录。自1982年国家对药品生产实行批准文号制度以来，本所先后获得各类生物制品生产批件115个，产品种类涵盖： 疫苗、类毒素、抗毒素、免疫血清、血液制品、细胞因子、单克隆抗体、免疫学诊断试剂等诸多类型从1992年起，本所工业总产值首次突破亿元大关，且每年以15-20%的速度递增。产品销售网络遍及全国29个省市自治区。1993年，所组建成立了进出口公司、 获得进出口经营权，　先后有十几种产品出口韩国日本越南、印度、美国、巴基斯坦、加拿大等国家，每年出口创汇额达 100万美元以上。";

        String[] test_set = test.split("，");
        for (String s : test_set){
            System.out.println(s);
            CoNLLSentence sent = HanLP.parseDependency(s);
            //System.out.println(sent);
            ArrayList<HashMap> childrenDict = getChildrenDict(sent);
            /*
            for ( int i = 0; i < childrenDict.size();  i++) {
                System.out.print(sent.word[i].LEMMA);
                System.out.println(childrenDict.get(i));
            }
            */
            System.out.println();
            SVO(sent,childrenDict);
            System.out.println();

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
