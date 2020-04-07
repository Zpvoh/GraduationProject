package application.controller;

import application.controller.json_model.CoursewareName;
import application.controller.json_model.Success;
import application.model.*;
import application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;

@RestController
@CrossOrigin
public class CoursewaresController {
    @Autowired
    private GraphNodeService graphNodeService;
    @Autowired
    private NodeChildService nodeChildService;
    @Autowired
    private FileService fileService;

    @Value("${resourcesTitle}")
    private String resourcesTitle;
    @Value("${helpFile}")
    private String helpFile;

    @RequestMapping(value = "/coursewares/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public String[] coursewares(@PathVariable String course_id, @PathVariable String mindmap_id,
                                        @PathVariable String node_id) {
        //找到node
        GraphNode result_node = graphNodeService.findByNodeId(course_id, mindmap_id, node_id);
        if (result_node==null)
            return null;
        Courseware[] coursewares = graphNodeService.findCoursewares(result_node.getLong_id());

        String[] ans = new String[coursewares.length];

        for (int i = 0; i < coursewares.length; i++){
            ans[i] = coursewares[i].getCourseware_name();
        }

        return ans;
    }

    @RequestMapping(value = "/upload_courseware/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success upload_courseware(@PathVariable String course_id, @PathVariable String mindmap_id,
                                     @PathVariable String node_id, @RequestParam(value = "courseware") MultipartFile file) {
        //final String filePath = "/home/ubuntu/MindMapFileStorage/" + course_id + "/" + mindmap_id + "/" + node_id + "/courseware/";
        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/courseware/";
        Success s = new Success();
        s.setSuccess(false);

        // 获取文件名
        String fileName = file.getOriginalFilename();

        //首先判断文件名字是否已经存在
        Courseware cw = nodeChildService.findCourseware(filePath + fileName);
        if (cw != null){
            return s;
        }

        File dest = new File(filePath + fileName);

        //找到node
        GraphNode result_node = graphNodeService.findByNodeId(course_id, mindmap_id, node_id);
        if (result_node != null) {
            // 检测是否存在目录
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            try {
                file.transferTo(dest);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //新建Courseware
            Courseware courseware = new Courseware();
            courseware.setCourseware_name(fileName);
            courseware.setStore_address(filePath + fileName);
            nodeChildService.saveCourseware(courseware);

            //建立关系
            result_node.addCourseware(courseware);
            graphNodeService.save(result_node);
            s.setSuccess(true);
        }
        return s;
    }

    @RequestMapping(value = "/download_courseware/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public String download_courseware(@PathVariable String course_id, @PathVariable String mindmap_id,
                                      @PathVariable String node_id, @RequestBody CoursewareName courseware,
                                      HttpServletRequest request, HttpServletResponse response) {

        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/courseware/";

        String courseware_name = courseware.getCourseware_name();
        String fileUrl = filePath + courseware_name;

        File file = new File(fileUrl);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + courseware_name);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/delete_courseware/{course_id}/{mindmap_id}/{node_id}/{courseware}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete_courseware(@PathVariable String course_id, @PathVariable String mindmap_id,
                                               @PathVariable String node_id, @PathVariable String courseware, HttpServletResponse response) {
        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/courseware/";
        String fileUrl = filePath + courseware;

        nodeChildService.deleteCourseware(courseware, fileUrl);
        Success success = new Success();
        success.setSuccess(true);
        return ResponseEntity.ok().body(success);
    }

    @RequestMapping(value = "/view_courseware/{course_id}/{mindmap_id}/{node_id}/{courseware}", method = RequestMethod.GET)
    public ResponseEntity<?> view_courseware(@PathVariable String course_id, @PathVariable String mindmap_id,
                                          @PathVariable String node_id, @PathVariable String courseware, HttpServletResponse response) {
        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/courseware/";
        String fileUrl = filePath + courseware;

        try {
            byte[] file = fileService.getFile(fileUrl);
            return ResponseEntity.ok().body(file);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(new HashMap<>().put("error", "Failed to load courseware"));
        }
    }

    @RequestMapping(value = "/view_help_video", method = RequestMethod.GET)
    public ResponseEntity<?> view_help_video(HttpServletResponse response) {
        String fileUrl = helpFile;

        try {
            byte[] file = fileService.getFile(fileUrl);
            return ResponseEntity.ok().body(file);
        }
        catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(404).body(new HashMap<>().put("error", "Failed to load courseware"));
        }
    }
}


