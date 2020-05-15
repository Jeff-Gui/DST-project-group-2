package basic;

import DST2.Group2.DAO.ClinicAnnDAO;
import DST2.Group2.DAO.DosingGuidelineDAO;
import DST2.Group2.DAO.DrugLabelDAO;
import DST2.Group2.DAO.VarDrugAnnDAO;
import DST2.Group2.bean.ClinicAnnBean;
import DST2.Group2.bean.DosingGuidelineBean;
import DST2.Group2.bean.DrugLabelBean;
import DST2.Group2.bean.VarDrugAnnBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * @Functionality_Tested
 * 1. given target drug name or phenotype and source beans, filter beans accordingly.
 *
 * @Date 2020/5/13
 * @author DST group 2
 **/
public class SearchTest extends BaseTest {
    @Autowired
    ClinicAnnDAO clinicAnnDAO;
    @Autowired
    DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    DrugLabelDAO drugLabelDAO;
    @Autowired
    VarDrugAnnDAO varDrugAnnDAO;

    @Test
    public void search(){
        // generate mock data
        List<ClinicAnnBean> clinicAnnBeans =  clinicAnnDAO.findAll().subList(0,100);
        List<DosingGuidelineBean> dosingGuidelineBeans = dosingGuidelineDAO.findAll().subList(0,100);
        List<DrugLabelBean> drugLabelBeans = drugLabelDAO.findAll().subList(0,100);
        List<VarDrugAnnBean> varDrugAnnBeans = varDrugAnnDAO.findAll().subList(0,100);

        assertNotNull(clinicAnnBeans);
        assertNotNull(dosingGuidelineBeans);
        assertNotNull(drugLabelBeans);
        assertNotNull(varDrugAnnBeans);

        //do search
        assertNotNull(drugLabelDAO.search("atomoxetine","",drugLabelBeans));
        assertNotNull( varDrugAnnDAO.search("atomoxetine","",varDrugAnnBeans));
        assertNotNull(dosingGuidelineDAO.search("atomoxetine","",dosingGuidelineBeans));
        assertNotNull(clinicAnnDAO.search("atomoxetine","",clinicAnnBeans));

    }
}
