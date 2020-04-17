package dao;

import DBmtd.DBmethods;
import bean.VarDrugAnnBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VarDrugAnnDAO {

	public List<VarDrugAnnBean> searchByDrug(String drug, List<VarDrugAnnBean> anns) {
		Iterator<VarDrugAnnBean> iterator=anns.iterator();
		while(iterator.hasNext()) {
			VarDrugAnnBean ann=iterator.next();
			if (!drug.equals(ann.getDrug())) {
				iterator.remove();
			}
		}
		return anns;
	}
	
	public List<VarDrugAnnBean> searchByPhen(String phen, List<VarDrugAnnBean> anns) {
		Iterator<VarDrugAnnBean> iterator=anns.iterator();
		while(iterator.hasNext()) {
			VarDrugAnnBean ann=iterator.next();
			if (!ann.getAnnotation().contains(phen)) {
				iterator.remove();
			}
		}
		return anns;
	}
	public List<VarDrugAnnBean> getAnn() {
		Connection postgres= DBmethods.getConnection();
		List<VarDrugAnnBean> allAnns=new ArrayList<>();
		try {
			PreparedStatement preparedStatement = postgres.prepareStatement("Select variant,location,gene,chemical,notes,sentence from location_annvar");
			ResultSet rs=preparedStatement.executeQuery();
			while (rs.next()) {
				String variant=rs.getString("variant");
				String location=rs.getString("location");
				String gene=rs.getString("gene");
				String drug=rs.getString("drug");
				String notes=rs.getString("notes");
				String ann=rs.getString("sentence");
				VarDrugAnnBean varDrugAnn= new VarDrugAnnBean(variant,location,gene,drug,notes,ann);
				allAnns.add(varDrugAnn);	
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allAnns;
	}
}
