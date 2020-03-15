import application.controller.json_model.*;
import application.service.MindmapService;
import application.service.NodeChildService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {
    public static void main(String[] args) {
        NodeChildService nodeChildService=new NodeChildService();
        System.out.println(nodeChildService.getAllAssignmentShort());
    }

}
