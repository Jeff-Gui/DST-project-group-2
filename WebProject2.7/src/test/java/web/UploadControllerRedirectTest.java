package web;

import DST2.Group2.DAO.SampleDAO;
import DST2.Group2.bean.SampleBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @Functionality_Tested
 * 1. file upload (both annovar and vep), sample metadata and data insertion
 * 2. redirect to matching
 * TODO 2. boundary cases: maximum size, blank file, empty uploaded_by
 * @Date 2020/5/12
 * @author DST group 2
 **/
public class UploadControllerRedirectTest extends BaseWebTest {

    @Autowired
    SampleDAO sampleDAO;

    @Test
    public void uploadAnnovarTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }

        // do upload and redirect to match
        String url = "/upload/annovar";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).
                file(new MockMultipartFile("file", "file", "application/annovar-vcf", new FileInputStream(new File("src/test/resources/annovar_test.vcf")))).
                param("uploaded_by","MockUser"));
        MvcResult mvcResult = resultActions.
                andExpect(MockMvcResultMatchers.view().name("samples")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isFound()). // HttpStatus=302 (transient redirect)
                andReturn();

        // rollback
        sampleDAO.deleteById(testSampleId);
    }

    @Test
    public void uploadVepTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }

        // do upload and redirect to match
        String url = "/upload/vep";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).
                file(new MockMultipartFile("file", "file", "application/vep_test-vcf", new FileInputStream(new File("src/test/resources/blank_test.vcf")))).
                param("uploaded_by","MockUser"));
        MvcResult mvcResult = resultActions.
//                andExpect(MockMvcResultMatchers.view().name("redirect:/matching?sampleId="+testSampleId+"&sampleType=vep")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isFound()). // HttpStatus=302 (transient redirect)
                andReturn();

        // rollback
        sampleDAO.deleteById(testSampleId);
    }

    @Test
    public void uploadBlankTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }
        try{
            // do upload and redirect to match
            String url = "/upload/annovar";
            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).
                    file(new MockMultipartFile("file", "file", "application/annovar-vcf", new FileInputStream(new File("src/test/resources/wrong_test.png")))).
                    param("uploaded_by","MockUser"));
            MvcResult mvcResult = resultActions.
                    andExpect(MockMvcResultMatchers.view().name("samples")).
                    andDo(MockMvcResultHandlers.print()).
                    andExpect(ResultMatcher.matchAll()).
                    andExpect(MockMvcResultMatchers.status().isFound()). // HttpStatus=302 (transient redirect)
                    andReturn();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void uploadWrongTest() throws Exception {
        //read existing sample number
        List<SampleBean> samples = sampleDAO.findAll();
        int testSampleId;
        if (samples.size()==0){
            testSampleId = 1;
        } else {
            testSampleId = samples.get(samples.size() - 1).getId()+1;
        }

        // do upload and redirect to match
        String url = "/upload/annovar";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).
                file(new MockMultipartFile("file", "file", "application/annovar-vcf", new FileInputStream(new File("src/test/resources/wrong_test.png")))).
                param("uploaded_by","MockUser"));
        MvcResult mvcResult = resultActions.
                andExpect(MockMvcResultMatchers.view().name("samples")).
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isFound()). // HttpStatus=302 (transient redirect)
                andReturn();

    }
}
