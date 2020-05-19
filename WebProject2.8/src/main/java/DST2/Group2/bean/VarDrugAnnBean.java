package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean for variant~drug annotation.
 * Related genes, drug, annotation are reported in the matching report.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class VarDrugAnnBean {
	private String variantID;
	private String location;
	private String gene;
	private String drug;
	private String notes;
	private String annotation;
	public VarDrugAnnBean(String variantID, String location, String gene, String drug, String notes, String annotation) {
		super();
		this.variantID = variantID;
		this.location = location;
		this.gene = gene;
		this.drug = drug;
		this.notes = notes;
		this.annotation = annotation;
	}
	public String getVariantID() {
		return variantID;
	}
	public void setVariantID(String variantID) {
		this.variantID = variantID;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getDrug() {
		return drug;
	}
	public void setDrug(String drug) {
		this.drug = drug;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

}
