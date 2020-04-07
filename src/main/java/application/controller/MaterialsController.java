package application.controller;

import application.controller.json_model.MaterialName;
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

@RestController
@CrossOrigin
public class MaterialsController {
    @Autowired
    private GraphNodeService graphNodeService;
    @Autowired
    private NodeChildService nodeChildService;
    @Autowired
    private FileService fileService;

    @Value("${resourcesTitle}")
    private String resourcesTitle;

    @RequestMapping(value = "/materials/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public String[] materials(@PathVariable String course_id, @PathVariable String mindmap_id,
                                                              @PathVariable String node_id) {

        //找到node
        GraphNode result_node = graphNodeService.findByNodeId(course_id, mindmap_id, node_id);
        if(result_node==null)
            return null;
        Material[] materials = graphNodeService.findMaterials(result_node.getLong_id());

        String[] ans = new String[materials.length];

        for (int i = 0; i < materials.length; i++){
            ans[i] = materials[i].getMaterialName();
        }

        return ans;
    }

    @RequestMapping(value = "/upload_material/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success upload_material(@PathVariable String course_id, @PathVariable String mindmap_id,
                                   @PathVariable String node_id,
                                   @RequestParam(value = "material") MultipartFile file) {

        final String filePath =resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/material/";
        Success s = new Success();
        s.setSuccess(false);
        // 获取文件名
        String fileName = file.getOriginalFilename();

        //首先判断文件名字是否已经存在
        Material material = nodeChildService.findMaterial(filePath + fileName);
        if (material != null){
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

            //新建
            Material ma = new Material();
            ma.setMaterialName(fileName);
            ma.setStoreAddress(filePath + fileName);
            ma = nodeChildService.saveMaterial(ma);

            //建立关系
            result_node.setMaterial(ma);
            graphNodeService.save(result_node);
            s.setSuccess(true);
        }
        return s;
    }

    @RequestMapping(value = "/download_material/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public String download_material(@PathVariable String course_id, @PathVariable String mindmap_id,
                                    @PathVariable String node_id, @RequestBody MaterialName material,
                                    HttpServletRequest request, HttpServletResponse response) {

        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/material/";

        String material_name = material.getMaterial_name();
        String fileUrl = filePath + material_name;

        File file = new File(fileUrl);
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + material_name);// 设置文件名
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

    @RequestMapping(value = "/delete_material/{course_id}/{mindmap_id}/{node_id}/{material}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete_material(@PathVariable String course_id, @PathVariable String mindmap_id,
                                             @PathVariable String node_id, @PathVariable String material, HttpServletResponse response) {
        final String filePath = resourcesTitle + course_id + "/" + mindmap_id + "/" + node_id + "/material/";
        String fileUrl = filePath + material;

        nodeChildService.deleteMaterial(material, fileUrl);
        Success success = new Success();
        success.setSuccess(true);
        return ResponseEntity.ok().body(success);
    }

    @RequestMapping(value = "/links/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.GET)
    public Link[] links(@PathVariable String course_id, @PathVariable String mindmap_id,
                          @PathVariable String node_id) {

        //找到node
        GraphNode result_node = graphNodeService.findByNodeId(course_id, mindmap_id, node_id);
        if(result_node==null)
            return null;

        return graphNodeService.findLinks(result_node.getLong_id());
    }

    @RequestMapping(value = "/upload_link/{course_id}/{mindmap_id}/{node_id}", method = RequestMethod.POST)
    public Success upload_link(@PathVariable String course_id, @PathVariable String mindmap_id,
                               @PathVariable String node_id,
                               @RequestBody Link link) {
        Success s = new Success();
        s.setSuccess(false);

        //找到node
        GraphNode result_node = graphNodeService.findByNodeId(course_id, mindmap_id, node_id);

        if (result_node != null) {
            link.setNode(result_node.getLong_id() + "");

            //新建Courseware
            nodeChildService.saveLink(link);

            //建立关系
            result_node.setLink(link);
            graphNodeService.save(result_node);
            s.setSuccess(true);
        }
        return s;
    }

    @RequestMapping(value = "/delete_link/{link_name}/{node}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete_link(@PathVariable String link_name, @PathVariable String node) {
        nodeChildService.deleteLink(link_name, node);
        Success success = new Success();
        success.setSuccess(true);
        return ResponseEntity.ok().body(success);
    }
}


