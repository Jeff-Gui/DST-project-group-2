package DST2.Group2.DAO;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.Utils.ListMatch;
import DST2.Group2.bean.VarDrugAnnBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class VarDrugAnnDAO {

	private static final Logger log = LoggerFactory.getLogger(VarDrugAnnDAO.class);

	public List<VarDrugAnnBean> search(String drugName, String phenotype, List<VarDrugAnnBean> varDrugAnnBeans) {
		Iterator<VarDrugAnnBean> iterator= varDrugAnnBeans.iterator();
		String[] drugList=drugName.split(",");
		String[] phenList=phenotype.split(",");
		boolean hasDrug=false;
		boolean hasPhen=false;
		while(iterator.hasNext()) {
			VarDrugAnnBean VarDrugAnnBean =iterator.next();
			if (VarDrugAnnBean.getAnnotation()!=null && VarDrugAnnBean.getNotes()!=null) {
			hasDrug= ListMatch.listMatch(VarDrugAnnBean.getDrug(),drugList);
			hasPhen=ListMatch.listMatch2(VarDrugAnnBean.getNotes(), VarDrugAnnBean.getAnnotation(),phenList);}
			if (!hasDrug || !hasPhen) {
				iterator.remove();
			}
		}
		return varDrugAnnBeans;
	}

	public List<VarDrugAnnBean> findAll() {
		List<VarDrugAnnBean> allAnns=new ArrayList<>();
		DBmethods.execSQL(connection -> {
		try {
			PreparedStatement preparedStatement = connection.prepareStatement("Select distinct variant,location,gene,chemical,notes,sentence from location_annvar");
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
			// TODO handle SQL exception
			e.printStackTrace();
		}});
		return allAnns;
	}

	public int doImportVarDrugAnn(boolean doDelete) {
		/**
		 * Download and import the latest var_drug_ann table from PGKB.
		 * @Done: test
		 * @ToDo: IO Error handling
		 */
		AtomicInteger counter = new AtomicInteger();
		DBmethods.execSQL(connection -> {
			try{
				// 1. Check file exist
				BufferedReader br = new BufferedReader(
						new InputStreamReader(
								new DataInputStream(getClass().getResourceAsStream("/tables/var_drug_ann.tsv"))));
				String header = br.readLine(); // first line is col name
				if (header.equals("")){ throw new IOException(); } // if file empty, throw an error
				// 2. Delete old data
				if (doDelete) { connection.createStatement().execute("DELETE FROM var_drug_ann;"); }
				// 3. Insert new data
				connection.setAutoCommit(false);
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO var_drug_ann(annotation_id,variant,gene,chemical,pmid,phenotype_category,significance,notes,sentence,studyparam,allels,choromosome) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
				String line;
				while ((line = br.readLine()) != null) {
					String[] field = line.split("\t");
					for (int j=1; j<=field.length;j++){
						preparedStatement.setString(j,field[j-1]); // 12 fields in total will be inserted
					}
					if (field.length<12){ // if record is less than essential length
						for (int j=field.length+1; j<=12;j++){
							preparedStatement.setString(j, null);
						}
					}
					preparedStatement.addBatch();
					if (counter.get() % 1000 == 0) {
						// execute batch for every 1000 records
						preparedStatement.executeBatch();
						connection.commit();
					}
					counter.getAndIncrement();
				}
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		});
		return counter.get();
	}

	public int doImportVariant(boolean doDelete){
		/**
		 * Download and import the latest variant table from PGKB.
		 * And then update location annvar table.
		 * @Done: test
		 * @ToDo: IO Error handling
		 */
		AtomicInteger counter = new AtomicInteger();
		DBmethods.execSQL(connection -> {
			try{
				// 1. Check file exist
				BufferedReader br = new BufferedReader(
						new InputStreamReader(
								new DataInputStream(getClass().getResourceAsStream("/tables/variant.tsv"))));
				String header = br.readLine(); // first line is col name
				if (header.equals("")){ throw new IOException(); } // if file empty, throw an error
				// 2. Delete old data if not init
				if (doDelete) { connection.createStatement().execute("DELETE FROM variant;"); }
				// 3. Insert new data
				connection.setAutoCommit(false);
				PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO variant(variant_id,variant_name,gene_ids,gene_symbols,location,variant_annotation_count,clinical_annotation_count,high_level_clinical_annotation,guidance_annotation_count,label_annotation_count,synonyms) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
				String line;
				while ((line = br.readLine()) != null) {
					String[] field = line.split("\\t");
					if (field.length<11) {
						// if no essential fields
						continue;
					}
					for (int i=0; i<=4; i++){
						preparedStatement.setString(i+1,field[i]);
					}
					for (int i=5; i<=9; i++){
						preparedStatement.setInt(i+1,Integer.parseInt(field[i]));
					}
					preparedStatement.setString(11,field[10]);
					preparedStatement.addBatch();
					if (counter.get() % 1000 == 0) {
						// execute batch for every 1000 records
						preparedStatement.executeBatch();
						connection.commit();
					}
					counter.getAndIncrement();
				}

				// uppdate location_annvar table
				if (doDelete){ connection.createStatement().execute("DROP TABLE location_annvar;"); }
				connection.createStatement().execute("SELECT create_location_annvar(); " );
				connection.commit();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		});
		return counter.get();
	}

}
