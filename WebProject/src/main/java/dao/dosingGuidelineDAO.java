package dao;

import DBmtd.DBmethods;
import bean.DosingGuidelineBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class dosingGuidelineDAO {
	public static List<DosingGuidelineBean> searchByDrug(String drugName, List<DosingGuidelineBean> dosingGuidelineBeans) {
		Iterator<DosingGuidelineBean> iterator= dosingGuidelineBeans.iterator();
		while(iterator.hasNext()) {
			DosingGuidelineBean dosingGuidelineBean =iterator.next();
			if (drugName.equals(dosingGuidelineBean.getDrug())) {
				iterator.remove();
			}
		}
		return dosingGuidelineBeans;
	}
	public static List<DosingGuidelineBean> searchByPhenotype(String phenotype, List<DosingGuidelineBean> dosingGuidelineBeans) {
		Iterator<DosingGuidelineBean> iterator= dosingGuidelineBeans.iterator();
		while(iterator.hasNext()) {
			DosingGuidelineBean dosingGuidelineBean =iterator.next();
			String summary= dosingGuidelineBean.getSummary_markdown();
			if (!summary.contains(phenotype)) {
				iterator.remove();
			}
		}
		return dosingGuidelineBeans;
	}
	
	public static List<DosingGuidelineBean> getDosingGuideline() {
		Connection postgres = DBmethods.getConnection();
		List<DosingGuidelineBean> allGuidelines=new ArrayList<>();
		try {
			PreparedStatement preparedStatement = postgres.prepareStatement("Select name,drug, source,recommendation,summary_markdown from dosing_guideline_name");
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
		}
		return allGuidelines;
	}
}
