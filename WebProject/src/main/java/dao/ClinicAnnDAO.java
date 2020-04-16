package dao;

import DBmtd.DBmethods;
import bean.ClinicAnnBean;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClinicAnnDAO {

    public List<ClinicAnnBean> findAll(){
        List<ClinicAnnBean> clinicAnnBeans = new ArrayList<>();
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
                    String related_diseases = resultSet.getString("related_diseases");
                    String biogeographical_groups = resultSet.getString("biogeographical_groups");
                    String chromosome = resultSet.getString("chromosome");
                    ClinicAnnBean clinicAnnBean = new ClinicAnnBean(id,location,gene,evidencelevel,types,annotation_text,related_chemicals,related_diseases,biogeographical_groups,chromosome);
                    clinicAnnBeans.add(clinicAnnBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return clinicAnnBeans;
    }

}
