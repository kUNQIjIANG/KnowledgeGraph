package Construction.RelationExtraction;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;
import com.hankcs.hanlp.corpus.document.sentence.word.CompoundWord;
import com.hankcs.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.hankcs.hanlp.model.crf.CRFNERecognizer;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CompletionService;

public class Extractor {

    ArrayList<HashMap> getChildrenDict (CoNLLSentence sentence){
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

    ArrayList<CoNLLWord> getNER(CoNLLSentence sent){
        ArrayList<CoNLLWord> ners = new ArrayList<>();
        String[] posNers = {"nr","ns","nt","nx","nz","nh"};
        String[] depRels = {"主谓关系","动宾关系","介宾关系"};
        for (CoNLLWord word : sent){
            if (Arrays.asList(posNers).contains(word.POSTAG) || Arrays.asList(depRels).contains(word.DEPREL)){
                ners.add(word);
            }
        }
        return ners;
    }

    ArrayList<CoNLLWord> getVerb(CoNLLSentence sent){
        ArrayList<CoNLLWord> verbs = new ArrayList<>();
        String[] depRels = {"核心关系","并列关系"};
        for (CoNLLWord word : sent){
            if (word.CPOSTAG.equals("v") && !word.DEPREL.equals("动宾关系")){
                verbs.add(word);
            }
        }
        return verbs;
    }

}
