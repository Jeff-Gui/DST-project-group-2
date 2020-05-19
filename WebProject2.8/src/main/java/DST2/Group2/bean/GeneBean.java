package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean for gene, internally used for Ensembl id ~ gene symbol mapping.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class GeneBean {
    private String symbol;
    private String ensembl_id;

    public GeneBean(String symbol, String ensembl_id) {
        this.symbol = symbol;
        this.ensembl_id = ensembl_id;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getEnsembl_id() {
        return ensembl_id;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setEnsembl_id(String ensembl_id) {
        this.ensembl_id = ensembl_id;
    }
}
