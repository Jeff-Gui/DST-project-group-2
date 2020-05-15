package DST2.Group2.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.DosingGuidelineBean;
import DST2.Group2.bean.DrugBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class DosingGuidelineDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(DosingGuidelineDAO.class);

	public boolean existsById(String id) {
		return super.existsById(id, "dosing_guideline");
	}

	public List<DosingGuidelineBean> search(String drugName, String phenotype, List<DosingGuidelineBean> dosingGuidelineBeans) {
		Iterator<DosingGuidelineBean> iterator= dosingGuidelineBeans.iterator();
		String[] drugList=drugName.split(",");
		String[] phenList=phenotype.split(",");
		while(iterator.hasNext()) {
			DosingGuidelineBean DosingGuidelineBean =iterator.next();
			boolean hasDrug= ListMatch.listMatch(DosingGuidelineBean.getName(),drugList);
			boolean hasPhen=ListMatch.listMatch(DosingGuidelineBean.getSummary_markdown(),phenList);
			if (!hasDrug || !hasPhen) {
				iterator.remove();
			}
		}
		return dosingGuidelineBeans;
	}

	public List<DosingGuidelineBean> findAll() {
		List<DosingGuidelineBean> allGuidelines=new ArrayList<>();
		DBmethods.execSQL(connection -> {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement("Select name,drug, source,recommendation,summary_markdown from dosing_guideline_name");
				ResultSet rs=preparedStatement.executeQuery();
				while (rs.next()) {
					String gene = null;
					String name=rs.getString("name");
					String drug=rs.getString("drug");
					String source=rs.getString("source");
					boolean rec=rs.getBoolean("recommendation");
					String summary_markdown=rs.getString("summary_markdown");
					DosingGuidelineBean dosingGuidelineBean =new DosingGuidelineBean(gene,name,drug,source,rec,summary_markdown);
					allGuidelines.add(dosingGuidelineBean);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}});
		return allGuidelines;
	}

	public void saveDosingGuideline(DosingGuidelineBean dosingGuidelineBean) {
		DBmethods.execSQL(connection -> {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO dosing_guideline(id, obj_cls,name,recommendation,drug_id,source,summary_markdown,text_markdown,raw) VALUES(?,?,?,?,?,?,?,?,?);");
				preparedStatement.setString(1, dosingGuidelineBean.getId());
				preparedStatement.setString(2, dosingGuidelineBean.getObj_cls());
				preparedStatement.setString(3, dosingGuidelineBean.getName());
				preparedStatement.setBoolean(4, dosingGuidelineBean.isRecommendation());
				preparedStatement.setString(5, dosingGuidelineBean.getDrug_id());
				preparedStatement.setString(6,dosingGuidelineBean.getSource());
				preparedStatement.setString(7,dosingGuidelineBean.getSummary_markdown());
				preparedStatement.setString(8,dosingGuidelineBean.getText_markdown());
				preparedStatement.setString(9,dosingGuidelineBean.getRaw());
				preparedStatement.execute();
			} catch (SQLException e) {
				log.info("", e);
			}
		});
	}

	public void doImportGuidelineName(boolean doDelete){
		// create dosing_guideline_name table via join
		DBmethods.execSQL(connection -> {
			try {
				if (doDelete) {connection.createStatement().execute("DROP TABLE dosing_guideline_name;");}
				connection.createStatement().execute("SELECT create_dosing_guideline_name();");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}

}
