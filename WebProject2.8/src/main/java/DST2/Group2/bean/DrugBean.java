package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean for drug, only used by internal models.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class DrugBean {

    private String id;
    private String name;
    private boolean biomarker;
    private String drugUrl;
    private String ObjCls;

    public DrugBean(String id, String name, boolean biomarker, String drugUrl, String objCls) {
        this.id = id;
        this.name = name;
        this.biomarker = biomarker;
        this.drugUrl = drugUrl;
        ObjCls = objCls;
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

    public boolean isBiomarker() {
        return biomarker;
    }

    public void setBiomarker(boolean biomarker) {
        this.biomarker = biomarker;
    }

    public String getDrugUrl() {
        return drugUrl;
    }

    public void setDrugUrl(String drugUrl) {
        this.drugUrl = drugUrl;
    }

    public String getObjCls() {
        return ObjCls;
    }

    public void setObjCls(String objCls) {
        ObjCls = objCls;
    }

}
