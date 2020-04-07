package application.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RecommendationController {

    @RequestMapping(value = "/recommendation/{course_id}/{graph_id}/{student_name}")
    public String generateRecommendationList(@PathVariable String course_id, @PathVariable String graph_id,
                                             @PathVariable String student_name){
        return null;
    }
}
