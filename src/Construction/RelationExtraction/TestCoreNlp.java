package Construction.RelationExtraction;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;


public class TestCoreNlp {
    public void test() throws Exception {
        String props="StanfordCoreNLP-chinese.properties";
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        //String text = "曼联官方宣布，穆里尼奥下课。";
        String text = "本所是国家教育部批准的硕士学位授权单位，医学免疫学、病原生物学授权点。迄今为止，共培养硕士研究生66人。2001年度被人事部批准为企业博士后科研工作站。由长春生物所主办的《中国生物制品学杂志》是国家一级专业性杂志，已刊载论文数千篇，被评为中华预防医学会优秀期刊，1999年改为国际版大十六开，有关信息被美国医学会检索系统收录。自1982年国家对药品生产实行批准文号制度以来，本所先后获得各类生物制品生产批件115个，产品种类涵盖： 疫苗、类毒素、抗毒素、免疫血清、血液制品、细胞因子、单克隆抗体、免疫学诊断试剂等诸多类型从1992年起，本所工业总产值首次突破亿元大关，且每年以15-20%的速度递增。产品销售网络遍及全国29个省市自治区。1993年，所组建成立了进出口公司、 获得进出口经营权，　先后有十几种产品出口韩国日本越南、印度、美国、巴基斯坦、加拿大等国家，每年出口创汇额达 100万美元以上。";
        long startTime = System.currentTimeMillis();
        Annotation document = new Annotation(text);

        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                //词性标注
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                // 命名实体识别
                //String ne = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.printf("word: %s, pos: %s, ner: %s",word,pos,ner);
                System.out.println();

            }
        }
        long endTime = System.currentTimeMillis();
        System.out.printf("time used: %d",endTime-startTime);
    }

    public static void main(String[] args) throws Exception{
        TestCoreNlp nlp = new TestCoreNlp();
        nlp.test();
    }
}
