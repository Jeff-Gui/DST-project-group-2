package DST2.Group2.Controller;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DST2.Group2.DAO.DrugLabelDAO;
import DST2.Group2.DAO.VarDrugAnnDAO;
import DST2.Group2.DAO.VcfDAO;
import DST2.Group2.DAO.dosingGuidelineDAO;
import DST2.Group2.DAO.sampleDAO;
import DST2.Group2.bean.ClinicAnnBean;
import DST2.Group2.bean.DosingGuideline;
import DST2.Group2.bean.DrugLabel;
import DST2.Group2.bean.Sample;
import DST2.Group2.bean.VarDrugAnn;
import DST2.Group2.servlet.DispatchServlet;
@MultipartConfig
public class MatchDrugLabel  {
	private static final Logger log = LoggerFactory.getLogger(MatchDrugLabel.class);

	public void register(DispatchServlet.Dispatcher dispatcher) {
		//map urls
        //dispatcher.registerPostMapping("/upload", this::uploadVcfOutput);
        dispatcher.registerGetMapping("/matchingIndex", this::matchingIndex);
        dispatcher.registerGetMapping("/matching", this::matching);
       // dispatcher.registerGetMapping("/searchDrug", this::searchDrug);
        dispatcher.registerGetMapping("/searchPhen", this::searchPhen);
        dispatcher.registerGetMapping("/samples", this::samples);
        //dispatcher.registerPostMapping("/searchDrug", search::searchdrug);
       
    }
	public void matchingIndex(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("matchingindex");
    	request.getRequestDispatcher("/pages/matching_index.jsp").forward(request, response);
    }
	public void samples(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("samples");
    	List<Sample> samples = sampleDAO.findAll();
    	//pass to jsp
        request.setAttribute("samples", samples);
        request.getRequestDispatcher("/pages/samples.jsp").forward(request, response);
    }
	public static List<DrugLabel> matchedDrugLabel =null;
	public static List<DosingGuideline> matchedGuidelines =null;
	public static List<VarDrugAnn> matchedAnns=null;
	
	public void matching(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //set in jsp
		matchedDrugLabel =null;
		matchedGuidelines =null;
		matchedAnns=null;
		System.out.println("matching");
		String sampleIdParameter = request.getParameter("sampleId");
        if (sampleIdParameter == null) {
            request.getRequestDispatcher("/pages/samples.jsp").forward(request, response);
            return;
        }
        Integer sampleId = null;
        try {
            sampleId = Integer.valueOf(sampleIdParameter);
        } catch (NumberFormatException e) {
            response.sendRedirect("samples");
            return;
        }
        List<String> refGenes = VcfDAO.getRefs(sampleId);
        if (refGenes.isEmpty()) {
            response.sendRedirect("samples");
            return;
        }
        System.out.println("getdruglabels");
        List<DrugLabel> drugLabels = DrugLabelDAO.getDrugLabel();
        System.out.println("getguidelines");
        List<DosingGuideline> dosingGuidelines = dosingGuidelineDAO.getDosingGuideline();
        System.out.println("getanns");
        List<VarDrugAnn> VarDrugAnns=VarDrugAnnDAO.getAnn();
        System.out.println("matchlabel");
        List<DrugLabel> matchedDrugLabel = doMatchDrugLabel(refGenes, drugLabels);
        System.out.println("matchguideline");
        final List<DosingGuideline> matchedDosingGuideline = doMatchDosingGuideline(refGenes, dosingGuidelines);
        System.out.println("matchann");
        final List<VarDrugAnn> matchedAnn=doMatchVarDrugAnn(refGenes,VarDrugAnns);
        log.info("finished");

        //pass to jsp
        
        request.setAttribute("matchedDrugLabel", matchedDrugLabel);
        request.setAttribute("matchedDosingGuideline", matchedDosingGuideline);
        request.setAttribute("matchedVarDrugAnn",matchedAnn);
        request.setAttribute("sample", sampleDAO.findById(sampleId));
        request.getRequestDispatcher("/pages/matching_index_search.jsp").forward(request, response);
       
	}
	
	
	private List<DrugLabel> doMatchDrugLabel(List<String> refGenes,List<DrugLabel> drugLabels) {
		List<DrugLabel> matchedLabels = new ArrayList<>();
        for (DrugLabel drugLabel : drugLabels) {
            boolean matched = false;
            for (String gene: refGenes) {
                if (drugLabel.getSummary_markdown().contains(gene)) {
                	//System.out.println("matched");
                    matched = true;
                    drugLabel.setvariantGene(gene);
                }
            }
            if (matched) {
                matchedLabels.add(drugLabel);
            }
        }
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
            	matchedGuidelines.add(guideline);
            }
        }
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
            	
            	matchedAnns.add(ann);
            	
            }
		}
		return matchedAnns;
	}
	
	
