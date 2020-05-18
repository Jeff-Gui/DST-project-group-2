package basic;

import DST2.Group2.DAO.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotNull;

/**
 * @Functionality_Tested
 * 1. Retrieve all information stored in knowledge base and sample tables via DAO.
 *
 * @Date 2020/5/12
 * @author DST group 2
 **/
public class BulkRetrieveDataTest extends BaseTest {
    @Autowired
    private ClinicAnnDAO clinicAnnDAO;
    @Autowired
    private DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    private DrugLabelDAO drugLabelDAO;
    @Autowired
    private DrugDAO drugDAO;
    @Autowired
    private VarDrugAnnDAO varDrugAnnDAO;
    @Autowired
    private SampleDAO sampleDAO;

    @Test
    public void retrieveClinicAnn(){
        assertNotNull(clinicAnnDAO.findAll());
        assertNotNull(dosingGuidelineDAO.findAll());
        assertNotNull(drugLabelDAO.findAll());
        assertNotNull(drugDAO.findAll());
        assertNotNull(varDrugAnnDAO.findAll());
        assertNotNull(sampleDAO.findAll());
    }
}