package DST2.Group2.Controller;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DST2.Group2.DAO.*;
import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.CSVUtils;
import DST2.Group2.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
/**
 * @Description This is the description of class
 * Controller for handling matching request.
 * Also handle searching functionality of the matching result.
 * @Date 2020/5/16
 * @Author DST group 2
 */
@MultipartConfig
@Controller
public class MatchController {

    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private ClinicAnnDAO clinicAnnDAO;
    @Autowired
    private DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    private DrugLabelDAO drugLabelDAO;
    @Autowired
    private VarDrugAnnDAO varDrugAnnDAO;
    @Autowired
    private VepDAO vepDAO;
    @Autowired
    private AnnovarDAO annovarDAO;
    @Autowired
    private SampleDAO sampleDAO;

    public ModelAndView w = new ModelAndView();
    public static List<DrugLabelBean> matchedDrugLabelBean;
    public static List<DosingGuidelineBean> matchedGuidelines;
    public static List<VarDrugAnnBean> matchedAnns=null;
    public static List<ClinicAnnBean> matched_clinic_ann_by_gene ;
    public static List<ClinicAnnBean> matched_clinic_ann_by_snp ;

    @RequestMapping("/matchingIndex")
    public String matchingIndex() {
        log.info("matchingindex");
        return "matching_index";
    }

    @RequestMapping("/samples")
    public ModelAndView samples(){
        log.info("samples");
        ModelAndView mv=new ModelAndView();
        mv.setViewName("samples");
        List<SampleBean> sampleBeans = sampleDAO.findAll();
        //pass to jsp
        mv.addObject("samples", sampleBeans);
        return mv;
    }

    @RequestMapping(value = {"/matching","/upload/matching"},method = RequestMethod.GET)
    private ModelAndView matching(@RequestParam("sampleId") String sampleIdParameter, @RequestParam("sampleType") String sampleType) {
        /**
         * @Description
         * Matching is divided into following steps:
         * 1. validate sample ID and read sample data;
         * 2. retrieve knowledge base data for matching;
         * 3. match drug label, dosing guideline, variant drug annotation, clinic annotation (either by gene or SNP);
         * 4. add matching result into the ModelAndView object.
         * @Param sampleId (request parameter), sample type (request paramter)
         * @Return jsp
         * @Date 2020/5/16
         * @author DST group 2
         **/
        //set in jsp
        matchedDrugLabelBean =null;
        matchedGuidelines =null;
        matchedAnns=null;
        matched_clinic_ann_by_gene=null;
        matched_clinic_ann_by_snp=null;
        log.info("matching");

        w.setViewName("matching_index_search");

        log.info(sampleIdParameter+" =sampleparameter");
        // null check of sample ID
        if (sampleIdParameter == null) {
            log.info("sample id parameter is null");
            ModelAndView s=new ModelAndView();
            s.setViewName("samples");
            return s;
        }

        int sampleId;
        try {
            sampleId = Integer.parseInt(sampleIdParameter);
        } catch (NumberFormatException e) {
            log.info(String.valueOf(e));
            ModelAndView s=new ModelAndView();
            s.setViewName("samples");
            return s;
        }
        log.info("get reference genes");
        // read sample from the database according to the sample type.
        List<RefBean> refBeans;
        if (sampleType.equals("annovar")) {refBeans = annovarDAO.getsampleGenes(sampleId);} else {
            if (sampleType.equals("vep")) {refBeans = vepDAO.getsampleGenes(sampleId);} else {
                log.info("sample type is not correct.");
                return new ModelAndView("matching_index_error");
            }
        }
        if (refBeans.isEmpty()) {
            log.info("reference gene set is empty");
            ModelAndView s=new ModelAndView();
            s.setViewName("matching_index_error");
            return s;
        }

        log.info("getdruglabels");
        List<DrugLabelBean> drugLabelBeans = drugLabelDAO.findAll();
        log.info("getguidelines");
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.findAll();
        log.info("getanns");
        List<VarDrugAnnBean> varDrugAnnBeans = varDrugAnnDAO.findAll();
        log.info("matchlabel");
        List<DrugLabelBean> matchedDrugLabelBean = doMatchDrugLabel(refBeans, drugLabelBeans);
        log.info("matchguideline");
        List<DosingGuidelineBean> matchedDosingGuidelineBean = doMatchDosingGuideline(refBeans, dosingGuidelineBeans);
        log.info("matchann");
        List<VarDrugAnnBean> matchedAnn = doMatchVarDrugAnn(refBeans, varDrugAnnBeans);
        log.info("match clinic annotation by gene");
        List<ClinicAnnBean> matched_clinic_ann_by_gene = doMatchClinic_by_Gene(refBeans);
        log.info("match clinic annotation by SNP, MAY TAKE SEVERAL MINUTES!");
        List<ClinicAnnBean> matched_clinic_ann_by_snp = doMatchClinic_by_SNP(refBeans);
        log.info("finished");

        //pass to jsp
        w.addObject("matchedDrugLabel", matchedDrugLabelBean);
        w.addObject("matchedDosingGuideline", matchedDosingGuidelineBean);
        w.addObject("matchedVarDrugAnn",matchedAnn);
        w.addObject("sample", sampleDAO.findById(sampleId));
        w.addObject("matched_clinic_ann_by_gene", matched_clinic_ann_by_gene);
        w.addObject("matched_clinic_ann_by_snp",matched_clinic_ann_by_snp);
        return w;
    }

