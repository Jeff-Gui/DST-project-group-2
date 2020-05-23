package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.GeneBean;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class GeneDAO {

    public List<GeneBean> findAll(){
        List<GeneBean>  geneBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT symbol, ensembl_id FROM gene;");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String symbol = resultSet.getString("symbol");
                    String ensembl_id = resultSet.getString("ensembl_id");
                    GeneBean geneBean = new GeneBean(symbol, ensembl_id);
                    geneBeans.add(geneBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return geneBeans;
    }

    public int doImport(boolean doDelete) {
        /**
         * Import the latest gene treated list tables provided by the admin.
         * @Done: test
         * @ToDo: IO Error handling
         */
        AtomicInteger counter = new AtomicInteger();
        DBmethods.execSQL(connection -> {
            try{
                // 1. Check file exist
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new DataInputStream(getClass().getResourceAsStream("/tables/genes_treated.tsv"))));
                String header = br.readLine(); // first line is col name
                if (header.equals("")){ throw new IOException(); } // if file empty, throw an error
                // 2. Delete old data
                if (doDelete){ connection.createStatement().execute("DELETE FROM gene;"); }
                // 3. Insert new data
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO gene(pgkb_id,ncbi_id,hgnc_id,es_id_old,name,symbol,alternative_names,alternative_symbols,isvip,hasvariantannotation,cross_reference,hascpicguidline,chromosome,ch37_start,ch37_stop,ch38_start,ch38_stop,ensembl_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
                String line;
                while ((line = br.readLine()) != null) {
                    String[] field = line.split("\\t");
                    for (int j=1; j<=18;j++){
                        preparedStatement.setString(j,field[j-1]); // 18 fields in total will be inserted
                    }
                    if (field.length<18){ // if record is less than essential length
                        for (int j=field.length+1; j<=18;j++){
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
            } catch (SQLException e) {
                //@Todo handle SQL exception
            } catch (FileNotFoundException fn){
                //@Todo handle file not found exception
            } catch (IOException i){
                //@Todo handle other file IO exceptions
            }
        });
        return counter.get();
    }

}
