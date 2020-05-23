package basic;

import DST2.Group2.DAO.*;
import DST2.Group2.Utils.SystemInit;
import DST2.Group2.crawler.DosingGuidelineCrawler;
import DST2.Group2.crawler.DrugLabelCrawler;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNotEquals;

/**
 * @Functionality_Tested
 * 1. Update knowledge base data via DAO while maintaining database structure.
 * 2. Retrieve metadata of the knowledge base after update.
 * WARNING: May damage database structure if failed.
 * WARNING: Do not run all three tests at the same time. initSystem() should only be called once for each system deployment.
 *
 * @Todo: file not exist? web failure?
 * @Date 2020/5/12
 * @author DST group 2
 **/
public class UpdateDataTest extends BaseTest {

    @Autowired
    private DosingGuidelineDAO dosingGuidelineDAO;
    @Autowired
    private DrugLabelDAO drugLabelDAO;
    @Autowired
    private ClinicAnnDAO clinicAnnDAO;
    @Autowired
    private GeneDAO geneDAO;
    @Autowired
    private VarDrugAnnDAO varDrugAnnDAO;

    @Test
    public void crawlerUpdate() {
        // 1. crawl down the data
        DosingGuidelineCrawler dosingGuidelineCrawler = new DosingGuidelineCrawler();
        DrugLabelCrawler drugLabelCrawler = new DrugLabelCrawler();
        dosingGuidelineCrawler.doCrawlerDosingGuidelineList();
        drugLabelCrawler.doCrawlerDrugLabel();
        drugLabelCrawler.doCrawlerDrug();
        // 2. generic tables after deletion
        dosingGuidelineDAO.doImportGuidelineName(true);
        drugLabelDAO.doImportDrugName(true);
    }

    @Test
    public void tableUpdate(){
        // 1. read tables in the source repository and update
        assertNotEquals(0,clinicAnnDAO.doImport(true));
        assertNotEquals(0,geneDAO.doImport(true));
        assertNotEquals(0,varDrugAnnDAO.doImportVarDrugAnn(true));
        // 2. generic tables after deletion
        varDrugAnnDAO.doImportVariant(true);
    }

    @Test
    public void initSystem() {
        // Should ONLY RUN ONCE for each system deployment.
        // test passed 6 minutes 14 seconds (Jeff, 12-05-2020)
        SystemInit systemInit = new SystemInit();
        systemInit.doInit();
    }

}