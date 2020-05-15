package web;

import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @Functionality_Tested
 * 1. retrieve knowledge information upon request, redirect to new page.
 * @Date 2020/5/12
 * @author DST group 2
 **/
public class KnowledgeBaseControllerTest extends BaseWebTest {

    @Test
    public void viewDrug() throws Exception {
        String url = "/drug";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url));
        MvcResult mvcResult = resultActions.
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void viewDosingGuideline() throws Exception {
        String url = "/dosingGuideline";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url));
        MvcResult mvcResult = resultActions.
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void viewClinicAnn() throws Exception {
        String url = "/clinicAnn";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url));
        MvcResult mvcResult = resultActions.
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void viewDrugLabel() throws Exception {
        String url = "/drugLabels";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url));
        MvcResult mvcResult = resultActions.
                andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }
}
