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
import DST2.Group2.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@MultipartConfig
@Controller
public class MatchDrugLabelController {

    private static final Logger log = LoggerFactory.getLogger(MatchDrugLabelController.class);

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
    private SampleDAO sampleDAO;

    public static ModelAndView w = new ModelAndView();
    public static List<DrugLabelBean> matchedDrugLabelBean;
    public static List<DosingGuidelineBean> matchedGuidelines;
    public static List<VarDrugAnn> matchedAnns=null;
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
        List<SampleBean> sampleBeans = SampleDAO.findAll();
        //pass to jsp
        mv.addObject("samples", sampleBeans);
        return mv;
    }

    @RequestMapping("/matching")
    private ModelAndView matching(@RequestParam("sampleId") String sampleIdParameter) {
        //set in jsp
        matchedDrugLabelBean =null;
        matchedGuidelines =null;
        matchedAnns=null;
        matched_clinic_ann_by_gene=null;
        matched_clinic_ann_by_snp=null;
        log.info("matching");

        w.setViewName("matching_index_search");

        log.info(sampleIdParameter+" =sampleparameter");

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

        log.info("get reference locations");
//        if (sampleType.equals("annovar")) {refBeans = AnnovarDAO.getsampleGenes(sampleId);} else {
//            if (sampleType.equals("vep")) {refBeans = VepDAO.getsampleGenes(sampleId);} else {
//                log.info("sample type is not correct.");
//                // to error page
//            }
//        }
        List<RefBean> refBeans = vepDAO.getsampleGenes(sampleId);
        if (refBeans.isEmpty()) {
            log.info("reference gene set is empty");
            ModelAndView s=new ModelAndView();
            s.setViewName("samples");
            return s;
        }

        log.info("getdruglabels");
        List<DrugLabelBean> drugLabelBeans = drugLabelDAO.getDrugLabel();
        log.info("getguidelines");
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.getDosingGuideline();
        log.info("getanns");
        List<VarDrugAnn> VarDrugAnns = varDrugAnnDAO.getAnn();
        log.info("matchlabel");
        List<DrugLabelBean> matchedDrugLabelBean = doMatchDrugLabel(refBeans, drugLabelBeans);
        log.info("matchguideline");
        List<DosingGuidelineBean> matchedDosingGuidelineBean = doMatchDosingGuideline(refBeans, dosingGuidelineBeans);
        log.info("matchann");
        List<VarDrugAnn> matchedAnn = doMatchVarDrugAnn(refBeans,VarDrugAnns);
        log.info("match clinic annotation by gene");
        List<ClinicAnnBean> matched_clinic_ann_by_gene = doMatchClinic_by_Gene(refBeans);
        log.info("match clinic annotation by SNP");
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
         * Part 2b: match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
         */
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();
        log.info("Filtered clinic annotation record total: " + refClinicAnns.size());

        int counter=0;
        for (RefBean obj : sampleGenes){
            boolean match=false;
            for (ClinicAnnBean clinicAnnBean:refClinicAnns){
//                counter++;
//                if (counter%100000000==0){
//                    System.out.println("processed: " + counter);
//                }
//                ArrayList<String> row = (ArrayList<String>) obj;
//                String gene = row.get(3); // gene symbol
                String gene=obj.getSym_gene();
                if (clinicAnnBean.getGene()==null){ continue; }
                if (gene!=null && gene.length()!=0) {
                    if (gene!="-" && clinicAnnBean.getGene().contains(gene)){

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
        log.info("matched sample info"+matched_sampleInfo);
        rt.add(matchedClinicAnnBeans); // 787 records matched from sample
        rt.add(matched_sampleInfo); // 185 genes matched from clinic annotation

        return matchedClinicAnnBeans;
    }

    private List<ClinicAnnBean> doMatchClinic_by_SNP(List<RefBean> sampleGenes) {
        /**
         * To map sample variant according to its exact genomic location.
         * Usually, this mapping is too strict to yield any positive result.
         * Therefore, mapping by location is not recommended to be used alone.
         */
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        DBmethods.execSQL(connection -> {
            String statement2 = "SELECT variant FROM variants WHERE location=? AND clianncount!=0;";
            ArrayList<RefBean> filtered = new ArrayList<>();
            try {
                /**
                 * Part 2a: match sample variant location with database, only variants with clinic annotations are considered.
                 * Query variant name ("rs" ID)
                 */
                ResultSet resultSet;

                for (RefBean o : sampleGenes) {

                    PreparedStatement preparedStatement2 = connection.prepareStatement(statement2);
                    //ArrayList<String> oldrow = (ArrayList<String>) o;
                    preparedStatement2.setString(1, o.getLocation());
                    resultSet = preparedStatement2.executeQuery();
                    while (resultSet.next()) {
                        RefBean newrow = new RefBean(o.getLocation(),o.getAllele(),o.getOri_gene(),o.getSym_gene());
                        //newrow.add(resultSet.getString(1)); // SNP name
                        filtered.add(newrow);
                    }
                }

                log.info("Processed SNP mapping: " + sampleGenes.size() + ". Found match: " + filtered.size());

                if (filtered.size()>0){ // row number, nothing found in sample VEP???
                    List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();

                    for (RefBean obj : filtered){
                        for (ClinicAnnBean clinicAnnBean:refClinicAnns){
                            //ArrayList<String> row = obj;
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

        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to: " + matched_sampleInfo + "sample SNPs.");

        rt.add(matchedClinicAnnBeans);
        rt.add(matched_sampleInfo);
        return matchedClinicAnnBeans;
    }

    private List<DrugLabelBean> doMatchDrugLabel(List<RefBean> refGenes, List<DrugLabelBean> drugLabelBeans) {
        List<DrugLabelBean> matchedLabels = new ArrayList<>();
        HashSet<String> matchedgene =new HashSet<>();
        for (DrugLabelBean drugLabelBean : drugLabelBeans) {
            boolean matched = false;

            for (RefBean ref: refGenes) {
                String gene=ref.getSym_gene();
                if (!gene.equals("-") && drugLabelBean.getSummary_markdown().contains(gene)) {
                    matched = true;
                    drugLabelBean.setvariantGene(gene);
                    matchedgene.add(gene);
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
        List<DosingGuidelineBean> matchedGuidelines = new ArrayList<>();
        Set<String> matchedgene=new HashSet<>();
        for (DosingGuidelineBean guideline : dosingGuidelineBeans) {
            boolean matched = false;
            for (RefBean ref: refGenes) {
                String gene=ref.getSym_gene();
                if (gene!="-" && guideline.getSummary_markdown().contains(gene)) {
                    matched = true;
                    guideline.setVariant_gene(gene);
                    matchedgene.add(gene);
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

    private List<VarDrugAnn> doMatchVarDrugAnn(List<RefBean> refGenes, List<VarDrugAnn> VarDrugAnns) {
        List<VarDrugAnn> matchedAnns=new ArrayList<>();
        Set<String> matchedgene=new HashSet<>();
        for (VarDrugAnn ann:VarDrugAnns) {
            boolean matched = false;
            String Gene=ann.getGene();
            for (RefBean ref: refGenes) {
                String gene=ref.getSym_gene();
                if (!gene.equals("-") && Gene.contains(gene)) {
                    matched = true;
                    matchedgene.add(gene);
                }
            }
            if (matched) {
                if (!matchedAnns.contains(ann)) {
                    matchedAnns.add(ann);
                }}
        }
        log.info("matched annotations: "+matchedAnns.size()+"\n"+"matched genes: "+matchedgene.size());
        return matchedAnns;
    }

    @RequestMapping("/search")
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub
        ModelAndView search=new ModelAndView();
        Map<String, Object> map=w.getModel();
        matchedDrugLabelBean = (List<DrugLabelBean>) map.get("matchedDrugLabel");
        matchedGuidelines= (List<DosingGuidelineBean>) map.get("matchedDosingGuideline");
        matchedAnns= (List<VarDrugAnn>) map.get("matchedVarDrugAnn");
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
        List<VarDrugAnn> filteredVarDrugAnn=null;
        List<ClinicAnnBean> filteredClinicAnnByGene=null;
        List<ClinicAnnBean> filteredClinicAnnBySNP=null;


        filteredDrugLabelBean = drugLabelDAO.search(drug,phen, matchedDrugLabelBean);
        filteredDosingGuidelineBean = dosingGuidelineDAO.search(drug,phen, matchedGuidelines);
        filteredVarDrugAnn = varDrugAnnDAO.search(drug,phen,matchedAnns);
        filteredClinicAnnByGene = clinicAnnDAO.search(drug,phen,matched_clinic_ann_by_gene);
        filteredClinicAnnBySNP = clinicAnnDAO.search(drug,phen,matched_clinic_ann_by_snp);

        //jsp
        search.addObject("filteredDrugLabel", filteredDrugLabelBean);
        search.addObject("filteredDosingGuideline", filteredDosingGuidelineBean);
        search.addObject("filteredVarDrugAnn",filteredVarDrugAnn);
        search.addObject("filteredClinicAnnByGene",filteredClinicAnnByGene);
        search.addObject("filteredClinicAnnBySNP",filteredClinicAnnBySNP);
        search.addObject("sample", sampleBean);

        //search.addObject("filtered_SNP_Anns",filtered_SNP_Ann);
        //request.getRequestDispatcher("/view/searchDrug.jsp").forward(request, response);
        search.setViewName("searchDrug");
        return search;
    }

    @RequestMapping("/download")
    public ModelAndView download(){
        Map<String, Object> map=w.getModel();
        matchedDrugLabelBean = (List<DrugLabelBean>) map.get("matchedDrugLabel");
        matchedGuidelines= (List<DosingGuidelineBean>) map.get("matchedDosingGuideline");
        matchedAnns= (List<VarDrugAnn>) map.get("matchedVarDrugAnn");
        matched_clinic_ann_by_gene= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_gene");
        matched_clinic_ann_by_snp= (List<ClinicAnnBean>) map.get("matched_clinic_ann_by_snp");
        System.out.println(matchedDrugLabelBean);
        CSVUtils csv=new CSVUtils();
        List DrugLabel = new ArrayList<Map>();
        LinkedHashMap LabelMap=new LinkedHashMap();
        List DosingGuideline = new ArrayList<Map>();
        LinkedHashMap GuidelineMap=new LinkedHashMap();
        List ClinicAnn = new ArrayList<Map>();
        List ClinicAnn_SNP = new ArrayList<Map>();
        LinkedHashMap ClinicAnnMap=new LinkedHashMap();
        List VarDrugAnn=new ArrayList<Map>();
        LinkedHashMap VarDrugAnnMap=new LinkedHashMap();

        for (VarDrugAnn ann:matchedAnns) {
            Map row=new LinkedHashMap<String,String>();
            row.put("1",ann.getVariantID());
            row.put("2",ann.getLocation());
            row.put("3",ann.getGene());
            row.put("4",ann.getDrug());
            row.put("5",ann.getNotes());
            row.put("6",ann.getAnnotation());
        }
        VarDrugAnnMap.put("1","variant id");
        VarDrugAnnMap.put("2","location");
        VarDrugAnnMap.put("3","gene");
        VarDrugAnnMap.put("4","drug");
        VarDrugAnnMap.put("5","notes");
        VarDrugAnnMap.put("6","summary");
        for (ClinicAnnBean clinic:matched_clinic_ann_by_gene) {
            Map row=new LinkedHashMap<String,String>();
            row.put("1",clinic.getLocation());
            row.put("2",clinic.getGene());
            row.put("3",clinic.getChromosome());
            row.put("4",clinic.getRelated_chemicals());
            row.put("5",clinic.getRelated_diseases());
            row.put("6",clinic.getAnnotation_text());
            ClinicAnn.add(row);
        }
        for (ClinicAnnBean clinic:matched_clinic_ann_by_snp) {
            Map row=new LinkedHashMap<String,String>();
            row.put("1",clinic.getLocation());
            row.put("2",clinic.getGene());
            row.put("3",clinic.getChromosome());
            row.put("4",clinic.getRelated_chemicals());
            row.put("5",clinic.getRelated_diseases());
            row.put("6",clinic.getAnnotation_text());
            ClinicAnn_SNP.add(row);
        }
        ClinicAnnMap.put("1","variant id");
        ClinicAnnMap.put("2","gene");
        ClinicAnnMap.put("3","chromosome");
        ClinicAnnMap.put("4","drug");
        ClinicAnnMap.put("5","disease");
        ClinicAnnMap.put("6","summary");

        for (DrugLabelBean label: matchedDrugLabelBean) {
            Map row = new LinkedHashMap<String, String>();
            row.put("1",label.getvariantGene());
            row.put("2",label.getDrugName());
            row.put("3",label.getSource());
            row.put("4",label.gethasAlternativeDrug());
            row.put("5",label.getSummary_markdown());
            DrugLabel.add(row);
        }
        LabelMap.put("1","variant");
        LabelMap.put("2","drug");
        LabelMap.put("3","source");
        LabelMap.put("4","has alternative drug");
        LabelMap.put("5","summary");

        for (DosingGuidelineBean label:matchedGuidelines) {
            Map row = new LinkedHashMap<String, String>();
            row.put("1",label.getVariant_gene());
            row.put("2",label.getName());
            row.put("3",label.getDrug());
            row.put("4",label.getSource());
            row.put("5",label.isRecommendation());
            row.put("5",label.getSummary_markdown());
            DosingGuideline.add(row);
        }
        GuidelineMap.put("1","variant");
        GuidelineMap.put("2","guideline name");
        GuidelineMap.put("3","drug");
        GuidelineMap.put("4","source");
        GuidelineMap.put("5","recommendation");
        GuidelineMap.put("6","summary");

        csv.createCSVFile(DrugLabel,LabelMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matchedDrugLabel");
        csv.createCSVFile(DosingGuideline,GuidelineMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matchedGuidelines");
        csv.createCSVFile(ClinicAnn,ClinicAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_clinic_ann_by_gene");
        csv.createCSVFile(ClinicAnn_SNP,ClinicAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_clinic_ann_by_snp");
        csv.createCSVFile(VarDrugAnn,VarDrugAnnMap,"C:/Users/jxm72/Desktop/semester 4/DST2/testfiles/","matched_variant_drug_annotation");

        w.setViewName("index");
        return w;

    }

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
