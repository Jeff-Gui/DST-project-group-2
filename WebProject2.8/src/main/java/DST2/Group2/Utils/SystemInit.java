package DST2.Group2.Utils;

import DST2.Group2.DAO.*;
import DST2.Group2.crawler.DosingGuidelineCrawler;
import DST2.Group2.crawler.DrugLabelCrawler;

public class SystemInit {
    /**
     * Run ONLY once when the system is first deployed
     * Insert data, Muted next time
     * TODO: implement self-muting function (read and store init success information in the app properties file)
     * @Author: DST group 2
     */

    private ClinicAnnDAO clinicAnnDAO = new ClinicAnnDAO();
    private DrugLabelDAO drugLabelDAO = new DrugLabelDAO();
    private VarDrugAnnDAO varDrugAnnDAO = new VarDrugAnnDAO();
    private GeneDAO geneDAO = new GeneDAO();
    private DosingGuidelineDAO dosingGuidelineDAO = new DosingGuidelineDAO();
    private DrugLabelCrawler drugLabelCrawler = new DrugLabelCrawler();
    private DosingGuidelineCrawler dosingGuidelineCrawler = new DosingGuidelineCrawler();

    public void doInit() {
        // Schema, function created by admin
        // Insert data
        clinicAnnDAO.doImport(false);
        varDrugAnnDAO.doImportVarDrugAnn(false);
        varDrugAnnDAO.doImportVariant(false);
        geneDAO.doImport(false);
        drugLabelCrawler.doCrawlerDrug();
        drugLabelCrawler.doCrawlerDrugLabel();
        dosingGuidelineCrawler.doCrawlerDosingGuidelineList();
        drugLabelDAO.doImportDrugName(false);
        dosingGuidelineDAO.doImportGuidelineName(false);
        // Mute myself

    }
}
