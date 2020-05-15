package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.ClinicAnnBean;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class ClinicAnnDAO {

    public List<ClinicAnnBean> search(String drugName, String phenotype, List<ClinicAnnBean> ClinicAnns) {
        Iterator<ClinicAnnBean> iterator=ClinicAnns.iterator();
        String[] drugList=drugName.split(",");
        String[] phenList=phenotype.split(",");
        while(iterator.hasNext()) {
            boolean hasDrug=false;
            boolean hasPhen=false;
            ClinicAnnBean clinicann=iterator.next();
            if (clinicann.getAnnotation_text()!=null && clinicann.getRelated_diseases()!=null) {
            hasDrug=ListMatch.listMatch(clinicann.getRelated_chemicals(),drugList);
            hasPhen=ListMatch.listMatch2(clinicann.getAnnotation_text(),clinicann.getRelated_diseases(),phenList);}
            if (!hasDrug || !hasPhen) {
                iterator.remove();
            }
        }
        return ClinicAnns;
    }

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
        return clinicAnnBeans;
    }

    public int doImport(boolean doDelete) {
        /**
         * Download and import the latest clinic annotation tables from PGKB.
         * @Done: test
         * @ToDo: IO Error handling
         */
        AtomicInteger counter = new AtomicInteger();
        DBmethods.execSQL(connection -> {
            try{
                // 1. Check file exist
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new DataInputStream(getClass().getResourceAsStream("/tables/clinical_ann_metadata.tsv"))));
                String header = br.readLine(); // first line is col name
                if (header.equals("")){ throw new IOException(); } // if file empty, throw an error
                // 2. Delete old data
                if (doDelete){  connection.createStatement().execute("DELETE FROM clinic_meta;"); }
                // 3. Insert new data
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO clinic_meta(id, location, gene, evidencelevel, clinical_annotation_types, genotype_phenotype_ids, annotation_text, variant_annotation_ids, variant_annotations, pmids, evidence_count, related_chemicals, related_diseases, biogeographical_groups, chromosome) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                String line;
                while ((line = br.readLine()) != null) {
                    String[] field = line.split("\\t");
                    preparedStatement.setInt(1,Integer.parseInt(field[0]));
                    if (field.length>15){ // if contain extra info
                        field = Arrays.copyOf(field,10);
                    }
                    for (int j=2; j<=field.length;j++){
                        preparedStatement.setString(j,field[j-1]); // 15 fields in total will be inserted
                    }
                    if (field.length<15){ // if record is less than essential length
                        for (int j=field.length; j<=15;j++){
                            preparedStatement.setString(j, null);
                        }
                    }
                    preparedStatement.addBatch();
                    if (counter.get() % 1000 == 0) {
                        // execute batch for every 1000 records
                        preparedStatement.executeBatch();
                        connection.commit();
                    }
                    counter.getAndIncrement();
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        return counter.get();
    }
}
