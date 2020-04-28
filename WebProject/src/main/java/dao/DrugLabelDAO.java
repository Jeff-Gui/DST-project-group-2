package dao;

import DBmtd.DBmethods;
import bean.DrugLabelBean;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Repository
public class DrugLabelDAO {

	public List<DrugLabelBean> searchByDrug(String drugName, List<DrugLabelBean> drugLabelBeans) {
		Iterator<DrugLabelBean> iterator= drugLabelBeans.iterator();
		while(iterator.hasNext()) {
			DrugLabelBean drugLabelBean =iterator.next();
			if (!drugName.equals(drugLabelBean.getDrugName())) {
				iterator.remove();
			}
		}
		return drugLabelBeans;
	}
	public List<DrugLabelBean> searchByPhenotype(String phenotype, List<DrugLabelBean> drugLabelBeans) {
		Iterator<DrugLabelBean> iterator= drugLabelBeans.iterator();
		while(iterator.hasNext()) {
			DrugLabelBean drugLabelBean =iterator.next();
			String summary= drugLabelBean.getSummary_markdown();
			if (!summary.contains(phenotype)) {
				iterator.remove();
			}
		}
		return drugLabelBeans;
	}
	
	public List<DrugLabelBean> getDrugLabel() {
		Connection postgres= DBmethods.getConnection();
		List<DrugLabelBean> allLabels=new ArrayList<>();
		try {
			PreparedStatement preparedStatement = postgres.prepareStatement("Select id, name,alternate_drug_available, source, summary_markdown from drugNames");
			ResultSet rs=preparedStatement.executeQuery();
			while (rs.next()) {
				String id=rs.getString("id");
				String name=rs.getString("name");
				boolean alternate_drug_available=rs.getBoolean("alternate_drug_available");
				String source=rs.getString("source");
				String summary_markdown=rs.getString("summary_markdown");
				DrugLabelBean druglabel=new DrugLabelBean(id,null,name,source,alternate_drug_available,summary_markdown);
				allLabels.add(druglabel);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allLabels;
	}
	
}


