package DST2.Group2.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DST2.Group2.Database.database;
import DST2.Group2.bean.DrugLabelBean;

public class DrugLabelDAO {
	
	
	public static List<DrugLabelBean> searchByDrug(String drugName, List<DrugLabelBean> drugLabelBeans) {
		Iterator<DrugLabelBean> iterator= drugLabelBeans.iterator();
		while(iterator.hasNext()) {
			DrugLabelBean drugLabelBean =iterator.next();
			if (!drugName.equals(drugLabelBean.getDrugName())) {
				iterator.remove();
			}
		}
		return drugLabelBeans;
	}
	public static List<DrugLabelBean> searchByPhenotype(String phenotype, List<DrugLabelBean> drugLabelBeans) {
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
	
	public static List<DrugLabelBean> getDrugLabel() {
		Connection postgres=database.connpostgres();
		List<DrugLabelBean> allLabels=new ArrayList<>();
		try {
			PreparedStatement preparedStatement = postgres.prepareStatement("Select name,alternate_drug_availabel, source, summary_markdown from drugNames");
			ResultSet rs=preparedStatement.executeQuery();
			while (rs.next()) {
				String gene=null;
				String name=rs.getString("name");
				boolean alternate_drug_availabel=rs.getBoolean("alternate_drug_availabel");
				String source=rs.getString("source");
				String summary_markdown=rs.getString("summary_markdown");
				DrugLabelBean druglabel=new DrugLabelBean(gene,name,source,alternate_drug_availabel,summary_markdown);
				allLabels.add(druglabel);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allLabels;
	}
	
}


