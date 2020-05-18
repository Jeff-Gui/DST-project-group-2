package DST2.Group2.bean;

import java.util.Date;

public class SampleBean {
    private int id;
    private Date createdAt;
    private String uploadedBy;
    private String sampleType;

    public SampleBean(int id, Date createdAt, String uploadedBy, String sampleType) {
        this.id = id;
        this.createdAt = createdAt;
        this.uploadedBy = uploadedBy;
        this.sampleType = sampleType;
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

    public String getSampleType() {
        return sampleType;
    }

    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }
}
