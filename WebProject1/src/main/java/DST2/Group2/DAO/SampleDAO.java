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
import org.springframework.stereotype.Repository;

@Repository
public class SampleDAO {
    public int save(String uploadedBy) {
        AtomicInteger key = new AtomicInteger(0);
        AtomicInteger newid= new AtomicInteger(1);
        DBmethods.execSQL(connection -> {
            try {

                PreparedStatement preparedStatement = connection.prepareStatement("insert into sample(created_at, uploaded_by) values (?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setTimestamp(1, new Timestamp(new Date().getTime()));
                preparedStatement.setString(2, uploadedBy);
                System.out.println(key.getAndSet(preparedStatement.executeUpdate()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            Statement stmt= null;
            try {
                stmt = connection.createStatement();
                String getid="select max(id) from sample";
                ResultSet rs=stmt.executeQuery(getid);
                while(rs.next()) {
                    newid.set(rs.getInt("max")+1);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        });
        System.out.println("decrement and get");
        return newid.decrementAndGet();
    }


    public SampleBean findById(int id) {
        AtomicReference<SampleBean> sample = new AtomicReference<>();
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select id, created_at, uploaded_by from sample where id = ?");
                preparedStatement.setInt(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int sampleId = resultSet.getInt("id");
                    Date createdAt = new Date(resultSet.getTimestamp("created_at").getTime());
                    String uploadedBy = resultSet.getString("uploaded_by");
                    sample.set(new SampleBean(sampleId, createdAt, uploadedBy));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});

        return sample.get();
    }
    public static List<SampleBean> findAll() {
        List<SampleBean> sampleBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from sample");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int sampleId = resultSet.getInt("id");
                    Date createdAt = new Date(resultSet.getTimestamp("created_at").getTime());
                    String uploadedBy = resultSet.getString("uploaded_by");
                    SampleBean sampleBean = new SampleBean(sampleId, createdAt, uploadedBy);
                    sampleBeans.add(sampleBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});

        return sampleBeans;
    }
}
