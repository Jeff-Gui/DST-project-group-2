package controller;

import DBmtd.DBmethods;
import bean.ClinicAnnBean;
import dao.ClinicAnnDAO;
import dao.VepDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @RequestMapping("/upload_vep")
    public void upload_vep(){
        /**
         * To edit: upload user file and sample number
         */
        File file = new File("/Users/jefft/Software/annovar/SK-HEP-1_vep.vcf");
//      332009 records
        Long fileLengthLong = file.length();
        byte[] fileContent = new byte[fileLengthLong.intValue()];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            inputStream.read(fileContent);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String content = new String(fileContent);
        vepDAO.save(0, content);
    }

    @RequestMapping("matchingIndex")
    public String matchingIndex(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        System.out.println("matchingindex");
        return "Hello";
    }

    @RequestMapping("/matching_clinic_ann")
    public String matching_clinic_ann(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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
        Integer sampleId;
        try {
            // if sample Id format is wrong, go to sample page (view all samples)
            sampleId = Integer.valueOf(sampleIdParameter);
        } catch (NumberFormatException e) {
            return "hello";
        }
        ArrayList<ArrayList<String>> sampleVep = new ArrayList<>();
        sampleVep = vepDAO.getsampleGenes(sampleId);
        if (sampleVep.isEmpty()) {
            // if sample is not in the database, go to sample page (view all samples)
            return "hello";
        }

        List<Object> matched_clinic_ann_by_gene = doMatch_Gene(sampleVep);
        List<Object> matched_clinic_ann_by_snp = doMatch_SNP(sampleVep);
        request.setAttribute("matched_clinic_ann_by_gene", matched_clinic_ann_by_gene);
        request.setAttribute("matched_clinic_ann_by_snp",matched_clinic_ann_by_snp);
        return "hello";
    }


    private ArrayList<Object> doMatch_SNP(ArrayList<ArrayList<String>> sampleGenes){
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
                                checkDup(matched_sampleInfo, row, gene);
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

    private ArrayList<Object> doMatch_Gene(ArrayList<ArrayList<String>> sampleGenes){
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

                    checkDup(matched_sampleInfo, row, gene);
                }
            }
        }

        log.info("Matched clinic annotation: " + matchedClinicAnnBeans.size() + " corresponding to " + matched_sampleInfo.size() + " genes from the sample.");

        rt.add(matchedClinicAnnBeans); // 787 records matched from sample
        rt.add(matched_sampleInfo); // 185 genes matched from clinic annotation

        return rt;
    }

    private void checkDup(HashMap<String, HashMap<String, String>> matched_sampleInfo, ArrayList<String> row, String gene) {
        // refactored by IDEA automatically
        if (matched_sampleInfo.containsKey(gene)){
            matched_sampleInfo.get(gene).put(row.get(0), row.get(1));
        } else {
            HashMap<String, String> submap = new HashMap<>();
            submap.put(row.get(0), row.get(1));
            matched_sampleInfo.put(gene,submap);
        }
    }

//    public static void main(String[] args){
//        MatchController mc = new MatchController();
//
//        mc.doMatch_Gene(0);
//        mc.doMatch_SNP(0);
//
////        CSVwriter.Array2CSV(mc.getVariant(0, false), "/Users/jefft/Desktop/o.csv");
//    }

}
