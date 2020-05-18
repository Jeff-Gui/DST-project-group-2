package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.RefBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class AnnovarDAO extends BaseDAO {

    private Logger log = LoggerFactory.getLogger(AnnovarDAO.class.getSimpleName());

    public void save(int sampleId, String content) {
        String[] lines = content.substring(content.indexOf("\n")+1).split("\\r|\\n");
        log.info("Records count: " + lines.length);
        DBmethods.execSQL(connection -> {
            String sql = "INSERT INTO annovar (sample_id, Chr, \"Start\", \"End\", \"Ref\", Alt, \"Func.refGene\", \"Gene.refGene\", \"GeneDetail.refGene\", \"ExonicFunc.refGene\", \"AAChange.refGene\", cytoBand) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                for (int i = 0; i < lines.length; i++) {
                    preparedStatement.setInt(1, sampleId);
                    String[] split = lines[i].split("\\t");
                    for (int j = 1; j <= split.length; j++) {
                        preparedStatement.setString(j + 1, split[j - 1]);
                    }
                    preparedStatement.addBatch();
                    if (i % 1000 == 0) {
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

    public List<RefBean> getsampleGenes(int sampleId) {
        String sql = "select distinct Chr, \"Start\", \"End\", Alt, \"Gene.refGene\" from annovar where \"ExonicFunc.refGene\" != 'synonymous SNV' and sample_id = ?";
        List<RefBean> refBeans = new ArrayList<>();

        DBmethods.execSQL(connection -> {
            PreparedStatement preparedStatement;
            try {
                // Load dictionary for location transfer: map Ch... to NC_..
                HashMap<String, String> transDic = new HashMap<>();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new DataInputStream(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResourceAsStream("NC_ChMapping.tsv")))));
                String line;
                while ((line = br.readLine()) != null) {
                    transDic.put(line.split("\t")[1].toLowerCase(), line.split("\t")[0]); // each line is NC...\tCh...
                }

                String location="-";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, sampleId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String lc = resultSet.getString(1); // Chromosome
                    String position_srt = resultSet.getString(2); // start position on the chromosome
                    String position_end = resultSet.getString(3); // end position on the chromosome
                    // re-format location from Ch... to NC...
                    if (position_srt.equals(position_end)) {
                        location = transDic.get(lc) + position_srt; // 1st field: location
                    } else {
                        location = "Range:" + transDic.get(lc) + position_srt + "" + transDic.get(lc) + position_end;
                    }
                    String allele = resultSet.getString(4); // 2nd field: allele
                    String ori_gene = resultSet.getString(5); // 3rd field: original gene.
                    refBeans.add(new RefBean(location, allele, ori_gene, ori_gene)); // In annovar format, original gene format is symbol.
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        });

        log.info("Total distinct record from the annovar-annotated sample: " + refBeans.size());

        return refBeans;
    }
}
