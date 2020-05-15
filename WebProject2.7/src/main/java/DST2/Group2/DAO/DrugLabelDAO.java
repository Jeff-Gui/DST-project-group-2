package DST2.Group2.DAO;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.DrugBean;
import DST2.Group2.bean.DrugLabelBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class DrugLabelDAO extends BaseDAO {

	private static final Logger log = LoggerFactory.getLogger(DrugLabelDAO.class);

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

	public List<DrugLabelBean> findAll() {
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
				e.printStackTrace();
			}});
		return allLabels;
	}

	public boolean existsById(String id) {
		return super.existsById(id, "drug_label");
	}

	public void saveDrugLabel(DrugLabelBean drugLabelBean) {
		DBmethods.execSQL(connection -> {
			try {
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO drug_label(id,name,obj_cls,alternate_drug_available,dosing_information,prescribing_markdown,source,text_markdown,summary_markdown,raw,drug_id) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				preparedStatement.setString(1, drugLabelBean.getId());
				preparedStatement.setString(2, drugLabelBean.getName());
				preparedStatement.setString(3, drugLabelBean.getObj_cls());
				preparedStatement.setBoolean(4, drugLabelBean.gethasAlternativeDrug());
				preparedStatement.setBoolean(5, drugLabelBean.isHasDosingInfo());
				preparedStatement.setString(6,drugLabelBean.getPrescribing_markdown());
				preparedStatement.setString(7,drugLabelBean.getSource());
				preparedStatement.setString(8,drugLabelBean.getText_markdown());
				preparedStatement.setString(9,drugLabelBean.getSummary_markdown());
				preparedStatement.setString(10,drugLabelBean.getRaw());
				preparedStatement.setString(11,drugLabelBean.getDrug_id());
				preparedStatement.execute();
			} catch (SQLException e) {
				log.info("", e);
			}
		});

	}

	public void doImportDrugName(boolean doDelete){
		// create drugNames table via join
		DBmethods.execSQL(connection -> {
			try {
				if (doDelete) {connection.createStatement().execute("DROP TABLE drugnames;");}
				connection.createStatement().execute("SELECT create_drugNames()");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});
	}
}