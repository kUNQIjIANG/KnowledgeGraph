package Construction.RelationExtraction;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.document.sentence.word.Word;
import com.hankcs.hanlp.model.crf.CRFNERecognizer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.HanLP.Config;
import com.hankcs.hanlp.model.perceptron.PerceptronNERecognizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Test {
    public static void main(String[] args) throws IOException {
        String SENTENCE = "莫言是第一个获得诺贝尔文学奖的中国籍作家。";

        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer("E:/hanLP/data/model/perceptron/pku199801/cws.bin",
                                                                            Config.PerceptronPOSModelPath,
                                                                            Config.PerceptronNERModelPath);
        PerceptronNERecognizer recognizer = new PerceptronNERecognizer(Config.PerceptronNERModelPath);

        //Sentence sentence = analyzer.analyze(SENTENCE);

        //String[] sentence2 = recognizer.recognize("莫言 是 第一 个 获得 诺贝尔文学奖 的 中国 籍作家 。".split(" "), "d v m q v nz u ns n w".split(" "));
        //System.out.println(sentence);
        //System.out.println("1: "+ Arrays.toString(sentence2));
        //for (String s : sentence2){
            //System.out.println(s);
        //}
        System.out.println();

        //String test = "徐先生还具体帮助李红确定了把画雄鹰、松鼠和麻雀作为主攻目标。";
        //String test = "热门报道 金融界 朱邦凌:疫苗生产记录造假的背后是对生命的漠视2016年山东疫苗事件主要是疫苗在流通环节存在漏洞，" +
        //              "而诺贝尔奖生产记录造假说明生产环节更加重要。疫苗生产关系重大，关系到无数人的生命安全。";
        //String test = "成龙是第一个获得诺贝尔文学奖的中国籍作家。";
        //String test = "城建成为外商投资新热点，莫言是第一个诺贝尔文学奖作家。";
        //String test = "管理局发现该企业造假";
        //等严重违反《药品生产质量管理规范》行为。
        String test = "本所是国家教育部批准的硕士学位授权单位,医学、免疫学、病原生物学授权点。迄今为止，共培养硕士研究生66人。2001年度本所被人事部批准为企业博士后科研工作站。由长春生物所主办的《中国生物制品学杂志》是国家一级专业性杂志，已刊载论文数千篇，被评为中华预防医学会优秀期刊，1999年改为国际版大十六开，有关信息被美国医学会检索系统收录。自1982年国家对药品生产实行批准文号制度以来，本所先后获得各类生物制品生产批件115个，产品种类涵盖： 疫苗、类毒素、抗毒素、免疫血清、血液制品、细胞因子、单克隆抗体、免疫学诊断试剂等诸多类型从1992年起，本所工业总产值首次突破亿元大关，且每年以15-20%的速度递增。产品销售网络遍及全国29个省市自治区。1993年，所组建成立了进出口公司、 获得进出口经营权，　先后有十几种产品出口韩国日本越南、印度、美国、巴基斯坦、加拿大等国家，每年出口创汇额达 100万美元以上。";
        //String test = "尼泊尔发生8.1级地震，震源深度20千米，至少造成8761人死亡。";
        //String test = "地震震中位于博克拉，该城市是尼泊尔第二大城市、著名旅游胜地。";
        //String test = "德国总统高克访问中国，并在同济大学发表演讲。";
        //String test = "2000年，莫言的《红高粱》入选《亚洲周刊》评选的“20世纪中文小说100强”。";
        //String test = "浮士德与魔鬼达成协议。";
        //String test = "习近平对埃及进行国事访问。";
        //String test = "习近平对埃及访问。”;
        //String test = "习近平对埃及国事访问。";
        //String test = "美国总统奥巴马访问中国";
        //String test = "小米和王明喜欢电影。";
        //String test = "习近平访问美国，发表讲话。";
        //String test = "中科院被国务院批准为研究院";
        //String test = "国务院批准中科院为研究院";
        DependencyExtractor depExt = new DependencyExtractor();
        PathExtractor pathExt = new PathExtractor();

        String[] test_set = test.split("&");
        for (String s : test_set){
            System.out.println(s);
            CoNLLSentence sent = HanLP.parseDependency(s);
            System.out.println(sent);

            ArrayList<HashMap> childrenDict = depExt.getChildrenDict(sent);

            /*
            for ( int i = 0; i < childrenDict.size();  i++) {
                System.out.print(sent.word[i].LEMMA);
                System.out.println(childrenDict.get(i));
            }
            */
            System.out.println();

            depExt.SVO(sent,childrenDict);
            ArrayList<String> relations = pathExt.getRelations(sent,childrenDict);
            System.out.println(Arrays.toString(relations.toArray()));
            System.out.println();

        }


        try {
            //CRFNERecognizer ner = new CRFNERecognizer();
            //String[] sner = ner.recognize("成龙 是 第一 个 获得 诺贝尔文学奖 的 中国 籍作家 。".split(" "), "nr v m q v nz u ns n w".split(" "));
            //System.out.println(Arrays.toString(sner));
        } catch (Exception e){
            System.out.println("exp");
        }
    }



}
