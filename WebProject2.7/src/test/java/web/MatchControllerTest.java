package web;

import DST2.Group2.DAO.*;
import DST2.Group2.bean.SampleBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;


/**
 * @Functionality_Tested
 * 1. match mock sample (both annovar and vep) with knowledge base info with spike-in control.
 * TODO performance testing, web search
 * @Date 2020/5/13
 * @author DST group 2
 **/
public class MatchControllerTest extends BaseWebTest {

    @Autowired
    SampleDAO sampleDAO;
    @Autowired
    AnnovarDAO annovarDAO;
    @Autowired
    VepDAO vepDAO;
    @Autowired
    ClinicAnnDAO clinicAnnDAO;
    @Autowired
    DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    DrugLabelDAO drugLabelDAO;
    @Autowired
    VarDrugAnnDAO varDrugAnnDAO;


    final String MOCK_VEP_STRING = "## STRAND : Strand of the feature (1/-1)\n## FLAGS : Transcript quality flags\n#Uploaded_variation\tLocation\tAllele\tGene\tFeature\tFeature_type\tConsequence\tcDNA_position\tCDS_position\tProtein_position\tAmino_acids\tCodons\tExisting_variation\tExtra\n.\tChr1:14653\tT\tENSG00000223972\tENST00000450305\tTranscript\tdownstream_gene_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;DISTANCE=983;STRAND=1\n.\tChr1:14653\tT\tENSG00000223972\tENST00000456328\tTranscript\tdownstream_gene_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;DISTANCE=244;STRAND=1\n.\tChr1:14653\tT\tENSG00000227232\tENST00000488147\tTranscript\tintron_variant,non_coding_transcript_variant\t-\t-\t-\t-\t-\t-\tIMPACT=MODIFIER;STRAND=-1";
    final String MOCK_ANNOVAR_STRING = "Chr\tStart\tEnd\tRef\tAlt\tFunc.refGene\tGene.refGene\tGeneDetail.refGene\tExonicFunc.refGene\tAAChange.refGene\tcytoBand\nchr1\t14574\t14574\tA\tG\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14599\t14599\tT\tA\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14604\t14604\tA\tG\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14610\t14610\tT\tC\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14653\t14653\tC\tT\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33\nchr1\t14677\t14677\tG\tA\tncRNA_exonic\tWASH7P\t.\t.\t.\t1p36.33";

    @Test
    public void annovarMatchTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }
        sampleDAO.save("Test","annovar");
        annovarDAO.save(testSampleId,MOCK_ANNOVAR_STRING);

        //do match
        String url = "/matching";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url).param("sampleId",testSampleId+"").param("sampleType","annovar")).
                andExpect(MockMvcResultMatchers.view().name("matching_index_search")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andReturn();

        //clear
        sampleDAO.deleteById(testSampleId);
    }

    @Test
    public void vepMatchTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }
        sampleDAO.save("Test","vep");
        vepDAO.save(testSampleId,MOCK_VEP_STRING);

        //do match
        String url = "/matching";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url).param("sampleId",testSampleId+"").param("sampleType","vep")).
                andExpect(MockMvcResultMatchers.view().name("matching_index_search")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).
                andReturn();

        //clear
        sampleDAO.deleteById(testSampleId);
    }

//    @Test
//    public void searchTest(){
//        // TODO unable to test search function in Spring container, since model and view is member field in controller
//        // generate mock model and view
//        MatchController matchController = new MatchController();
//        ModelAndView w = new ModelAndView();
//        List<ClinicAnnBean> clinicAnnBeans =  clinicAnnDAO.findAll().subList(0,100);
//        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.findAll().subList(0,100);
//        List<DrugLabelBean> drugLabelBeans = drugLabelDAO.findAll().subList(0,100);
//        List<VarDrugAnnBean> varDrugAnnBeans = varDrugAnnDAO.findAll().subList(0,100);
//
//        assertNotNull(clinicAnnBeans);
//        assertNotNull(dosingGuidelineBeans);
//        assertNotNull(drugLabelBeans);
//        assertNotNull(varDrugAnnBeans);
//        // insert mock data
//        w.addObject("matchedDrugLabel",drugLabelBeans);
//        w.addObject("matchedDrugLabel",drugLabelBeans);
//        w.addObject("matchedDosingGuideline",dosingGuidelineBeans);
//        w.addObject("matchedVarDrugAnn",varDrugAnnBeans);
//        w.addObject("matched_clinic_ann_by_gene",clinicAnnBeans.subList(0,50));
//        w.addObject("matched_clinic_ann_by_snp",clinicAnnBeans.subList(51,100));
//        w.addObject("sample",new SampleBean(1,null,"Test","annovar"));
//        matchController.w = w;
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        request.addParameter("drug","atomoxetine");
//        request.addParameter("phenotype","");
//
//        ModelAndView result = matchController.search(request);
//        assertNotNull(result);
//    }
}