//	private ArrayList<Object> doMatchClinic_by_Gene(ArrayList<ArrayList<String>> sampleGenes){
//        /**
//         * Part 2b: match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
//         */
//        ArrayList<Object> rt = new ArrayList<>();
//        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
//        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}
//
//        List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();
//
////        int counter=0;
//        for (Object obj : sampleGenes){
//            for (ClinicAnnBean clinicAnnBean:refClinicAnns){
////                counter++;
////                if (counter%100000000==0){
////                    System.out.println("processed: " + counter);
////                }
//                ArrayList<String> row = (ArrayList<String>) obj;
//                String gene = row.get(3); // gene symbol
//                if (clinicAnnBean.getGene()==null){ continue; }
//                if (clinicAnnBean.getGene().contains(gene)){
//
//                    if (!matchedClinicAnnBeans.contains(clinicAnnBean)){
//                        matchedClinicAnnBeans.add(clinicAnnBean);
//                    }
//
//                    updateSampleReturn(matched_sampleInfo, row, gene);
//                }
//            }
//        }
//
//        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to " + matched_sampleInfo.size() + " genes from the sample.");
//
//        rt.add(matchedClinicAnnBeans); // 787 records matched from sample
//        rt.add(matched_sampleInfo); // 185 genes matched from clinic annotation
//
//        return rt;
//    }
	public void searchDrug(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
		System.out.println("searchDrug");
    	String drug=request.getParameter("drug");
    	List<DrugLabel> filteredDrugLabel =null;
    	List<DosingGuideline> filteredDosingGuideline =null;
    	List<VarDrugAnn> filteredVarDrugAnn=null;
    	filteredDrugLabel=DrugLabelDAO.searchByDrug(drug, matchedDrugLabel);
    	filteredDosingGuideline=dosingGuidelineDAO.searchByDrug(drug, matchedGuidelines);
    	filteredVarDrugAnn=VarDrugAnnDAO.searchByDrug(drug,matchedAnns);
    	//jsp
    	request.setAttribute("matchedDrugLabel",filteredDrugLabel);
    	request.setAttribute("matchedDosingGuideline", filteredDosingGuideline);
    	request.setAttribute("matchedVarDrugAnn",filteredVarDrugAnn);
        request.getRequestDispatcher("/pages/matching_index_search.jsp").forward(request, response);

    	}
	public void searchPhen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
    	String phen=request.getParameter("phenotype");
    	List<DrugLabel> filteredDrugLabel =null;
    	List<DosingGuideline> filteredDosingGuideline =null;
    	List<VarDrugAnn> filteredVarDrugAnn=null;
    	filteredDrugLabel=DrugLabelDAO.searchByPhenotype(phen, matchedDrugLabel);
    	filteredDosingGuideline=dosingGuidelineDAO.searchByPhenotype(phen, matchedGuidelines);
    	filteredVarDrugAnn=VarDrugAnnDAO.searchByPhen(phen, matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel",filteredDrugLabel);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuideline);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
    	 	}
	
	
	
	
	public void uploadVcfOutput(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("uploadvcf");
    	
    	String uploadedBy = request.getParameter("uploaded_by");
//        if (uploadedBy == null || uploadedBy.isEmpty()) {
//        	System.out.println("isenpty");
//            request.setAttribute("validateError", "Uploaded by can not be blank");
//            request.getRequestDispatcher("/pages/matching_index_error.jsp").forward(request, response);
//            return;
//        }
        System.out.println("getpart");
        Part requestPart = request.getPart("vcf");
        if (requestPart == null) {
            request.setAttribute("validateError", "vcf output file can not be blank");
            request.getRequestDispatcher("/pages/matching_index_error.jsp").forward(request, response);
            return;
        }
        
        InputStream inputStream = requestPart.getInputStream();
        
        byte[] bytes = inputStream.readAllBytes();
        //System.out.println(bytes);

        String content = new String(bytes);
        //System.out.println(content);

        int sampleId = sampleDAO.save(uploadedBy);
        Sample s=new Sample(sampleId,new Timestamp(new Date().getTime()),uploadedBy);
        //System.out.println(sampleId);
        request.setAttribute("sample",s);
        VcfDAO.save(sampleId, content);
        response.sendRedirect("matching?sampleId=" + sampleId);
    }
	
	
	
	
	
	
	
	
}
