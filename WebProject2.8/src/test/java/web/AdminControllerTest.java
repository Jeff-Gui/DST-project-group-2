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
 * 1. update data if submitted
 * 2. display metadata of the database
 * 3. delete sample and/or update user account
 *
 * @Date 2020/5/13
 * @author DST group 2
 **/
public class AdminControllerTest extends BaseWebTest {
    // TODO test data display panel
    // TODO test user & sample manipulation panel

    @Test
    public void viewClinicAnn() throws Exception {
        String url = "/admin/import";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url));
        MvcResult mvcResult = resultActions.
                andDo(MockMvcResultHandlers.print()).
                andExpect(MockMvcResultMatchers.view().name("/admin/panel")).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    } // Took 4 minutes Jeff 13-05-2020
}
