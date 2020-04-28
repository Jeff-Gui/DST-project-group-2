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
import org.springframework.stereotype.Repository;

@Repository
public class DosingGuidelineDAO {
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

	public List<DosingGuidelineBean> getDosingGuideline() {
		List<DosingGuidelineBean> allGuidelines=new ArrayList<>();
		DBmethods.execSQL(connection -> {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement("Select name,drug, source,recommendation,summary_markdown from dosing_guideline_name");
				ResultSet rs=preparedStatement.executeQuery();
				while (rs.next()) {
					String gene=null;
					String name=rs.getString("name");
					String drug=rs.getString("drug");
					String source=rs.getString("source");
					boolean rec=rs.getBoolean("recommendation");
					String summary_markdown=rs.getString("summary_markdown");
					DosingGuidelineBean dosingGuidelineBean =new DosingGuidelineBean(gene,name,drug,source,rec,summary_markdown);
					allGuidelines.add(dosingGuidelineBean);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}});
		return allGuidelines;
	}
}
