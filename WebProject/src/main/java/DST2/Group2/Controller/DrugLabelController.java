package DST2.Group2.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import dao.DrugLabelDAO;
import dao.VarDrugAnnDAO;
import dao.DosingGuidelineDAO;
import dao.SampleDAO;
import bean.DosingGuidelineBean;
import bean.DrugLabelBean;
import bean.SampleBean;
import bean.VarDrugAnnBean;
import dao.VepDAO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vep/labelAndguideline")
public class DrugLabelController {

    List<DrugLabelBean> matchedDrugLabelBean =null;
    List<DosingGuidelineBean> matchedGuidelines =null;
    List<VarDrugAnnBean> matchedAnns=null;

    private VepDAO vepDAO = new VepDAO();
    private DosingGuidelineDAO dosingGuidelineDAO = new DosingGuidelineDAO();
    private DrugLabelDAO drugLabelDAO = new DrugLabelDAO();
    private VarDrugAnnDAO varDrugAnnDAO = new VarDrugAnnDAO();
    private SampleDAO sampleDAO = new SampleDAO();

    @RequestMapping("/matchingIndex")
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


    @RequestMapping("/matching")
	public String matching(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        //set in jsp
		String sampleIdParameter = request.getParameter("sampleId");
        if (sampleIdParameter == null) {
            request.getRequestDispatcher("/samples.jsp").forward(request, response);
            return "Hello";
        }
        int sampleId;
        try {
            sampleId = Integer.parseInt(sampleIdParameter);
        } catch (NumberFormatException e) {
            response.sendRedirect("samples");
            return "Hello";
        }

        ArrayList<ArrayList<String>> refGenes = vepDAO.getsampleGenes(sampleId);

        if (refGenes.isEmpty()) {
            response.sendRedirect("samples");
            return "Hello";
        }

        ArrayList<Object> matched_drugLabel_by_gene = doMatchDrugLabel(refGenes);
        ArrayList<Object> matched_dosingGuideline_by_gene = doMatchDosingGuideline(refGenes);
        ArrayList<Object> matched_ann_by_gene = doMatchVarDrugAnn(refGenes);

        //pass to jsp
        request.setAttribute("matchedDrugLabel", matched_drugLabel_by_gene);
        request.setAttribute("matchedDosingGuideline", matched_dosingGuideline_by_gene);
        request.setAttribute("matchedVarDrugAnn",matched_ann_by_gene);
        request.setAttribute("sample", SampleDAO.findById(sampleId));
        return "Hello";
    }
	
	private ArrayList<Object> doMatchDrugLabel(ArrayList<ArrayList<String>> refGenes) {
        List<DrugLabelBean> drugLabelBeans = drugLabelDAO.getDrugLabel();
        ArrayList<Object> rt = new ArrayList<>();
        List<DrugLabelBean> matchedLabels = new ArrayList<>();
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>();

        for (DrugLabelBean drugLabelBean : drugLabelBeans) {
            for (ArrayList<String> row: refGenes) {
                String gene = row.get(3); // 4st field: gene symbol
                if (drugLabelBean.getSummary_markdown().contains(gene)) {
                    drugLabelBean.setvariantGene(gene);
                    if (!matchedLabels.contains(drugLabelBean)){ matchedLabels.add(drugLabelBean); }
                    updateSampleReturn(matched_sampleInfo, row, gene);
                }
            }
        }

        rt.add(matchedLabels);
        rt.add(matched_sampleInfo);
        return rt;
    }

	private ArrayList<Object> doMatchDosingGuideline(ArrayList<ArrayList<String>> refGenes) {
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.getDosingGuideline();
        ArrayList<Object> rt = new ArrayList<>();
        List<DosingGuidelineBean> matchedGuidelines = new ArrayList<>();
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>();

        for (DosingGuidelineBean guideline : dosingGuidelineBeans) {
            for (ArrayList<String> row: refGenes) {
                String gene = row.get(3); // 4st field: gene symbol
                if (guideline.getSummary_markdown().contains(gene)) {
                    if (!matchedGuidelines.contains(guideline)){ matchedGuidelines.add(guideline); }
                    updateSampleReturn(matched_sampleInfo, row, gene);
                }
            }
        }

        rt.add(matchedGuidelines);
        rt.add(matched_sampleInfo);
        return rt;
    }
	
	private ArrayList<Object> doMatchVarDrugAnn(ArrayList<ArrayList<String>> refGenes) {
        List<VarDrugAnnBean> VarDrugAnns = varDrugAnnDAO.getAnn();
        ArrayList<Object> rt = new ArrayList<>();
        List<VarDrugAnnBean> matchedAnns=new ArrayList<>();
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>();

		for (VarDrugAnnBean ann:VarDrugAnns) {
			String Gene=ann.getGene();
            for (ArrayList<String> row: refGenes) {
                String gene = row.get(3);
                if (Gene.contains(gene)) {
                    if (!matchedAnns.contains(ann)){ matchedAnns.add(ann); }
                    updateSampleReturn(matched_sampleInfo, row, gene);
                }
            }
		}

		rt.add(matchedAnns);
        rt.add(matched_sampleInfo);
		return rt;
	}

	@RequestMapping("/searchDrug")
	public String searchDrug(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
    	String drug=request.getParameter("drug");
    	List<DrugLabelBean> filteredDrugLabelBean;
    	List<DosingGuidelineBean> filteredDosingGuidelineBean;
    	List<VarDrugAnnBean> filteredVarDrugAnn;
    	filteredDrugLabelBean =drugLabelDAO.searchByDrug(drug, matchedDrugLabelBean);
    	filteredDosingGuidelineBean = dosingGuidelineDAO.searchByDrug(drug, matchedGuidelines);
    	filteredVarDrugAnn=varDrugAnnDAO.searchByDrug(drug,matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel", filteredDrugLabelBean);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuidelineBean);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
    	return "Hello";
    }

    @RequestMapping("/searchPhen")
	public String searchPhen(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    	//be consistent with jsp
    	String phen=request.getParameter("phenotype");
    	List<DrugLabelBean> filteredDrugLabelBean;
    	List<DosingGuidelineBean> filteredDosingGuidelineBean;
    	List<VarDrugAnnBean> filteredVarDrugAnn;
    	filteredDrugLabelBean =drugLabelDAO.searchByPhenotype(phen, matchedDrugLabelBean);
    	filteredDosingGuidelineBean = dosingGuidelineDAO.searchByPhenotype(phen, matchedGuidelines);
    	filteredVarDrugAnn=varDrugAnnDAO.searchByPhen(phen, matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel", filteredDrugLabelBean);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuidelineBean);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
        return "Hello";
    }

    public void updateSampleReturn(HashMap<String, HashMap<String, String>> matched_sampleInfo, ArrayList<String> row, String gene) {
        // refactored by IDEA automatically
        if (matched_sampleInfo.containsKey(gene)){
            matched_sampleInfo.get(gene).put(row.get(0), row.get(1));
        } else {
            HashMap<String, String> submap = new HashMap<>();
            submap.put(row.get(0), row.get(1));
            matched_sampleInfo.put(gene,submap);
        }
    }
	
    @RequestMapping("/upload")
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
        int sampleId = sampleDAO.save(uploadedBy);
        vepDAO.save(sampleId, content);
        response.sendRedirect("matching?sampleId=" + sampleId);
    }

}
