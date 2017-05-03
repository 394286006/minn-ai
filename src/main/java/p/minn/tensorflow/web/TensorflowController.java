package p.minn.tensorflow.web;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import p.minn.common.annotation.MyParam;
import p.minn.common.exception.WebPrivilegeException;
import p.minn.privilege.utils.Constant;
import p.minn.tensorflow.service.TensorflowService;
 
/**
 * 
 * @author minn 
 * @QQ:3942986006
 * 
 */
@Controller
@RequestMapping("/tensorflow")
@SessionAttributes(Constant.LOGINUSER)
public class TensorflowController {

	@Autowired
	private TensorflowService tensorflowService;
	
	@RequestMapping(params = "method=updatePic")
    @ResponseBody
    public Object updatePic(@MyParam("language") String lang,HttpServletRequest req,HttpServletResponse rep) {
        Object entity = null;
        try {
              entity=tensorflowService.updatePic(lang,req);
        } catch (Exception e) {
            e.printStackTrace();
            entity = new WebPrivilegeException(e.getMessage());
        }
        return entity;
    }
	
	@RequestMapping(params = "method=compare")
    @ResponseBody
    public Object compare(@MyParam("language") String lang,@RequestParam("messageBody") String messageBody,HttpServletRequest req) {
        Object entity = null;
        try {
              entity=tensorflowService.compare(lang,messageBody,req);
        } catch (Exception e) {
            e.printStackTrace();
            entity = new WebPrivilegeException(e.getMessage());
        }
        return entity;
    }

}
