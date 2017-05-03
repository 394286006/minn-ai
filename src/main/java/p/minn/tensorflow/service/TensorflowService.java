package p.minn.tensorflow.service;
import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tensorflow.Graph;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.TensorFlow;

import p.minn.common.utils.MyGsonMap;
import p.minn.common.utils.Page;
import p.minn.hadoop.hdfs.HDFSFileUtils;
import p.minn.privilege.entity.IdEntity;
import p.minn.privilege.entity.PicturePath;
import p.minn.privilege.utils.Utils;

/**
 * 
 * @author minn 
 * @QQ:3942986006
 * 
 */
@Service
public class TensorflowService extends LabelImage{
 
  @Autowired
  private HDFSFileUtils hdfsFileUtils;
  
  public Object updatePic(String lang,HttpServletRequest req) throws Exception {
    // TODO Auto-generated method stub
       Map<String,String> rs=new HashMap<String,String>();
       String  uploadPath =Utils.getReadUploadHDFSPath(req); 
       File disk = null;
       FileItem item = null;
       DiskFileItemFactory factory = new DiskFileItemFactory();                                   
       String fileName = "";
       String messageBody="";
       ListIterator iterator = null;
       List items = null;
       ServletFileUpload upload = new ServletFileUpload( factory );
       items = upload.parseRequest(req);
       iterator = items.listIterator();
       while(iterator.hasNext() ) {
            item = (FileItem)iterator.next();
            if(item.isFormField()){
                if (item.getFieldName().equalsIgnoreCase("upfilename")){
                    fileName = item.getString();                                                                    }
                if (item.getFieldName().equalsIgnoreCase("messageBody")){
                    messageBody=URLDecoder.decode(item.getString(), "UTF-8");
                }
              }else{
                  disk = new File(uploadPath + fileName+".png");   
                  item.write(disk);    
               }
          } 
       rs.put("fileName",fileName+".png");
       rs.put("imgpath", "hdfstmp/"+fileName+".png");
    return rs;
}

  public Object compare(String lang, String messageBody,HttpServletRequest req) {
    // TODO Auto-generated method stub
    String  uploadPath =Utils.getReadUploadHDFSPath(req); 
    Map<String,Object> rs=new HashMap<String,Object>();
    Map condition=(Map) Utils.gson2T(messageBody, Map.class);
     System.out.println("compare:"+condition.get("filename"));
     try {
      hdfsFileUtils.setInput("tensorflow/");
      byte[] graphDef= hdfsFileUtils.readFileData("tensorflow_inception_graph.pb");
      String labelstr= hdfsFileUtils.readFileContent("imagenet_comp_graph_label_strings.txt");
      String[] labelarr=labelstr.split("\n");
      List<String> labels=Arrays.asList(labelarr) ;
      byte[] imageBytes = readAllBytesOrExit(Paths.get(uploadPath+condition.get("filename")));
      try (Tensor image = constructAndExecuteGraphToNormalizeImage(imageBytes)) {
        float[] labelProbabilities = executeInceptionGraph(graphDef, image);
        int bestLabelIdx = maxIndex(labelProbabilities);
        rs.put("matchname", labels.get(bestLabelIdx));
        rs.put("matchpercent",  labelProbabilities[bestLabelIdx] * 100f);
      }
    } catch (Exception e) {
      rs.put("matchname","none");
      rs.put("matchpercent", 0.0);
    }
    return rs;
  }
  
 
}