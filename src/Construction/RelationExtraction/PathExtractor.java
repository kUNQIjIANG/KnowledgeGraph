package Construction.RelationExtraction;

import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.hankcs.hanlp.corpus.dependency.CoNll.CoNLLWord;

import java.util.*;

public class PathExtractor extends Extractor {

    public static ArrayList<String> getPath (CoNLLSentence sent, ArrayList<HashMap> childrenDict, CoNLLWord source, CoNLLWord target){
        HashMap<CoNLLWord, ArrayList<String>> paths = new HashMap<>();
        Queue<CoNLLWord> queue = new LinkedList<>();
        queue.add(source);
        ArrayList<String> sourcePath = new ArrayList<>();
        paths.put(source,sourcePath);
        while (!queue.isEmpty()){
            CoNLLWord cur = queue.remove();
            HashMap<String,ArrayList<Integer>> children = childrenDict.get(cur.ID-1);

            // children path
            for (Map.Entry<String,ArrayList<Integer>> entry : children.entrySet()){
                for (int child : entry.getValue()){
                    CoNLLWord childWord = sent.word[child-1];
                    if (!paths.containsKey(childWord)){
                        ArrayList<String> childPath = new ArrayList<>(paths.get(cur));
                        childPath.add(childWord.DEPREL);
                        paths.put(childWord,childPath);
                        queue.add(childWord);
                        if (childWord.equals(target)) return paths.get(target);
                    }
                }
            }

            // head path
            if (!cur.DEPREL.equals("核心关系")) {
                CoNLLWord headWord = cur.HEAD;
                if (!paths.containsKey(headWord)) {
                    ArrayList<String> headPath = new ArrayList<>(paths.get(cur));
                    headPath.add(cur.DEPREL);
                    paths.put(headWord, headPath);
                    queue.add(headWord);
                    if (headWord.equals(target)) return paths.get(target);
                }
            }
        }
        return paths.get(target);
    }

    ArrayList<String> getRelations (CoNLLSentence sent,ArrayList<HashMap> childrenDict){
        ArrayList<String> relations = new ArrayList<>();
        String[] keyPaths = {"主谓关系","动宾关系","间宾关系","前置宾语","兼语","状中结构"};
        ArrayList<CoNLLWord> verbs = getVerb(sent);
        ArrayList<CoNLLWord> ners = getNER(sent);
        for (CoNLLWord verb : verbs){
            String relation = verb.LEMMA + "(";
            for (CoNLLWord ner : ners){
                ArrayList<String> path = getPath(sent,childrenDict,verb,ner);
                if (!Collections.disjoint(Arrays.asList(keyPaths),path)){
                    relation += ner.LEMMA + ",";
                }
            }
            relation = relation.substring(0,relation.length()-1) + ")";
            relations.add(relation);
        }
        return relations;

    }
}
