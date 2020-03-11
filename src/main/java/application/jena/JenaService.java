package application.jena;

import application.controller.json_model.NodeValue;
import application.controller.json_model.Reason;
import application.controller.json_model.Suggestion;
import application.model.Node;
import application.repository.NodeRepository;
import application.service.NodeChildService;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * Jena 推理机
 * @Date 2019/6/1
 * @author DreamBoy
 */

@Service
public class JenaService {
    @Autowired
    private NodeChildService nodeChildService;
    @Autowired
    private NodeRepository nodeRepository;

    private OntModel ontModel;
    {
        ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        ontModel.read("java.owl");
    }


    public static void main(String[] args) {
        JenaService service = new JenaService();
        List<String> father = service.searchRelationNode("结构化编程", "precursorOf");
        System.out.println(father);
    }


    public List<Suggestion> getSuggestion(String mindmap_id, long studentId) {
        List<NodeValue> nodeValueList = nodeChildService.getNodeValueForStudent(mindmap_id, studentId);
        Map<String, NodeValue> nodeValueMap = new HashMap<>();
        List<String> problemNodes = new ArrayList<>();
        for (NodeValue nodeValue: nodeValueList) {
            nodeValueMap.put(nodeValue.getNode_topic(), nodeValue);

            if (nodeValue.getValue() < 0.6) {
                problemNodes.add(nodeValue.getNode_topic());
            }
        }
        List<Suggestion> suggestions = new ArrayList<>();

        for (String topic: problemNodes) {
            Map<String, String> map = getSuggestionsForANode(nodeValueMap, topic);
            for (String str: map.keySet()) {
                Suggestion suggestion = new Suggestion();
                suggestion.setProblemNodeTopic(topic);
                suggestion.setSourceNodeTopic(str);
                suggestion.setReason(map.get(str));
                if (str != null)
                    suggestions.add(suggestion);
            }

        }
        return suggestions;
    }

    public Map<String, String> getSuggestionsForANode(Map<String, NodeValue> nodeValueMap, String nodeTopic) {
        Map<String, String> suggestions = new HashMap<>();
        List<String> topics = searchRelationNode(nodeTopic, "precursorOf");

        helpFun(suggestions, nodeValueMap, topics);

        return suggestions;
    }

    private void helpFun(Map<String, String> suggestions, Map<String, NodeValue> nodeValueMap, List<String> topics) {
        if (topics.size() != 0) {
            for (String topic: topics) {
                if (!suggestions.containsKey(topic)) {
                    NodeValue nodeValue = nodeValueMap.get(topic);
                    if (nodeValue.getValue() < 0.6) { //节点成绩小于0.6
                        suggestions.put(topic, "该知识节点没有学好，建议阅读该节点相关课件等学习资源");
                    } else if (nodeValue.getScore() == 0 && nodeValue.getTotalScore() != 0) { // 节点有题目，但没有完成
                        suggestions.put(topic, "该节点作业完成度不够，建议认真完成该节点相关作业");
                    }else {
                        List<String> tmp = searchRelationNode(topic, "precursorOf");
                        helpFun(suggestions, nodeValueMap, tmp);
                    }
                }
            }
        }
    }

    private List<String> searchRelationNode(String child, String relation) {
        String queryString = "" +
                "PREFIX mytology: <http://www.semanticweb.org/mindmap/ontology#>\n" +
                "SELECT ?s WHERE {\n" +
                "?s ?"+ relation +" mytology:"+child+".\n" +
                "}" +
                "";
        Query query = QueryFactory.create(queryString);
        QueryExecution qe = QueryExecutionFactory.create(query, ontModel);
        ResultSet results = qe.execSelect();
        List<String> rs = new ArrayList<>();

        while (results.hasNext()) {
            String[] s = results.next().toString().split("#");
            rs.add(s[1].split(">")[0]);
        }

        qe.close();
        return rs;
    }
}
