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
        String text = "曼联官方宣布，穆里尼奥下课。";
        long startTime = System.currentTimeMillis();
        Annotation document = new Annotation(text);

        pipeline.annotate(document);

        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String word = token.get(CoreAnnotations.TextAnnotation.class);
                System.out.println(word);
                //词性标注
                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
                System.out.println(pos);
                // 命名实体识别
                String ne = token.get(CoreAnnotations.NormalizedNamedEntityTagAnnotation.class);
                String ner = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
                System.out.printf("word:\t%s, ne:\t%s, ner:\t%s",word,ne,ner);

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
