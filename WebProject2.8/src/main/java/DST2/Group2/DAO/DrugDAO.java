package DST2.Group2.DAO;

import DST2.Group2.bean.DrugBean;
import DST2.Group2.Database.DBmethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DrugDAO extends BaseDAO {

    private static final Logger log = LoggerFactory.getLogger(DrugDAO.class);

    public boolean existsById(String id) {
        return super.existsById(id, "drug");
    }

    public void saveDrug(DrugBean drugBean) {
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("insert into drug (id, name, obj_cls, biomarker, drug_url) values    (?,?,?,?,?)");
                preparedStatement.setString(1, drugBean.getId());
                preparedStatement.setString(2, drugBean.getName());
                preparedStatement.setString(3, drugBean.getObjCls());
                preparedStatement.setBoolean(4, drugBean.isBiomarker());
                preparedStatement.setString(5, drugBean.getDrugUrl());
                preparedStatement.execute();
            } catch (SQLException e) {
                log.info("", e);
            }
        });
    }

    public List<DrugBean> findAll() {
        List<DrugBean> drugBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select id,name,obj_cls,drug_url,biomarker from drug");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String objCls = resultSet.getString("obj_cls");
                    String drugUrl = resultSet.getString("drug_url");
                    boolean biomarker = resultSet.getBoolean("biomarker");
                    DrugBean drugBean = new DrugBean(id, name, biomarker, drugUrl, objCls);
                    drugBeans.add(drugBean);
                }
            } catch (SQLException e) {
                log.info("", e);
            }
        });
        return drugBeans;
    }

}
