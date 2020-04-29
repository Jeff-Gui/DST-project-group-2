package DST2.Group2.DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.VarDrugAnnBean;
import org.springframework.stereotype.Repository;

@Repository
public class VarDrugAnnDAO {

	public List<VarDrugAnnBean> search(String drugName, String phenotype, List<VarDrugAnnBean> varDrugAnnBeans) {
		Iterator<VarDrugAnnBean> iterator= varDrugAnnBeans.iterator();
		String[] drugList=drugName.split(",");
		String[] phenList=phenotype.split(",");
		while(iterator.hasNext()) {
			VarDrugAnnBean VarDrugAnnBean =iterator.next();
			Boolean hasDrug= ListMatch.listMatch(VarDrugAnnBean.getDrug(),drugList);
			Boolean hasPhen=ListMatch.listMatch2(VarDrugAnnBean.getNotes(), VarDrugAnnBean.getAnnotation(),phenList);
			if (hasDrug==false || hasPhen==false) {
				iterator.remove();
			}
		}
		return varDrugAnnBeans;
	}

	public List<VarDrugAnnBean> getAnn() {
		List<VarDrugAnnBean> allAnns=new ArrayList<>();
		DBmethods.execSQL(connection -> {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("Select variant,location,gene,chemical,notes,sentence from location_annvar");
			ResultSet rs=preparedStatement.executeQuery();
			while (rs.next()) {
				String variant=rs.getString("variant");
				String location=rs.getString("location");
				String gene=rs.getString("gene");
				String drug=rs.getString("chemical");
				String notes=rs.getString("notes");
				String ann=rs.getString("sentence");
				VarDrugAnnBean varDrugAnnBean = new VarDrugAnnBean(variant,location,gene,drug,notes,ann);

				allAnns.add(varDrugAnnBean);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}});
		return allAnns;
	}
}
