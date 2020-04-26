package DST2.Group2.Controller;
import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import DST2.Group2.DAO.*;
import DST2.Group2.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import DST2.Group2.Controller.CSVUtils;
@MultipartConfig
@Controller
public class MatchDrugLabel  {
	private static final Logger log = LoggerFactory.getLogger(MatchDrugLabel.class);
    private ClinicAnnDAO clinicAnnDAO = new ClinicAnnDAO();
    private HashMap<String, String[]> target = new HashMap<>();

    @RequestMapping("/matchingIndex")
	public String matchingIndex(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	log.info("matchingindex");
        return "matching_index";
    }
    @RequestMapping("/samples")
    public ModelAndView samples(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	log.info("samples");
    	ModelAndView mv=new ModelAndView();
    	mv.setViewName("samples");
    	List<Sample> samples = SampleDAO.findAll();
    	//pass to jsp
        mv.addObject("samples",samples);
        return mv;
    }
	public static List<DrugLabel> matchedDrugLabel =null;
	public static List<DosingGuideline> matchedGuidelines =null;
	public static List<VarDrugAnn> matchedAnns=null;
    public static List<ClinicAnnBean> matchedClinicAnns=null;
    public static List<VarDrugAnn> matched_SNP_Anns=null;

    public static ModelAndView w=new ModelAndView();

    @RequestMapping("/matching")

    public ModelAndView matching(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //set in jsp
		matchedDrugLabel =null;
		matchedGuidelines =null;
		matchedAnns=null;
		matchedClinicAnns=null;
        matched_SNP_Anns=null;
		log.info("matching");

        w.setViewName("matching_index_search");
		String sampleIdParameter = request.getParameter("sampleId");
		log.info(sampleIdParameter+" =sampleparameter");
        if (sampleIdParameter == null) {
                log.info("sample id parameter is null");
                ModelAndView s=new ModelAndView();
                s.setViewName("samples");
        return s;
        }
        Integer sampleId = null;
        try {
            sampleId = Integer.valueOf(sampleIdParameter);
        } catch (NumberFormatException e) {
            log.info(String.valueOf(e));
            ModelAndView s=new ModelAndView();
            s.setViewName("samples");
            return s;
        }
        log.info("get reference genes");
        List<String> refGenes = VcfDAO.getRefs(sampleId);
        log.info("get reference locations");
        List<String> locs=VcfDAO.getloc(sampleId);
        if (refGenes.isEmpty()) {
            log.info("reference gene set is empty");
            ModelAndView s=new ModelAndView();
            s.setViewName("samples");
            return s;
        }
        log.info("getdruglabels");
        List<DrugLabel> drugLabels = DrugLabelDAO.getDrugLabel();
        log.info("getguidelines");
        List<DosingGuideline> dosingGuidelines = DosingGuidelineDAO.getDosingGuideline();
        log.info("getanns");
        List<VarDrugAnn> VarDrugAnns=VarDrugAnnDAO.getAnn();
        log.info("getClinicAnns");
        List<ClinicAnnBean> ClinicAnns=ClinicAnnDAO.getClinicAnn();
        log.info("matchlabel");
        List<DrugLabel> matchedDrugLabel = doMatchDrugLabel(refGenes, drugLabels);
        log.info("matchguideline");
        List<DosingGuideline> matchedDosingGuideline = doMatchDosingGuideline(refGenes, dosingGuidelines);
        log.info("matchann");
        List<VarDrugAnn> matchedAnn=doMatchVarDrugAnn(refGenes,VarDrugAnns);
        log.info("matchedClinicAnn");
        List<ClinicAnnBean> matchedClinicAnns=doMatchClinic_by_Gene(refGenes,ClinicAnns);
        log.info("matched by SNP");

        //List<VarDrugAnn> matched_SNP_Anns=doMatchAnn_by_SNP(locs,VarDrugAnns);
        log.info("finished");


        //pass to jsp
        w.addObject("matchedDrugLabel", matchedDrugLabel);
        w.addObject("matchedDosingGuideline", matchedDosingGuideline);
        w.addObject("matchedVarDrugAnn",matchedAnn);
        w.addObject("sample", SampleDAO.findById(sampleId));
        w.addObject("matchedClinicalAnnotation",matchedClinicAnns);
        //w.addObject("matched_SNP_Anns",matched_SNP_Anns);
        return w;
	}
	
	
	private List<DrugLabel> doMatchDrugLabel(List<String> refGenes,List<DrugLabel> drugLabels) {
		List<DrugLabel> matchedLabels = new ArrayList<>();
        for (DrugLabel drugLabel : drugLabels) {
            boolean matched = false;
            for (String gene: refGenes) {
                if (drugLabel.getSummary_markdown().contains(gene)) {

                    matched = true;
                    drugLabel.setvariantGene(gene);
                }
            }
            if (matched) {
                if (!matchedLabels.contains(drugLabel)) {
                matchedLabels.add(drugLabel);
            }}
        }
        log.info("matched labels"+matchedLabels.size());
        return matchedLabels;
    }
	
	
	private List<DosingGuideline> doMatchDosingGuideline(List<String> refGenes, List<DosingGuideline> dosingGuidelines) {
		List<DosingGuideline> matchedGuidelines = new ArrayList<>();
        for (DosingGuideline guideline : dosingGuidelines) {
            boolean matched = false;
            for (String gene: refGenes) {
                if (guideline.getSummary_markdown().contains(gene)) {
                    matched = true;
                    guideline.setVariant_gene(gene);
                }
            }
            if (matched) {
                if (!matchedGuidelines.contains(guideline)) {
            	matchedGuidelines.add(guideline);
            }}

        }
        log.info("matched guidelines"+matchedGuidelines.size());
        return matchedGuidelines;
    }
	
