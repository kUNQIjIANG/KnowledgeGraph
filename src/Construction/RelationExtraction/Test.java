package Construction.RelationExtraction;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.corpus.document.sentence.word.IWord;
import com.hankcs.hanlp.corpus.document.sentence.word.Word;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.HanLP.Config;
import com.hankcs.hanlp.model.perceptron.PerceptronNERecognizer;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
        String SENTENCE = "莫言是第一个获得诺贝尔文学奖的中国籍作家。";
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer("E:/hanLP/data/model/perceptron/pku199801/cws.bin",
                                                                            Config.PerceptronPOSModelPath,
                                                                            Config.PerceptronNERModelPath);
        PerceptronNERecognizer recognizer = new PerceptronNERecognizer(Config.PerceptronNERModelPath);

        Sentence sentence = analyzer.analyze(SENTENCE);

        String[] sentence1 = recognizer.recognize("吴忠市 习近平 联合国 谭利华 来到 布达拉宫 广场".split(" "), "ns nr nt nr p ns n".split(" "));

        System.out.println("1: "+sentence);
        for (String s : sentence1){
            System.out.println(s);
        }
        System.out.println();
    }
}
