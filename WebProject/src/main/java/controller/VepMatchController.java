package controller;

import DBmtd.DBmethods;
import bean.*;
import dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/vep/clinic_ann")
public class VepMatchController {

    private static final Logger log = LoggerFactory.getLogger(VepMatchController.class);

    private VepDAO vepDAO = new VepDAO();
    private ClinicAnnDAO clinicAnnDAO = new ClinicAnnDAO();
    private DosingGuidelineDAO dosingGuidelineDAO = new DosingGuidelineDAO();
    private DrugLabelDAO drugLabelDAO = new DrugLabelDAO();
    private VarDrugAnnDAO varDrugAnnDAO = new VarDrugAnnDAO();
    private SampleDAO sampleDAO = new SampleDAO();

    List<DrugLabelBean> matchedDrugLabelBean =null;
    List<DosingGuidelineBean> matchedGuidelines =null;
    List<VarDrugAnnBean> matchedAnns=null;

    @RequestMapping("/upload_vep")
    public void upload_vep(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        /**
         * To edit: upload user file and sample number
         */
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

    @RequestMapping("/samples")
    public void samples(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("samples");
        List<SampleBean> samples = SampleDAO.findAll();
        //pass to jsp
        request.setAttribute("samples", samples);
        request.getRequestDispatcher("/samples.jsp").forward(request, response);
    }

    @RequestMapping("/matchingIndex")
    public String matchingIndex(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("matchingindex");
        return "Hello";
    }

    @RequestMapping("/matching")
    public String doMatch(HttpServletRequest request, HttpServletResponse response) {
        /**
         * To code:
         * 1. handle sample Id error: direct to page to view all samples (add samples.jsp)?
         * (change "hello" below)
         */
        String sampleIdParameter = request.getParameter("sampleId");
        String sampleType = request.getParameter("sampleType");

        if (sampleIdParameter == null | sampleType == null) {
            // if sample Id or sample type is not specified, go to sample page (view all samples)
            return "hello";
        }
        int sampleId;
        try {
            // if sample Id format is wrong, go to sample page (view all samples)
            sampleId = Integer.parseInt(sampleIdParameter);
        } catch (NumberFormatException e) {
            return "hello";
        }

        ArrayList<ArrayList<String>> sampleVep = vepDAO.getsampleGenes(sampleId);

        if (sampleVep.isEmpty()) {
            // if sample is not in the database, go to sample page (view all samples)
            return "hello";
        }

        List<Object> matched_clinic_ann_by_gene = doMatchClinic_by_Gene(sampleVep);
        List<Object> matched_clinic_ann_by_snp = doMatchClinic_by_SNP(sampleVep);
        ArrayList<Object> matched_drugLabel_by_gene = doMatchDrugLabel(sampleVep);
        ArrayList<Object> matched_dosingGuideline_by_gene = doMatchDosingGuideline(sampleVep);
        ArrayList<Object> matched_ann_by_gene = doMatchVarDrugAnn(sampleVep);

        request.setAttribute("matched_clinic_ann_by_gene", matched_clinic_ann_by_gene);
        request.setAttribute("matched_clinic_ann_by_snp",matched_clinic_ann_by_snp);
        request.setAttribute("matchedDrugLabel", matched_drugLabel_by_gene);
        request.setAttribute("matchedDosingGuideline", matched_dosingGuideline_by_gene);
        request.setAttribute("matchedVarDrugAnn",matched_ann_by_gene);
        request.setAttribute("sample", SampleDAO.findById(sampleId));
        return "hello";
    }

    @RequestMapping("/searchDrug")
    public String searchDrug(HttpServletRequest request, HttpServletResponse response) {
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
    public String searchPhen(HttpServletRequest request, HttpServletResponse response) {
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

    private ArrayList<Object> doMatchClinic_by_SNP(ArrayList<ArrayList<String>> sampleGenes){
        /**
         * To map sample variant according to its exact genomic location.
         * Usually, this mapping is too strict to yield any positive result.
         * Therefore, mapping by location is not recommended to be used alone.
         */
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        DBmethods.execSQL(connection -> {
            String statement2 = "SELECT variant_name FROM variant WHERE location=? AND clinical_annotation_count!=0;";
            ArrayList<ArrayList<String>> filtered = new ArrayList<>();
            try {
                /**
                 * Part 2a: match sample variant location with database, only variants with clinic annotations are considered.
                 * Query variant name ("rs" ID)
                 */
                ResultSet resultSet;

                for (Object o : sampleGenes) {

                    PreparedStatement preparedStatement2 = connection.prepareStatement(statement2);
                    ArrayList<String> oldrow = (ArrayList<String>) o;
                    preparedStatement2.setString(1, oldrow.get(0));
                    resultSet = preparedStatement2.executeQuery();
                    while (resultSet.next()) {
                        ArrayList<String> newrow = new ArrayList<>();
                        newrow.add(oldrow.get(0)); // location
                        newrow.add(oldrow.get(1)); // allele
                        newrow.add(oldrow.get(2)); // gene_ori
                        newrow.add(oldrow.get(3)); // gene_sym
                        newrow.add(resultSet.getString(1)); // SNP name
                        filtered.add(newrow);
                    }
                }

                log.info("Processed SNP mapping: " + sampleGenes.size() + ". Found match: " + filtered.size());

                if (filtered.size()>0){ // row number, nothing found in sample VEP???
                    List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();

                    for (Object obj : filtered){
                        for (ClinicAnnBean clinicAnnBean:refClinicAnns){
                            ArrayList<String> row = (ArrayList<String>) obj;
                            String location = row.get(4); // SNP name (location in ClinicAnnBean)
                            if (clinicAnnBean.getLocation()==null){ continue; }
                            if (clinicAnnBean.getLocation().contains(location)){

                                if (!matchedClinicAnnBeans.contains(clinicAnnBean)){
                                    matchedClinicAnnBeans.add(clinicAnnBean);
                                }

                                String gene = row.get(3);
                                updateSampleReturn(matched_sampleInfo, row, gene);
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
        return rt;
    }

    private ArrayList<Object> doMatchClinic_by_Gene(ArrayList<ArrayList<String>> sampleGenes){
        /**
         * Part 2b: match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
         */
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll();

        for (Object obj : sampleGenes){
            for (ClinicAnnBean clinicAnnBean:refClinicAnns){
                ArrayList<String> row = (ArrayList<String>) obj;
                String gene = row.get(3); // gene symbol
                if (clinicAnnBean.getGene()==null){ continue; }
                if (clinicAnnBean.getGene().contains(gene)){

                    if (!matchedClinicAnnBeans.contains(clinicAnnBean)){
                        matchedClinicAnnBeans.add(clinicAnnBean);
                    }

                    updateSampleReturn(matched_sampleInfo, row, gene);
                }
            }
        }

        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to " + matched_sampleInfo.size() + " genes from the sample.");

        rt.add(matchedClinicAnnBeans); // 787 records matched from sample
        rt.add(matched_sampleInfo); // 185 genes matched from clinic annotation

        return rt;
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

    private void updateSampleReturn(HashMap<String, HashMap<String, String>> matched_sampleInfo, ArrayList<String> row, String gene) {
        // refactored by IDEA automatically
        if (matched_sampleInfo.containsKey(gene)){
            matched_sampleInfo.get(gene).put(row.get(0), row.get(1));
        } else {
            HashMap<String, String> submap = new HashMap<>();
            submap.put(row.get(0), row.get(1));
            matched_sampleInfo.put(gene,submap);
        }
    }

}
