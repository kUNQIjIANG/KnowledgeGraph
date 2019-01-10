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
            String test = "本所是国家教育部批准的硕士学位授权单位,医学、免疫学、病原生物学授权点。迄今为止，共培养硕士研究生66人。2001年度本所被人事部批准为企业博士后科研工作站。" +
                    "长春生物所主办的《中国生物制品学杂志》是国家一级专业性杂志，已刊载论文数千篇，被评为中华预防医学会优秀期刊，1999年改为国际版大十六开，有关信息被美国医学会检索系统收录。" +
                    "自1982年国家对药品生产实行批准文号制度以来，本所先后获得各类生物制品生产批件115个，产品种类涵盖：疫苗、类毒素、抗毒素、免疫血清、血液制品、细胞因子、单克隆抗体、免疫学诊断试剂等诸多类型。" +
                    "从1992年起，本所工业总产值首次突破亿元大关，且每年以15-20%的速度递增。产品销售网络遍及全国29个省市自治区。1993年，本所组建成立了进出口公司、获得进出口经营权，先后有十几种产品出口" +
                    "韩国日本越南、印度、美国、巴基斯坦、加拿大等国家，每年出口创汇额达100万美元以上。";
            /*
            String test = "长春长生生物公司假疫苗案持续升温，继习近平李克强批示严查后，中纪委表态成立调查组，甚至有消息称习近平要借这个机会抓大老虎。" +
                    "反腐维权联盟发起人马波不认为，事件最终能给民众一个满意答复。中共喉舌《人民日报》下属媒体更是公开给长生生物开脱。" +
                    "7月15日，中共国家药监局通报了长春长生公司狂犬病疫苗生产记录造假问题，假疫苗问题的盖子由此揭开。" +
                    "长春长生已经向山东疾病预防控制中心出售了25万多支劣质百白破疫苗，而长生生物已经向重庆和河北出售了40多万支劣质百白破疫苗。" +
                    "目前尚不清楚到底有多少儿童被注射了这些疫苗。网友”Uromayutori”表示，市场占率超过1/4的长生生物公司狂犬疫苗造假，" +
                    "百白破造假，被查出来也就罚个三百万；进口别国已审批的疫苗，以假药罪罚两百万判七年；问题疫苗致残儿童的家长上访，寻衅滋事判两年；" +
                    "三聚氰胺事件中被处分的官员现在居然还能管着药监部门。这就是中共的奇葩现象。自由亚洲电台7月24日文章指，目前大陆的疫苗分一类及二类，" +
                    "其中一类疫苗包括乙肝疫苗、百白破等由政府提供免费接种。而水痘、疯狗症等二类疫苗则要自费注射。在2016年改制后，原先由市场定价的二类疫苗，" +
                    "必须由政府的疾病控制中心统一采购和销售，因此便形成为了部分不法人员从中谋取利益的灰色地带。北京时间7月22日16时22分，名为“中国经济网”的认证微博" +
                    "爆出猛料，揭露长生公司一年花6亿人民币的营销费用行贿各地官员。微博写道：长春生物年报显示，2017年长生生物疫苗销售的营业收入15.39亿元，" +
                    "销售费用为5.83亿元，销售人员仅25人，人均销售费用2331.85万元。这背后肯定存在行贿受贿等严重腐败行为。自由亚洲电台上述报道披露，" +
                    "公司董事长高俊芳亦被媒体起底，没有任何医学方面资历的她在公司身兼董事长、董事、法定代表人、总经理、财务总监，" +
                    "她的丈夫、儿子、亲属在公司都担任管理层或股东。估计其家族财富约67亿元人民币。时政分析人士陈思敏说，据《疫苗之王》一文披露，" +
                    "三地三家国企摇身一变为民企，高俊芳等三人同时变身疫苗三巨头，掌握了全国疫苗的半壁江山。长春长生公司董事长兼总经理高俊芳是三巨头之首，因此被称为“疫苗之王”。" +
                    "网上另有消息透露，三人为隐秘富豪，比他们更隐密的是背后不方便挂名的大老板即大靠山，这也是“疫苗之王”多次出现问题，年年被举报却无人能撼动的原因。" +
                    "现在“疫苗之王”被撼动，表明其背后的大靠山或在中共十九大后退休了。这些年“疫苗之王”要命的疫苗上市通行无阻惹众怒，从下到上都说要彻查到底，" +
                    "大靠山会不会变成大老虎并落网，同样值得关注。";
            */
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
            //String test = "长生生物造假狂犬疫苗";
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