	private List<VarDrugAnn> doMatchVarDrugAnn(List<String> refGenes,List<VarDrugAnn> VarDrugAnns) {
		List<VarDrugAnn> matchedAnns=new ArrayList<>();
		for (VarDrugAnn ann:VarDrugAnns) {
			boolean matched = false;
			String Gene=ann.getGene();
            for (String gene: refGenes) {
                if (Gene.contains(gene)) {
                    matched = true;
                }
            }
            if (matched) {
                if (!matchedAnns.contains(ann)) {
            	matchedAnns.add(ann);
            }}
		}
		log.info("matched annotations"+matchedAnns.size());
		return matchedAnns;
	}


    private List<ClinicAnnBean> doMatchClinic_by_Gene(List<String> sampleGenes,List<ClinicAnnBean> ClinicAnn) {
        /**
         * Part 2b: match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
         */
        List<ClinicAnnBean> matchedClinicAnns=new ArrayList<>();
        for (ClinicAnnBean ann:ClinicAnn) {
            boolean matched = false;
            String Gene=ann.getGene();
            for (String gene: sampleGenes) {
                if (Gene.contains(gene)) {
                    matched = true;
                }
            }
            if (matched) {
                if (!matchedClinicAnns.contains(ann)) {
                matchedClinicAnns.add(ann);
            }}
        }
        log.info("matched clinic annotations"+matchedClinicAnns.size());
        return matchedClinicAnns;

    }
    private List<VarDrugAnn> doMatchAnn_by_SNP(List<String> locs,List<VarDrugAnn> Anns) {
        List<VarDrugAnn> matched_SNP_Anns=new ArrayList<>();
        for (VarDrugAnn ann:Anns) {
            boolean matched = false;
            String annlocation=ann.getLocation();
            if (annlocation!=null ) {
                if (annlocation!=""){
            String location=annlocation.split(":")[1];
            int chromosome= Integer.parseInt(annlocation.split(":")[0].split("_")[1].split("[.]")[0]);

            for (String loc: locs) {
                int refchromosome= Integer.parseInt(loc.split(":")[0]);
                String refloc=loc.split(":")[1];

                if (chromosome==refchromosome) {
                    if (location.equals(refloc)) {
//
                    matched = true;
                }}

            }
                }
            } else {

            }
            if (matched) {
                if (!matched_SNP_Anns.contains(ann)) {
                    matched_SNP_Anns.add(ann);
                }}
        }
        log.info("matched clinic annotations"+matched_SNP_Anns.size());
        return matched_SNP_Anns;
    }
    @RequestMapping("/search")
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        ModelAndView search=new ModelAndView();
        Map<String, Object> map=w.getModel();
        matchedDrugLabel= (List<DrugLabel>) map.get("matchedDrugLabel");
        matchedGuidelines= (List<DosingGuideline>) map.get("matchedDosingGuideline");
        matchedAnns= (List<VarDrugAnn>) map.get("matchedVarDrugAnn");
        matchedClinicAnns= (List<ClinicAnnBean>) map.get("matchedClinicalAnnotation");
        matched_SNP_Anns= (List<VarDrugAnn>) map.get("matched_SNP_Anns");
        Sample sample= (Sample) map.get("sample");
        log.info("searchDrug");
        String drug=request.getParameter("drug");
        String phen=request.getParameter("Phenotype");
        log.info(drug);
        log.info(phen);

