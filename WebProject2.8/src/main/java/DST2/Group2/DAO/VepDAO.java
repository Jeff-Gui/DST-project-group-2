package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.GeneBean;
import DST2.Group2.bean.RefBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Repository
public class VepDAO {

    private static final Logger log = LoggerFactory.getLogger(VepDAO.class);

    public void save(int sampleId, String content) {
        String[] line = content.split("#");
        String[] lines = line[line.length - 1].split("\\n");
        log.info("Records count: " + (lines.length - 1)); // first line is column header

        DBmethods.execSQL(connection -> {
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO vep(sample_id, Uploaded_variation, Location, Allele, Gene, Feature, Feature_type, Consequence, cDNA_position, CDS_position, Protein_position, Amino_acids, Codons, Existing_variation, Extra) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                for (int i = 1; i < lines.length; i++) {
                    //                skip the first line which is column names
                    preparedStatement.setInt(1, sampleId);
                    String[] fields = lines[i].split("\\t");
                    if (fields.length < 14) {
                        log.info("Poor record at line: " + i);
                        log.info("Record: " + lines[i]);
                        throw new SQLException();
                    }
                    for (int j = 2; j <= 15; j++) {
                        preparedStatement.setString(j, fields[j - 2]);
                    }
                    preparedStatement.addBatch();
                    if (i % 1000 == 0) {
                        // execute batch for every 1000 records
                        preparedStatement.executeBatch();
                        connection.commit();
                    }
                }
                if (lines.length<1001){
                    preparedStatement.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    public ArrayList<RefBean> getsampleGenes(int sample_id) {
        GeneDAO geneDAO = new GeneDAO();
        ArrayList<RefBean> results = new ArrayList<>();

        DBmethods.execSQL(connection -> {
            String statement = "SELECT DISTINCT location,allele,gene FROM vep WHERE sample_id=?";
            try {
                /**
                 * Read and transfer variant location from the sample, meanwhile read allele.
                 */
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setInt(1, sample_id);
                ResultSet resultSet = preparedStatement.executeQuery();

                // Load dictionary for location transfer: map Ch... to NC_..
                HashMap<String, String> transDic = new HashMap<>();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new DataInputStream(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("NC_ChMapping.tsv")))));
                String line;
                while ((line = br.readLine()) != null) {
                    transDic.put(line.split("\t")[1], line.split("\t")[0]); // each line is NC...\tCh...
                }

                List<GeneBean> refGenes = geneDAO.findAll();

                while (resultSet.next()) {

                    String lc = resultSet.getString(1);
                    // re-format location from Ch... to NC...
                    String lc_trans = lc.substring(lc.indexOf(":")); // constant string after colon
                    String location = transDic.get(lc.substring(0, lc.indexOf(":"))) + lc_trans; // 1st field: location
                    String allele = resultSet.getString(2); // 2nd field: allele
                    String ori_gene = resultSet.getString(3); // 3rd field: Ensembl ID

                    // transfer Ensembl ID to gene symbol
                    String sym_gene = "-";
                    if (!ori_gene.equals("-")) {
                        boolean ini = false;
                        for (GeneBean geneBean : refGenes) {
                            if (geneBean.getEnsembl_id()!=null && geneBean.getEnsembl_id().length()!=0){
                            if (geneBean.getEnsembl_id().equals(ori_gene)) {
                                if (geneBean.getSymbol()!=null && geneBean.getSymbol().length()!=0) {
                                sym_gene = geneBean.getSymbol();
                                ini = true;
                                break;}
                            }}
                        }
                        if (!ini) {
                            sym_gene = "-";
                        }
                    } else {
                        sym_gene = "-";
                    }

                    results.add( new RefBean(location, allele, ori_gene, sym_gene)); // each row: location, allele, gene_ori, gene_sym); // 4th field: gene symbol

                }

                log.info("Total distinct record from the vep-annotated sample: " + results.size());

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });
        return results;
    }

}