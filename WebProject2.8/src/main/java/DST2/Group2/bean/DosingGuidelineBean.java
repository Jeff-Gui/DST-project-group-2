package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean class for dosing guideline, which is implemented in two tables of the database (on joined table).
 * name, related drug and summary markdown are reported in the matching result.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class DosingGuidelineBean {
	private String id;
	private String obj_cls;
	private String variant_gene;
	private String name;
	private String drug;
	private String drug_id;
	private String source;
	private boolean recommendation;
	private String summary_markdown;
	private String text_markdown;
	private String raw;

	public DosingGuidelineBean(String variant_gene, String name, String drug, String source, boolean recommendation,
							   String summary_markdown) {
		// constructor for getting dosing guideline from the dosing_guideline_name table (read data)
		this.variant_gene = variant_gene;
		this.name=name;
		this.drug = drug;
		this.source = source;
		this.recommendation = recommendation;
		this.summary_markdown = summary_markdown;
	}

	public DosingGuidelineBean(String id, String obj_cls, String name, boolean rec, String drug_id, String source,
							   String summary_markdown, String text_markdown, String raw) {
		// constructor for getting dosing guideline from the dosing_guideline_name table (read data)
		this.id = id;
		this.variant_gene = name;
		this.name=name;
		this.obj_cls = obj_cls;
		this.drug_id = drug_id;
		this.source = source;
		this.recommendation = rec;
		this.summary_markdown = summary_markdown;
		this.text_markdown = text_markdown;
		this.raw = raw;
	}

	public String getVariant_gene() {
		return variant_gene;
	}

	public void setVariant_gene(String variant_gene) {
		this.variant_gene = variant_gene;
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

	public String getDrug() {
		return drug;
	}

	public void setDrug(String drug) {
		this.drug = drug;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public boolean isRecommendation() {
		return recommendation;
	}

	public void setRecommendation(boolean recommendation) {
		this.recommendation = recommendation;
	}

	public String getSummary_markdown() {
		return summary_markdown;
	}

	public void setSummary_markdown(String summary_markdown) {
		this.summary_markdown = summary_markdown;
	}

	public String getObj_cls() {
		return obj_cls;
	}

	public void setObj_cls(String obj_cls) {
		this.obj_cls = obj_cls;
	}

	public String getDrug_id() {
		return drug_id;
	}

	public void setDrug_id(String drug_id) {
		this.drug_id = drug_id;
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
}
