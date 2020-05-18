package basic;

import DST2.Group2.DAO.AnnovarDAO;
import DST2.Group2.DAO.SampleDAO;
import DST2.Group2.DAO.VepDAO;
import DST2.Group2.bean.SampleBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Functionality_Tested
 * 1. search user, sample metadata information by ID; retrieve full sample data by ID.
 * 2. delete sample information according to the sample ID.
 * WARNING: may damage database if failed
 *
 * @Date 2020/5/12
 * @author DST group 2
 **/
public class SampleTransactTest extends BaseTest {

    final String MOCK_VEP_STRING = "## STRAND : Strand of the feature (1/-1)\n## FLAGS : Transcript quality flags\n#Uploaded_variation\tLocation\tAllele\tGene\tFeature\tFeature_type\tConsequence\tcDNA_position\tCDS_position\tProtein_position\tAmino_acids\tCodons\tExisting_variation\tExtra\n.\tChr1:14653\tT\tENSG00000223972\tENST00000450305\tTranscript\tdownstream_gene_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;DISTANCE=983;STRAND=1\n.\tChr1:14653\tT\tENSG00000223972\tENST00000456328\tTranscript\tdownstream_gene_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;DISTANCE=244;STRAND=1\n.\tChr1:14653\tT\tENSG00000227232\tENST00000488147\tTranscript\tintron_variant,non_coding_transcript_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;STRAND=-1";
    final String MOCK_ANNOVAR_STRING = "Chr\tStart\tEnd\tRef\tAlt\tFunc.refGene\tGene.refGene\tGeneDetail.refGene\tExonicFunc.refGene\tAAChange.refGene\tcytoBand\nchr1\t14574\t14574\tA\tG\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14599\t14599\tT\tA\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14604\t14604\tA\tG\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14610\t14610\tT\tC\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14653\t14653\tC\tT\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14677\t14677\tG\tA\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33";
    int testSampleId;

    @Autowired
    private SampleDAO sampleDAO;
    @Autowired
    private VepDAO vepDAO;
    @Autowired
    private AnnovarDAO annovarDAO;

    @Test
    public void sampleById() {
        // insert mock sample
        List<SampleBean> samples = sampleDAO.findAll();
        if (samples.size()==0){ testSampleId = 1; } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }
        vepDAO.save(testSampleId,MOCK_VEP_STRING);
        sampleDAO.save("Test_1_vep","vep"); // should automatically generate id = sample number already have + 1
        annovarDAO.save(testSampleId+1,MOCK_ANNOVAR_STRING);
        sampleDAO.save("Test_2_annovar","annovar");
        // sample data
        assertNotEquals(0,vepDAO.getsampleGenes(testSampleId).size());
        assertNotEquals(0,annovarDAO.getsampleGenes(testSampleId+1).size());
        // sample metadata
        SampleBean s = sampleDAO.findById(testSampleId);
        assertNotNull(s);
        assertEquals(testSampleId,s.getId());
        assertEquals("Test_1_vep",s.getUploadedBy());
        SampleBean s2 = sampleDAO.findById(testSampleId+1);
        assertNotNull(s);
        assertEquals("Test_2_annovar",s2.getUploadedBy());
        // clear up
        sampleDAO.deleteById(testSampleId);
        sampleDAO.deleteById(testSampleId+1);
    }

}
