package DST2.Group2.Controller;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.*;
import DST2.Group2.bean.ClinicAnnBean;
import DST2.Group2.DAO.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

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
public class VepMatchController {

    private static final Logger log = LoggerFactory.getLogger(DST2.Group2.Controller.VepMatchController.class);

    private HashMap<String, String[]> target = new HashMap<>();
    private VepDAO vepDAO = new VepDAO();
    private ClinicAnnDAO clinicAnnDAO = new ClinicAnnDAO();
    private DosingGuidelineDAO dosingGuidelineDAO = new DosingGuidelineDAO();
    private DrugLabelDAO drugLabelDAO = new DrugLabelDAO();
    private VarDrugAnnDAO varDrugAnnDAO = new VarDrugAnnDAO();
    private SampleDAO sampleDAO = new SampleDAO();

    List<DrugLabel> matchedDrugLabel =null;
    List<DosingGuideline> matchedGuidelines =null;
    List<VarDrugAnn> matchedAnns=null;


    @RequestMapping(value = "/matching/{sampleType}/{sampleId}",method = RequestMethod.GET)
    public ModelAndView doMatch(@PathVariable("sampleType") String sampleType, @PathVariable("sampleId") String sampleIdParameter, HttpServletRequest request) {
        /**
         * To code:
         * 1. handle sample Id error: direct to page to view all samples (add samples.jsp)?
         * (change "hello" below)
         */

        if (sampleIdParameter == null | sampleType == null) {
            // if sample Id or sample type is not specified, go to sample page (view all samples)
            HashMap<String,Object> data = new HashMap<>();
            data.put("error_message", "sample not specified yet.");
            return new ModelAndView("hello",data);
        }
        int sampleId;
        try {
            // if sample Id format is wrong, go to sample page (view all samples)
            sampleId = Integer.parseInt(sampleIdParameter);
        } catch (NumberFormatException e) {
            HashMap<String,Object> data = new HashMap<>();
            data.put("error_message", "sample Id format is wrong.");
            return new ModelAndView("hello",data);
        }

        ArrayList<ArrayList<String>> sampleVep = vepDAO.getsampleGenes(sampleId);

        String search_drug="ivacaftor"; // test
        String search_phen="";
        target.put("drug", search_drug.split(","));
        target.put("disease", search_phen.split(","));

        if (sampleVep.isEmpty()) {
            // if sample is not in the database, go to sample page (view all samples)
            HashMap<String,Object> data = new HashMap<>();
            data.put("error_message", "sample not uploaded yet.");
            return new ModelAndView("hello",data);
        }

        List<Object> matched_clinic_ann_by_gene = doMatchClinic_by_Gene(sampleVep);
        List<Object> matched_clinic_ann_by_snp = doMatchClinic_by_SNP(sampleVep);


        HashMap<String,Object> data = new HashMap<>();
        data.put("matched_clinic_ann_by_gene", matched_clinic_ann_by_gene);
        data.put("matched_clinic_ann_by_snp",matched_clinic_ann_by_snp);

        data.put("sample", sampleDAO.findById(sampleId));
        return new ModelAndView("hello", data);
    }


    private ArrayList<Object> doMatchClinic_by_SNP(ArrayList<ArrayList<String>> sampleGenes) {
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
                    List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll(target);

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

    private ArrayList<Object> doMatchClinic_by_Gene(ArrayList<ArrayList<String>> sampleGenes) {
        /**
         * Part 2b: match sample mutated genes with clinic annotation, only variants with clinic annotations are considered.
         */
        ArrayList<Object> rt = new ArrayList<>();
        List<ClinicAnnBean> matchedClinicAnnBeans = new ArrayList<>(); // e.g. [clinicAnnBean1, clinicAnnBean2,...]
        HashMap< String, HashMap<String, String> > matched_sampleInfo = new HashMap<>(); // e.g. { GENE1 : {s12345:T, s6789:G}, GENE2 : {s123:C},...}

        List<ClinicAnnBean> refClinicAnns = clinicAnnDAO.findAll(target);
        log.info("Filtered clinic annotation record total: " + refClinicAnns.size());

//        int counter=0;
        for (Object obj : sampleGenes){
            for (ClinicAnnBean clinicAnnBean:refClinicAnns){
//                counter++;
//                if (counter%100000000==0){
//                    System.out.println("processed: " + counter);
//                }
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
