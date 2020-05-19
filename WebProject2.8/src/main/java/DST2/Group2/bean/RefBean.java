package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean describing sample information used for matching.
 * Support both annovar-annotated or VEP-annotated data since only fields covered by both is required for matching.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class RefBean {

    private String location;
    private String allele;
    private String ori_gene;
    private String sym_gene;

    public RefBean(String location, String allele, String ori_gene, String sym_gene) {
        this.location = location;
        this.allele = allele;
        this.ori_gene = ori_gene;
        this.sym_gene = sym_gene;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAllele() {
        return allele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }

    public String getOri_gene() {
        return ori_gene;
    }

    public void setOri_gene(String ori_gene) {
        this.ori_gene = ori_gene;
    }

    public String getSym_gene() {
        return sym_gene;
    }

    public void setSym_gene(String sym_gene) {
        this.sym_gene = sym_gene;
    }
}
