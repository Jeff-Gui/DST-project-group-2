package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.GeneBean;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GeneDAO {

    public static List<GeneBean> findAll(){
        List<GeneBean>  geneBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {
            try{
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT symbol, ensemble_id FROM symbol_ensembl;");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String symbol = resultSet.getString("symbol");
                    String ensembl_id = resultSet.getString("ensemble_id");
                    GeneBean geneBean = new GeneBean(symbol, ensembl_id);
                    geneBeans.add(geneBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return geneBeans;
    }
}
