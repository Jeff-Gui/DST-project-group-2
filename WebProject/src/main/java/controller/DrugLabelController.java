package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.DrugLabelDAO;
import dao.VarDrugAnnDAO;
import dao.dosingGuidelineDAO;
import dao.SampleDAO;
import bean.DosingGuidelineBean;
import bean.DrugLabelBean;
import bean.SampleBean;
import bean.VarDrugAnnBean;
import DST2.Group2.servlet.DispatchServlet;
import dao.VepDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vep/labelAndguideline")
public class DrugLabelController {
	public void register(DispatchServlet.Dispatcher dispatcher) {
		//map urls
        dispatcher.registerPostMapping("/upload", this::uploadVcfOutput);
        dispatcher.registerGetMapping("/matchingIndex", this::matchingIndex);
        dispatcher.registerGetMapping("/matching", this::matching);
        dispatcher.registerGetMapping("/searchDrug", this::searchDrug);
        dispatcher.registerGetMapping("/searchPhen", this::searchPhen);
        dispatcher.registerGetMapping("/samples", this::samples);
       
    }
	public void matchingIndex(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("matchingindex");
    	request.getRequestDispatcher("/matching_index.jsp").forward(request, response);
    }

    @RequestMapping("/samples")
	public void samples(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("samples");
    	List<SampleBean> samples = SampleDAO.findAll();
    	//pass to jsp
        request.setAttribute("samples", samples);
        request.getRequestDispatcher("/samples.jsp").forward(request, response);
    }
	List<DrugLabelBean> matchedDrugLabelBean =null;
	List<DosingGuidelineBean> matchedGuidelines =null;
	List<VarDrugAnnBean> matchedAnns=null;
    private VepDAO vepDAO = new VepDAO();

    @RequestMapping("/matching")
	public void matching(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //set in jsp
		String sampleIdParameter = request.getParameter("sampleId");
        if (sampleIdParameter == null) {
            request.getRequestDispatcher("/samples.jsp").forward(request, response);
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
        List<DrugLabelBean> drugLabelBeans = DrugLabelDAO.getDrugLabel();
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.getDosingGuideline();
        List<VarDrugAnnBean> VarDrugAnns=VarDrugAnnDAO.getAnn();

        List<DrugLabelBean> matchedDrugLabelBean = doMatchDrugLabel(refGenes, drugLabelBeans);
        List<DosingGuidelineBean> matchedDosingGuidelineBean = doMatchDosingGuideline(refGenes, dosingGuidelineBeans);
        List<VarDrugAnnBean> matchedAnn=doMatchVarDrugAnn(refGenes,VarDrugAnns);
        //pass to jsp
        request.setAttribute("matchedDrugLabel", matchedDrugLabelBean);
        request.setAttribute("matchedDosingGuideline", matchedDosingGuidelineBean);
        request.setAttribute("matchedVarDrugAnn",matchedAnn);
        request.setAttribute("sample", SampleDAO.findById(sampleId));
        request.getRequestDispatcher("/matching_index_search.jsp").forward(request, response);
    }
	
	private List<DrugLabelBean> doMatchDrugLabel(List<String> refGenes, List<DrugLabelBean> drugLabelBeans) {
		List<DrugLabelBean> matchedLabels = new ArrayList<>();
        for (DrugLabelBean drugLabelBean : drugLabelBeans) {
            boolean matched = false;
            for (String gene: refGenes) {
                if (drugLabelBean.getSummary_markdown().contains(gene)) {
                    matched = true;
                    drugLabelBean.setvariantGene(gene);
                }
            }
            if (matched) {
                matchedLabels.add(drugLabelBean);
            }
        }
        return matchedLabels;
    }
	
	
	private List<DosingGuidelineBean> doMatchDosingGuideline(List<String> refGenes, List<DosingGuidelineBean> dosingGuidelineBeans) {
		List<DosingGuidelineBean> matchedGuidelines = new ArrayList<>();
        for (DosingGuidelineBean guideline : dosingGuidelineBeans) {
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
	
	private List<VarDrugAnnBean> doMatchVarDrugAnn(List<String> refGenes,List<VarDrugAnnBean> VarDrugAnns) {
		List<VarDrugAnnBean> matchedAnns=new ArrayList<>();
		for (VarDrugAnnBean ann:VarDrugAnns) {
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

	@RequestMapping("/searchDrug")
	public void searchDrug(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
    	String drug=request.getParameter("drug");
    	List<DrugLabelBean> filteredDrugLabelBean =null;
    	List<DosingGuidelineBean> filteredDosingGuidelineBean =null;
    	List<VarDrugAnnBean> filteredVarDrugAnn=null;
    	filteredDrugLabelBean =DrugLabelDAO.searchByDrug(drug, matchedDrugLabelBean);
    	filteredDosingGuidelineBean =dosingGuidelineDAO.searchByDrug(drug, matchedGuidelines);
    	filteredVarDrugAnn=VarDrugAnnDAO.searchByDrug(drug,matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel", filteredDrugLabelBean);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuidelineBean);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
    	}

    @RequestMapping("/searchPhen")
	public void searchPhen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
    	String phen=request.getParameter("phenotype");
    	List<DrugLabelBean> filteredDrugLabelBean =null;
    	List<DosingGuidelineBean> filteredDosingGuidelineBean =null;
    	List<VarDrugAnnBean> filteredVarDrugAnn=null;
    	filteredDrugLabelBean =DrugLabelDAO.searchByPhenotype(phen, matchedDrugLabelBean);
    	filteredDosingGuidelineBean =dosingGuidelineDAO.searchByPhenotype(phen, matchedGuidelines);
    	filteredVarDrugAnn=VarDrugAnnDAO.searchByPhen(phen, matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel", filteredDrugLabelBean);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuidelineBean);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
    	 	}
	
	
	
	
	public void uploadVcfOutput(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	System.out.println("uploadvcf");

    	String uploadedBy = request.getParameter("uploaded_by");
        if (uploadedBy == null || uploadedBy.isEmpty()) {
            request.setAttribute("validateError", "Uploaded by can not be blank");
            request.getRequestDispatcher("/matching_index_error.jsp").forward(request, response);
            return;
        }
        Part requestPart = request.getPart("vcf");
        if (requestPart == null) {
            request.setAttribute("validateError", "vcf output file can not be blank");
            request.getRequestDispatcher("/matching_index_error.jsp").forward(request, response);
            return;
        }
        InputStream inputStream = requestPart.getInputStream();
        byte[] bytes = inputStream.readAllBytes();
        String content = new String(bytes);
        int sampleId = SampleDAO.save(uploadedBy);
        vepDAO.save(sampleId, content);
        response.sendRedirect("matching?sampleId=" + sampleId);
    }

}
