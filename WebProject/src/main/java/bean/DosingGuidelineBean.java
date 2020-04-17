package bean;

public class DosingGuidelineBean {

	private String variant_gene;
	private String name;
	private String drug;
	private String source;
	private boolean recommendation;
	private String summary_markdown;
	private String id;

	public DosingGuidelineBean(String id, String variant_gene, String name, String drug, String source, boolean recommendation,
							   String summary_markdown) {
		this.id = id;
		this.variant_gene = variant_gene;
		this.name=name;
		this.drug = drug;
		this.source = source;
		this.recommendation = recommendation;
		this.summary_markdown = summary_markdown;
	}

	public String getVariant_gene() {
		return variant_gene;
	}

	public void setVariant_gene(String variant_gene) {
		this.variant_gene = variant_gene;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean equals(Object obj) {
		try {
			DosingGuidelineBean external = (DosingGuidelineBean) obj;
			return this.id.equals(external.getId());
		} catch (Exception e) {
			return false;
		}
	}

}
