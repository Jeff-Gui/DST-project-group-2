package DST2.Group2.DAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.DrugLabelBean;
import org.springframework.stereotype.Repository;

@Repository
public class DrugLabelDAO {

	public List<DrugLabelBean> search(String drugName, String phenotype, List<DrugLabelBean> drugLabelBeans) {
		Iterator<DrugLabelBean> iterator= drugLabelBeans.iterator();
		String[] drugList=drugName.split(",");
		String[] phenList=phenotype.split(",");
		while(iterator.hasNext()) {
			DrugLabelBean DrugLabelBean =iterator.next();
			boolean hasDrug= ListMatch.listMatch(DrugLabelBean.getDrugName(),drugList);
			boolean hasPhen=ListMatch.listMatch(DrugLabelBean.getSummary_markdown(),phenList);;

			if (!hasDrug || !hasPhen) {
				iterator.remove();
			}
		}
		return drugLabelBeans;
	}

	public List<DrugLabelBean> getDrugLabel() {
		List<DrugLabelBean> allLabels=new ArrayList<>();
		DBmethods.execSQL(connection -> {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement("Select name,alternate_drug_available, source, summary_markdown from drugNames");
				ResultSet rs=preparedStatement.executeQuery();
				while (rs.next()) {
					String gene=null;
					String name=rs.getString("name");
					boolean alternate_drug_availabel=rs.getBoolean("alternate_drug_available");
					String source=rs.getString("source");
					String summary_markdown=rs.getString("summary_markdown");
					DrugLabelBean druglabel=new DrugLabelBean(gene,name,source,alternate_drug_availabel,summary_markdown);
					allLabels.add(druglabel);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}});
		return allLabels;
	}

}