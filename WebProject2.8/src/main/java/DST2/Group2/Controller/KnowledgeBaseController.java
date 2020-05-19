package DST2.Group2.Controller;

import DST2.Group2.bean.*;
import DST2.Group2.DAO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
/**
 * @Description This is the description of class
 * Bulk retrieval of information stored in the localized database.
 * Information includes: drugs, drug labels, dosing guidelines and clinical annotations.
 * @Date 2020/5/15
 * @Author DST group 2
 */
@Controller
public class KnowledgeBaseController {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseController.class);

    @Autowired
    private DrugDAO drugDao;
    @Autowired
    private DrugLabelDAO drugLabelDao;
    @Autowired
    private DosingGuidelineDAO dosingGuidelineDao;
    @Autowired
    private ClinicAnnDAO clinicAnnDAO;

    @RequestMapping("/drugs")
    public ModelAndView drugs() {
        ModelAndView drug=new ModelAndView();
        drug.setViewName("drugs");
        List<DrugBean> drugBeans = drugDao.findAll();
        drug.addObject("drugs", drugBeans);
        return drug;
    }

    @RequestMapping("/drugLabels")
    public ModelAndView drugLabels(){
        ModelAndView d=new ModelAndView();
        d.setViewName("drug_labels");
        List<DrugLabelBean> drugLabelBeans = drugLabelDao.findAll();
        d.addObject("drugLabels", drugLabelBeans);
        return d;
    }

    @RequestMapping("/dosingGuideline")
    public ModelAndView dosingGuideline(){
        ModelAndView d=new ModelAndView();
        d.setViewName("dosing_guideline");
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDao.findAll();
        d.addObject("dosingGuidelines", dosingGuidelineBeans);
        return d;
    }

    @RequestMapping("/clinicAnn")
    public ModelAndView clinicAnn(){
        ModelAndView d=new ModelAndView();
        d.setViewName("clinic_ann");
        List<ClinicAnnBean> clinicAnnBeans = clinicAnnDAO.findAll();
        d.addObject("clinicAnn", clinicAnnBeans);
        return d;
    }

}