        List<DrugLabel> filteredDrugLabel =null;
        List<DosingGuideline> filteredDosingGuideline =null;
        List<VarDrugAnn> filteredVarDrugAnn=null;
        List<ClinicAnnBean> filteredClinicAnn=null;
        List<VarDrugAnn> filtered_SNP_Ann=null;


        filteredDrugLabel=DrugLabelDAO.search(drug,phen,matchedDrugLabel);
        filteredDosingGuideline=DosingGuidelineDAO.search(drug,phen, matchedGuidelines);
        System.out.println(matchedAnns+"  matched drug");
        filteredVarDrugAnn=VarDrugAnnDAO.search(drug,phen,matchedAnns);
        filteredClinicAnn=ClinicAnnDAO.search(drug,phen,matchedClinicAnns);
        //filtered_SNP_Ann=VarDrugAnnDAO.search(drug,phen,matched_SNP_Anns);


        //jsp
        search.addObject("filteredDrugLabel",filteredDrugLabel);
        search.addObject("filteredDosingGuideline", filteredDosingGuideline);
        search.addObject("filteredVarDrugAnn",filteredVarDrugAnn);
        search.addObject("filteredClinicAnn",filteredClinicAnn);
        search.addObject("sample", sample);

        //search.addObject("filtered_SNP_Anns",filtered_SNP_Ann);
        //request.getRequestDispatcher("/view/searchDrug.jsp").forward(request, response);
        search.setViewName("searchDrug");
        return search;
    }

    @RequestMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String, Object> map=w.getModel();
        matchedDrugLabel= (List<DrugLabel>) map.get("matchedDrugLabel");
        matchedGuidelines= (List<DosingGuideline>) map.get("matchedDosingGuideline");
        matchedAnns= (List<VarDrugAnn>) map.get("matchedVarDrugAnn");
        matchedClinicAnns= (List<ClinicAnnBean>) map.get("matchedClinicalAnnotation");
        CSVUtils csv=new CSVUtils();
        List exportData = new ArrayList<Map>();
        LinkedHashMap LabelMap=new LinkedHashMap();
        for (DrugLabel label:matchedDrugLabel) {
            Map row = new LinkedHashMap<String, String>();
            row.put("1",label.getvariantGene());
            row.put("2",label.getDrugName());
            row.put("3",label.getSource());
            row.put("4",label.gethasAlternativeDrug());
            row.put("5",label.getSummary_markdown());
            exportData.add(row);
        }
        LabelMap.put("1","variant");
        LabelMap.put("2","drug");
        LabelMap.put("3","source");
        LabelMap.put("4","has alternative drug");
        LabelMap.put("5","summary");

        csv.createCSVFile(exportData,LabelMap,"C:/Users/jxm72/Desktop/","matchedDrugLabel");

    }


}
