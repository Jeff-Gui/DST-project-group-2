package DST2.Group2.bean;

import java.util.Date;

/**
 * @Description This is the description of class
 * Bean for sample metadata, including automatically allocated ID, upload time (createdAt), user and sample type (annovar or VEP).
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class SampleBean {
    private int id;
    private Date createdAt;
    private String uploadedBy;
    private String description;
    private String sampleType;
    private boolean publicity;

    public SampleBean(int id, Date createdAt, String description, String uploadedBy, String sampleType, boolean publicity) {
        this.id = id;
        this.description = description;
        this.createdAt = createdAt;
        this.uploadedBy = uploadedBy;
        this.sampleType = sampleType;
        this.publicity = publicity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.sampleType = description;
    }

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    public boolean getPublicity() {
        return publicity;
    }

    public void setPublicity(boolean publicity) { this.publicity = publicity; }
}