    private List<ClinicAnnBean> doMatchClinic_by_Gene(List<RefBean> sampleGenes) {
        /**
         * @Description
         * Match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
         * Match if the corresponding gene of the sample variant is found in gene field in stored clinic annotation table.
         * @Param sampleGenes
         * @Return list of clinic annotation beans
         * @Date 2020/5/16
         * @author DST group 2
         */
//        ArrayList<Object> rt = new ArrayList<>(); // TODO: may return corresponding sample information to the matching result
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();
        log.info("Filtered clinic annotation record total: " + refClinicAnns.size());

        int counter=0;
        for (RefBean obj : sampleGenes){
            boolean match=false;
            for (ClinicAnnBean clinicAnnBean:refClinicAnns){

                String gene=obj.getSym_gene();
                if (clinicAnnBean.getGene()==null){ continue; }
                if (gene!=null && gene.length()!=0) {
                    if (!gene.equals("-") && clinicAnnBean.getGene().contains(gene)){

                        if (!matchedClinicAnnBeans.contains(clinicAnnBean)){
                            matchedClinicAnnBeans.add(clinicAnnBean);
                            match=true;
                        }

                        updateSampleReturn(matched_sampleInfo, obj, gene);
                    }
                }}
            if (match) {
                counter++;
            }
        }

        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to " + counter + " genes from the sample.");
//        log.info("matched sample info"+matched_sampleInfo);
//        rt.add(matchedClinicAnnBeans); // 787 records matched from sample
//        rt.add(matched_sampleInfo); // 185 genes matched from clinic annotation

        return matchedClinicAnnBeans;
    }

