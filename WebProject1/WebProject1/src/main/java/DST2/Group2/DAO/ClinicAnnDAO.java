package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.ClinicAnnBean;
import DST2.Group2.bean.DrugLabel;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ClinicAnnDAO {
    public static List<ClinicAnnBean> getClinicAnn() {
        List<ClinicAnnBean> allClinicAnn=new ArrayList<>();
        DBmethods.execSQL(connection -> {
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, location, gene, evidencelevel, clinical_annotation_types, annotation_text, related_chemicals, related_diseases, biogeographical_groups, chromosome FROM clinic_meta;");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String location = resultSet.getString("location");
                    String gene = resultSet.getString("gene");
                    String evidencelevel = resultSet.getString("evidencelevel");
                    String types = resultSet.getString("clinical_annotation_types");
                    String annotation_text = resultSet.getString("annotation_text");
                    String related_chemicals = resultSet.getString("related_chemicals");
                    if (related_chemicals!=null){ related_chemicals = related_chemicals.toLowerCase(); }
                    String related_diseases = resultSet.getString("related_diseases");
                    if (related_diseases!=null){ related_diseases = related_diseases.toLowerCase(); }
                    String biogeographical_groups = resultSet.getString("biogeographical_groups");
                    String chromosome = resultSet.getString("chromosome");
                    ClinicAnnBean clinicAnnBean = new ClinicAnnBean(id,location,gene,evidencelevel,types,annotation_text,related_chemicals,related_diseases,biogeographical_groups,chromosome);
                    allClinicAnn.add(clinicAnnBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
        return allClinicAnn;
    }
    public static List<ClinicAnnBean> search(String drugName, String phenotype, List<ClinicAnnBean> ClinicAnns) {
        Iterator<ClinicAnnBean> iterator=ClinicAnns.iterator();
        String[] drugList=drugName.split(",");
        String[] phenList=phenotype.split(",");
        while(iterator.hasNext()) {
            ClinicAnnBean clinicann=iterator.next();
            Boolean hasDrug=false;
            Boolean hasPhen=false;
            if (drugList[0].equals("")) {
                hasDrug=true;

            } else {
            for (String d:drugList) {
                if (clinicann.getRelated_chemicals().contains(d)) {
                    hasDrug=true;
                }
            }}

            if (phenList[0].equals("")) {
                hasPhen=true;
            } else {
                for (String p:phenList) {
                    if (clinicann.getRelated_diseases().contains(p) || clinicann.getAnnotation_text().contains(p)) {
                        hasPhen=true;
                    }
                }
            }

            if (hasDrug==false || hasPhen==false) {
                iterator.remove();
            }
        }
        return ClinicAnns;
    }

    public List<ClinicAnnBean> findAll(HashMap<String, String[]> target){

        List<ClinicAnnBean> clinicAnnBeans = new ArrayList<>();

        //String[] filter_drug = target.get("drug");

        //String[] filter_disease = target.get("disease");



        //if (filter_disease[0].equals("") & filter_drug[0].equals("")){

            DBmethods.execSQL(connection -> {

                try{

                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, location, gene, evidencelevel, clinical_annotation_types, annotation_text, related_chemicals, related_diseases, biogeographical_groups, chromosome FROM clinic_meta;");

                    ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {

                        String id = resultSet.getString("id");

                        String location = resultSet.getString("location");

                        String gene = resultSet.getString("gene");

                        String evidencelevel = resultSet.getString("evidencelevel");

                        String types = resultSet.getString("clinical_annotation_types");

                        String annotation_text = resultSet.getString("annotation_text");

                        String related_chemicals = resultSet.getString("related_chemicals");

                        if (related_chemicals!=null){ related_chemicals = related_chemicals.toLowerCase(); }

                        String related_diseases = resultSet.getString("related_diseases");

                        if (related_diseases!=null){ related_diseases = related_diseases.toLowerCase(); }

                        String biogeographical_groups = resultSet.getString("biogeographical_groups");

                        String chromosome = resultSet.getString("chromosome");

                        ClinicAnnBean clinicAnnBean = new ClinicAnnBean(id,location,gene,evidencelevel,types,annotation_text,related_chemicals,related_diseases,biogeographical_groups,chromosome);

                        clinicAnnBeans.add(clinicAnnBean);

                    }

                } catch (SQLException e) {

                    e.printStackTrace();

                }

            });

       /// }
        //else {

//            DBmethods.execSQL(connection -> {
//
//                try {
//
//                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, location, gene, evidencelevel, clinical_annotation_types, annotation_text, related_chemicals, related_diseases, biogeographical_groups, chromosome FROM clinic_meta;");
//
//                    ResultSet resultSet = preparedStatement.executeQuery();
//
//                    while (resultSet.next()) {
//
//                        String related_chemicals = resultSet.getString("related_chemicals");
//
//                        if (related_chemicals!=null){ related_chemicals = related_chemicals.toLowerCase();} else { continue; }
//
//                        String related_diseases = resultSet.getString("related_diseases");
//
//                        if (related_diseases!=null){ related_diseases = related_diseases.toLowerCase(); } else { continue; }
//
//                        if (ListMatch.listMatch(related_diseases, filter_disease) &
//
//                                ListMatch.listMatch(related_chemicals, filter_drug)){
//
//                            String id = resultSet.getString("id");
//
//                            String location = resultSet.getString("location");
//
//                            String gene = resultSet.getString("gene");
//
//                            String evidencelevel = resultSet.getString("evidencelevel");
//
//                            String types = resultSet.getString("clinical_annotation_types");
//
//                            String annotation_text = resultSet.getString("annotation_text");
//
//                            String biogeographical_groups = resultSet.getString("biogeographical_groups");
//
//                            String chromosome = resultSet.getString("chromosome");
//
//                            ClinicAnnBean clinicAnnBean = new ClinicAnnBean(id, location, gene, evidencelevel, types, annotation_text, related_chemicals, related_diseases, biogeographical_groups, chromosome);
//
//                            clinicAnnBeans.add(clinicAnnBean);
//
//                        }
//
//                    }
//
//                } catch (SQLException e) {
//
//                    e.printStackTrace();
//
//                }

//            });

//        }

        return clinicAnnBeans;

    }
}
