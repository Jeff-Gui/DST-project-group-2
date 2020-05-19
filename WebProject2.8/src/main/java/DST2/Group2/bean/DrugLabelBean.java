package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean for drug label, used by two implementations tables in the database (one joined)
 * name, prescribing markdown, related drug and variant are reported in the matching result.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class DrugLabelBean {
	private String id;
	private String name;
	private String obj_cls;
	private boolean hasAlternativeDrug;
	private boolean hasDosingInfo;
	private String prescribing_markdown;
	private String variantGene;
	private String drugName;
	private String source;
	private String summary_markdown;
	private String text_markdown;
	private String raw;
	private String drug_id;

	
	public DrugLabelBean(String variantGene, String drugName, String source, boolean hasAlternativeDrug,
                         String summary_markdown) {
		// for reading information from drugnames table
		this.variantGene = variantGene;
		this.drugName = drugName;
		this.source = source;
		this.hasAlternativeDrug = hasAlternativeDrug;
		this.summary_markdown = summary_markdown;
	}
	public DrugLabelBean(String id, String name, String obj_cls, boolean hasAlternativeDrug, boolean hasDosingInfo, String prescribing_markdown, String source, String text_markdown, String summary_markdown, String raw, String drug_id){
		this.id = id;
		this.name = name;
		this.obj_cls = obj_cls;
		this.hasAlternativeDrug = hasAlternativeDrug;
		this.hasDosingInfo = hasDosingInfo;
		this.prescribing_markdown = prescribing_markdown;
		this.source = source;
		this.text_markdown = text_markdown;
		this.summary_markdown = summary_markdown;
		this.raw = raw;
		this.drug_id = drug_id;
	}

	public boolean gethasAlternativeDrug() {
		return hasAlternativeDrug;
	}

	public void sethasAlternativeDrug(boolean hasAlternativeDrug) {
		this.hasAlternativeDrug = hasAlternativeDrug;
	}

	public String getvariantGene() {
		return variantGene;
	}

	public void setvariantGene(String variantGene) {
		this.variantGene = variantGene;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSummary_markdown() {
		return summary_markdown;
	}

	public void setSummary_markdown(String summary_markdown) {
		this.summary_markdown = summary_markdown;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getObj_cls() {
		return obj_cls;
	}

	public void setObj_cls(String obj_cls) {
		this.obj_cls = obj_cls;
	}

	public boolean isHasDosingInfo() {
		return hasDosingInfo;
	}

	public void setHasDosingInfo(boolean hasDosingInfo) {
		this.hasDosingInfo = hasDosingInfo;
	}

	public String getPrescribing_markdown() {
		return prescribing_markdown;
	}

	public void setPrescribing_markdown(String prescribing_markdown) {
		this.prescribing_markdown = prescribing_markdown;
	}

	public String getVariantGene() {
		return variantGene;
	}

	public void setVariantGene(String variantGene) {
		this.variantGene = variantGene;
	}

	public String getText_markdown() {
		return text_markdown;
	}

	public void setText_markdown(String text_markdown) {
		this.text_markdown = text_markdown;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public String getDrug_id() {
		return drug_id;
	}

	public void setDrug_id(String drug_id) {
		this.drug_id = drug_id;
	}
}
