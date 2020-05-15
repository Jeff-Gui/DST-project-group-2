package DST2.Group2.Controller;

import DST2.Group2.DAO.AnnovarDAO;
import DST2.Group2.DAO.SampleDAO;
import DST2.Group2.DAO.VepDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class UploadController extends HttpServlet {

    @Autowired
    private SampleDAO sampleDAO;
    @Autowired
    private AnnovarDAO annovarDAO;
    @Autowired
    private VepDAO vepDAO;

	private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    public UploadController() {
        super();
    }

    @RequestMapping(value = "/upload/{uploadType}", method = RequestMethod.POST)
	protected String doUpload(HttpServletRequest request,
                              @RequestParam("file") MultipartFile requestPart,
                              @PathVariable String uploadType) throws IOException {
        log.info("upload vcf");
    	String uploadedBy = request.getParameter("uploaded_by");

        if (uploadedBy == null || uploadedBy.isEmpty()) {
        	log.info("is empty");
            request.setAttribute("validateError", "Uploaded by can not be blank");
            return "matching_index_error";
        }
        if (requestPart == null) {
            request.setAttribute("validateError", "vcf output file can not be blank");
            return "matching_index_error";
        }
        
        InputStream inputStream = new BufferedInputStream(requestPart.getInputStream());
        
        byte[] bytes =  inputStream.readAllBytes();
        String content = new String(bytes);

        if (uploadType.equals("annovar")){
            int sampleId = sampleDAO.save(uploadedBy,"annovar");
            annovarDAO.save(sampleId, content);
            log.info("read file "+content.length());
            request.setAttribute("samples",sampleDAO.findAll());
            return "samples";
        } else {
            if (uploadType.equals("vep")){
                int sampleId = sampleDAO.save(uploadedBy,"vep");
                vepDAO.save(sampleId, content);
                log.info("read file "+content.length());
                request.setAttribute("samples",sampleDAO.findAll());
                return "samples";
            } else {
                return "matching_index_error";
            }
        }
	}

}
