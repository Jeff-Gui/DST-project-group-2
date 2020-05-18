package DST2.Group2.Controller;

import DST2.Group2.DAO.*;
import DST2.Group2.crawler.DosingGuidelineCrawler;
import DST2.Group2.crawler.DrugLabelCrawler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

    /**
     * A controller for administrator use case, pass account info & knowledge base to web page
     * Author: DST2 group 2
     * Version: 1.0 on 07-05-2020
     */

    @Autowired
    private ClinicAnnDAO clinicAnnDAO;
    @Autowired
    private DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    private DrugLabelDAO drugLabelDAO;
    @Autowired
    private VarDrugAnnDAO varDrugAnnDAO;
    @Autowired
    private SampleDAO sampleDAO;
    @Autowired
    private GeneDAO geneDAO;
    @Autowired
    private UserDAO userDAO;

    private DosingGuidelineCrawler dosingGuidelineCrawler = new DosingGuidelineCrawler();
    private DrugLabelCrawler drugLabelCrawler = new DrugLabelCrawler();

    @RequestMapping("/panel")
    private ModelAndView showInfo(){
        ModelAndView m = new ModelAndView("admin");
        m.addObject("count_clinic_all", clinicAnnDAO.findAll().size());
        m.addObject("count_dosing_all",dosingGuidelineDAO.findAll().size());
        m.addObject("count_label_all",drugLabelDAO.findAll().size());
        m.addObject("count_varDrug_all",varDrugAnnDAO.findAll().size());
        m.addObject("samples",sampleDAO.findAll());
        m.addObject("users",userDAO.findAll());
        return m;
    }

    @RequestMapping("/import")
    private String UpdateData(@RequestParam("update") String update, @RequestParam("deleteSampleId") String deleteSampleId, @RequestParam("deleteUserName") String deleteUserName){
        ModelAndView m = new ModelAndView("admin");
        if (update.equals("true")) {
            clinicAnnDAO.doImport(true);
            varDrugAnnDAO.doImportVarDrugAnn(true);
            geneDAO.doImport(true);
            dosingGuidelineCrawler.doCrawlerDosingGuidelineList();
            drugLabelCrawler.doCrawlerDrugLabel();
            drugLabelCrawler.doCrawlerDrug();
            varDrugAnnDAO.doImportVariant(true);
            drugLabelDAO.doImportDrugName(true);
            dosingGuidelineDAO.doImportGuidelineName(true);
        }
        if (!deleteSampleId.equals("")){
            for (String id:deleteSampleId.split(",")){
                sampleDAO.deleteById(Integer.parseInt(id));
            }
        }
        if (!deleteUserName.equals("")){
            for (String name:deleteUserName.split(",")){
                userDAO.deleteByName(name);
            }
        }
        return "redirect:/panel";
    }
    
}
