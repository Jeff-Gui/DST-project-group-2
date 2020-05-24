package DST2.Group2.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.SampleBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SampleDAO {

    private static final Logger log = LoggerFactory.getLogger(SampleDAO.class);

    public int save(String uploadedBy, String description, String sample_type, boolean publicity) {
        AtomicInteger newid= new AtomicInteger(1);
        DBmethods.execSQL(connection -> {
            try {
                ResultSet rs = connection.createStatement().executeQuery("SELECT max(id) FROM sample");
                while(rs.next()) {
                    newid.set(rs.getInt("max") + 1);
                }
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO sample(id,created_at, uploaded_by, description, sample_type, publicity) VALUES (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setTimestamp(2, new Timestamp(new Date().getTime()));
                preparedStatement.setString(3, uploadedBy);
                preparedStatement.setInt(1,newid.get());
                preparedStatement.setString(4,description);
                preparedStatement.setString(5,sample_type);
                preparedStatement.setBoolean(6,publicity);
                preparedStatement.execute();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        return newid.get();
    }


    public SampleBean findById(int id) {
        AtomicReference<SampleBean> sample = new AtomicReference<>();
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, created_at, uploaded_by, description, sample_type, publicity FROM sample WHERE id = ?");
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int sampleId = resultSet.getInt("id");
                    Date createdAt = new Date(resultSet.getTimestamp("created_at").getTime());
                    String uploadedBy = resultSet.getString("uploaded_by");
                    String description = resultSet.getString("description");
                    String sample_type = resultSet.getString("sample_type");
                    boolean publicity = resultSet.getBoolean("publicity");
                    sample.set(new SampleBean(sampleId, createdAt, description, uploadedBy, sample_type, publicity));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});

        return sample.get();
    }

    public List<SampleBean> findAll(String username, boolean admin) {
        List<SampleBean> sampleBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {

            try {
                ResultSet resultSet;
                if (admin) {
                    resultSet = connection.createStatement().executeQuery("SELECT * FROM sample;");
                } else {
                    resultSet = connection.createStatement().executeQuery("SELECT * FROM sample WHERE uploaded_by = \'"+username+"\' or publicity = true;");
                }
                while (resultSet.next()) {
                    int sampleId = resultSet.getInt("id");
                    Date createdAt = new Date(resultSet.getTimestamp("created_at").getTime());
                    String uploadedBy = resultSet.getString("uploaded_by");
                    String description = resultSet.getString("description");
                    String sample_type = resultSet.getString("sample_type");
                    boolean publicity = resultSet.getBoolean("publicity");
                    SampleBean sampleBean = new SampleBean(sampleId, createdAt, description, uploadedBy, sample_type, publicity);
                    sampleBeans.add(sampleBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});

        return sampleBeans;
    }

    public void deleteById(int id){
        /**
         * @Description
         * Delete target sample in the database, its metadata in sample table and data in vep/annovar table according to its sample type.
         * @Param  [id]: sample ID
         * @Return void
         * @Date 2020/5/12
         * @author DST group 2
        **/
        DBmethods.execSQL(connection -> {
            try {
                ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM sample WHERE id=" + id +";");
                rs.next();
                String sample_type = rs.getString("sample_type");
                connection.createStatement().execute("DELETE FROM sample WHERE id=" + id +";");
                if (sample_type.equals("annovar")){
                    connection.createStatement().execute("DELETE FROM annovar WHERE sample_id=" + id +";");
                } else {
                    connection.createStatement().execute("DELETE FROM vep WHERE sample_id=" + id +";");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});
    }
}