    private List<ClinicAnnBean> doMatchClinic_by_SNP(List<RefBean> sampleGenes) {
        /**
         * @Description
         *  To map sample variant according to its exact genomic location.
         *  Usually, this mapping is too strict to yield any positive result.
         *  Therefore, mapping by location is not recommended to be used alone.
         *  TODO ask user to decide whether to match by SNP or not
         *  TODO separate transaction whith database into DAO class.
         * @Param sampleGenes
         * @Return list of clinic annotation beans
         * @Date 2020/5/16
         * @author DST group 2
         **/
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}
        // Read in all variants
        DBmethods.execSQL(connection -> {
            String statement2 = "SELECT variant FROM variant WHERE location=? AND clinical_annotation_count!=0;";
            ArrayList<RefBean> filtered = new ArrayList<>();
            try {
                /**
                 * Match sample variant location with database, only variants with clinic annotations are considered.
                 * Query variant name ("rs" ID)
                 */
                ResultSet resultSet;

                for (RefBean o : sampleGenes) {
                    PreparedStatement preparedStatement2 = connection.prepareStatement(statement2);
                    String location  = o.getLocation();
                    if (location.indexOf("Range")==0) { continue; } // if not SNP from the sample, do not match.
                    preparedStatement2.setString(1, location);

                    resultSet = preparedStatement2.executeQuery();
                    while (resultSet.next()) {
                        RefBean newrow = new RefBean(o.getLocation(),o.getAllele(),o.getOri_gene(),o.getSym_gene());
                        filtered.add(newrow);
                    }
                }

                log.info("Processed SNP mapping: " + sampleGenes.size() + ". Found match: " + filtered.size());

                if (filtered.size()>0){ // row number, nothing found in sample VEP???
                    List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();

                    for (RefBean obj : filtered){
                        for (ClinicAnnBean clinicAnnBean:refClinicAnns){

                            String location = obj.getLocation(); // SNP name (location in ClinicAnnBean)
                            if (clinicAnnBean.getLocation()==null){ continue; }
                            if (clinicAnnBean.getLocation().contains(location)){

                                if (!matchedClinicAnnBeans.contains(clinicAnnBean)){
                                    matchedClinicAnnBeans.add(clinicAnnBean);
                                }

                                String gene = obj.getSym_gene();
                                updateSampleReturn(matched_sampleInfo, obj, gene);
                            }
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to: " + matched_sampleInfo.size() + "sample SNPs.");

        rt.add(matchedClinicAnnBeans);
        rt.add(matched_sampleInfo);
        return matchedClinicAnnBeans;
    }

    private List<DrugLabelBean> doMatchDrugLabel(List<RefBean> refGenes, List<DrugLabelBean> drugLabelBeans) {
        /**
         * @Description
         * Match genes corresponding to variants in the sample with drug label
         * @Param sampleGenes
         * @Return list of drug label beans
         * @Date 2020/5/16
         * @author DST group 2
         */
        List<DrugLabelBean> matchedLabels = new ArrayList<>();
        HashSet<String> matchedgene =new HashSet<>();
        for (DrugLabelBean drugLabelBean : drugLabelBeans) {
            boolean matched = false;

            for (RefBean ref: refGenes) {
                String gene=ref.getSym_gene();
                if (gene!=null && gene.length()!=0){
                    if (!gene.equals("-") && drugLabelBean.getSummary_markdown().contains(gene)) {
                        matched = true;
                        drugLabelBean.setvariantGene(gene);
                        matchedgene.add(gene);
                    }
                }
            }
            if (matched) {
                if (!matchedLabels.contains(drugLabelBean)) {
                    matchedLabels.add(drugLabelBean);
                }}
        }
        log.info("matched labels: "+matchedLabels.size()+"\n"+"matched gene: "+matchedgene.size());
        return matchedLabels;
    }

    private List<DosingGuidelineBean> doMatchDosingGuideline(List<RefBean> refGenes, List<DosingGuidelineBean> dosingGuidelineBeans) {
        /**
         * @Description
         * Match genes corresponding to variants in the sample with dosing guideline
         * @Param sampleGenes
         * @Return list of dosing guideline beans
         * @Date 2020/5/16
         * @author DST group 2
         */
        List<DosingGuidelineBean> matchedGuidelines = new ArrayList<>();
        Set<String> matchedgene=new HashSet<>();
        for (DosingGuidelineBean guideline : dosingGuidelineBeans) {
            boolean matched = false;
            for (RefBean ref: refGenes) {
                String gene=ref.getSym_gene();
                if (gene!=null && gene.length()!=0) {
                    if (!gene.equals("-") && guideline.getSummary_markdown().contains(gene)) {
                        matched = true;
                        guideline.setVariant_gene(gene);
                        matchedgene.add(gene);
                    }
                }
            }
            if (matched) {
                if (!matchedGuidelines.contains(guideline)) {
                    matchedGuidelines.add(guideline);
                }}

        }
        log.info("matched guidelines: "+matchedGuidelines.size()+"\n"+"matched genes: "+matchedgene.size());
        return matchedGuidelines;
    }

    private List<VarDrugAnnBean> doMatchVarDrugAnn(List<RefBean> refGenes, List<VarDrugAnnBean> varDrugAnnBeans) {
        /**
         * @Description
         * Match genes corresponding to variants in the sample with variant drug annotation
         * @Param sampleGenes
         * @Return list of variant drug annotation beans
         * @Date 2020/5/16
         * @author DST group 2
         */
        List<VarDrugAnnBean> matchedAnns=new ArrayList<>();
        Set<String> matchedgene=new HashSet<>();
        for (VarDrugAnnBean ann: varDrugAnnBeans) {
            boolean matched = false;
            String Gene=ann.getGene();
            if (Gene != null){
                for (RefBean ref: refGenes) {
                    String gene=ref.getSym_gene();
                    if (gene!=null && gene.length()!=0){
                        if (!gene.equals("-") && Gene.contains(gene)) {
                            matched = true;
                            matchedgene.add(gene);
                        }
                    }
                }
                if (matched) {
                    if (!matchedAnns.contains(ann)) { matchedAnns.add(ann); }
                }
            }
        }
        log.info("matched annotations: "+matchedAnns.size()+"\n"+"matched genes: "+matchedgene.size());
        return matchedAnns;
    }

    @RequestMapping({"/search","/upload/search"})
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView search=new ModelAndView();
        Map<String, Object> map=w.getModel();
        matchedDrugLabelBean = (List<DrugLabelBean>) map.get("matchedDrugLabel");
        matchedGuidelines= (List<DosingGuidelineBean>) map.get("matchedDosingGuideline");
        matchedAnns= (List<VarDrugAnnBean>) map.get("matchedVarDrugAnn");
        matched_clinic_ann_by_gene= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_gene");
        matched_clinic_ann_by_snp= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_snp");
//        matchedClinicAnns= (List<ClinicAnnBean>) map.get("matchedClinicalAnnotation");
//        matched_SNP_Anns= (List<VarDrugAnn>) map.get("matched_SNP_Anns");
        SampleBean sampleBean = (SampleBean) map.get("sample");
        log.info("searchDrug");
        String drug=request.getParameter("drug");
        String phen=request.getParameter("Phenotype");
        log.info(drug);
        log.info(phen);

        List<DrugLabelBean> filteredDrugLabelBean =null;
        List<DosingGuidelineBean> filteredDosingGuidelineBean =null;
        List<VarDrugAnnBean> filteredVarDrugAnnBean =null;
        List<ClinicAnnBean> filteredClinicAnnByGene=null;
        List<ClinicAnnBean> filteredClinicAnnBySNP=null;


        filteredDrugLabelBean = drugLabelDAO.search(drug,phen, matchedDrugLabelBean);
        filteredDosingGuidelineBean = dosingGuidelineDAO.search(drug,phen, matchedGuidelines);
        filteredVarDrugAnnBean = varDrugAnnDAO.search(drug,phen,matchedAnns);
        filteredClinicAnnByGene = clinicAnnDAO.search(drug,phen,matched_clinic_ann_by_gene);
        filteredClinicAnnBySNP = clinicAnnDAO.search(drug,phen,matched_clinic_ann_by_snp);

        //jsp
        search.addObject("filteredDrugLabel", filteredDrugLabelBean);
        search.addObject("filteredDosingGuideline", filteredDosingGuidelineBean);
        search.addObject("filteredVarDrugAnn", filteredVarDrugAnnBean);
        search.addObject("filteredClinicAnnByGene",filteredClinicAnnByGene);
        search.addObject("filteredClinicAnnBySNP",filteredClinicAnnBySNP);
        search.addObject("sample", sampleBean);

        //search.addObject("filtered_SNP_Anns",filtered_SNP_Ann);
        //request.getRequestDispatcher("/view/searchDrug.jsp").forward(request, response);
        search.setViewName("searchDrug");
        return search;
    }

//    @RequestMapping("/download")
//    public ModelAndView download(){  // TODO: implement matching result download
//        Map<String, Object> map=w.getModel();
//        matchedDrugLabelBean = (List<DrugLabelBean>) map.get("matchedDrugLabel");
//        matchedGuidelines= (List<DosingGuidelineBean>) map.get("matchedDosingGuideline");
//        matchedAnns= (List<VarDrugAnnBean>) map.get("matchedVarDrugAnn");
//        matched_clinic_ann_by_gene= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_gene");
//        matched_clinic_ann_by_snp= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_snp");
//        System.out.println(matchedDrugLabelBean);
//        CSVUtils csv=new CSVUtils();
//        List DrugLabel = new ArrayList<Map>();
//        LinkedHashMap LabelMap=new LinkedHashMap();
//        List DosingGuideline = new ArrayList<Map>();
//        LinkedHashMap GuidelineMap=new LinkedHashMap();
//        List ClinicAnn = new ArrayList<Map>();
//        List ClinicAnn_SNP = new ArrayList<Map>();
//        LinkedHashMap ClinicAnnMap=new LinkedHashMap();
//        List VarDrugAnn=new ArrayList<Map>();
//        LinkedHashMap VarDrugAnnMap=new LinkedHashMap();
//
//        for (VarDrugAnnBean ann:matchedAnns) {
//            Map row=new LinkedHashMap<String,String>();
//            row.put("1",ann.getVariantID());
//            row.put("2",ann.getLocation());
//            row.put("3",ann.getGene());
//            row.put("4",ann.getDrug());
//            row.put("5",ann.getNotes());
//            row.put("6",ann.getAnnotation());
//        }
//        VarDrugAnnMap.put("1","variant id");
//        VarDrugAnnMap.put("2","location");
//        VarDrugAnnMap.put("3","gene");
//        VarDrugAnnMap.put("4","drug");
//        VarDrugAnnMap.put("5","notes");
//        VarDrugAnnMap.put("6","summary");
//        for (ClinicAnnBean clinic:matched_clinic_ann_by_gene) {
//            Map row=new LinkedHashMap<String,String>();
//            row.put("1",clinic.getLocation());
//            row.put("2",clinic.getGene());
//            row.put("3",clinic.getChromosome());
//            row.put("4",clinic.getRelated_chemicals());
//            row.put("5",clinic.getRelated_diseases());
//            row.put("6",clinic.getAnnotation_text());
//            ClinicAnn.add(row);
//        }
//        for (ClinicAnnBean clinic:matched_clinic_ann_by_snp) {
//            Map row=new LinkedHashMap<String,String>();
//            row.put("1",clinic.getLocation());
//            row.put("2",clinic.getGene());
//            row.put("3",clinic.getChromosome());
//            row.put("4",clinic.getRelated_chemicals());
//            row.put("5",clinic.getRelated_diseases());
//            row.put("6",clinic.getAnnotation_text());
//            ClinicAnn_SNP.add(row);
//        }
//        ClinicAnnMap.put("1","variant id");
//        ClinicAnnMap.put("2","gene");
//        ClinicAnnMap.put("3","chromosome");
//        ClinicAnnMap.put("4","drug");
//        ClinicAnnMap.put("5","disease");
//        ClinicAnnMap.put("6","summary");
//
//        for (DrugLabelBean label: matchedDrugLabelBean) {
//            Map row = new LinkedHashMap<String, String>();
//            row.put("1",label.getvariantGene());
//            row.put("2",label.getDrugName());
//            row.put("3",label.getSource());
//            row.put("4",label.gethasAlternativeDrug());
//            row.put("5",label.getSummary_markdown());
//            DrugLabel.add(row);
//        }
//        LabelMap.put("1","variant");
//        LabelMap.put("2","drug");
//        LabelMap.put("3","source");
//        LabelMap.put("4","has alternative drug");
//        LabelMap.put("5","summary");
//
//        for (DosingGuidelineBean label:matchedGuidelines) {
//            Map row = new LinkedHashMap<String, String>();
//            row.put("1",label.getVariant_gene());
//            row.put("2",label.getName());
//            row.put("3",label.getDrug());
//            row.put("4",label.getSource());
//            row.put("5",label.isRecommendation());
//            row.put("5",label.getSummary_markdown());
//            DosingGuideline.add(row);
//        }
//        GuidelineMap.put("1","variant");
//        GuidelineMap.put("2","guideline name");
//        GuidelineMap.put("3","drug");
//        GuidelineMap.put("4","source");
//        GuidelineMap.put("5","recommendation");
//        GuidelineMap.put("6","summary");
//
//        csv.createCSVFile(DrugLabel,LabelMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matchedDrugLabel");
//        csv.createCSVFile(DosingGuideline,GuidelineMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matchedGuidelines");
//        csv.createCSVFile(ClinicAnn,ClinicAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_clinic_ann_by_gene");
//        csv.createCSVFile(ClinicAnn_SNP,ClinicAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_clinic_ann_by_snp");
//        csv.createCSVFile(VarDrugAnn,VarDrugAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_variant_drug_annotation");
//
//        w.setViewName("index");
//        return w;
//
//    }

    private void updateSampleReturn(HashMap<String, HashMap<String, String>> matched_sampleInfo, RefBean row, String gene) {

        // refactored by IDEA automatically

        if (matched_sampleInfo.containsKey(gene)){
            matched_sampleInfo.get(gene).put(row.getLocation(), row.getAllele());
        } else {
            HashMap<String, String> submap = new HashMap<>();
            submap.put(row.getLocation(), row.getAllele());
            matched_sampleInfo.put(gene,submap);
        }
    }
}
