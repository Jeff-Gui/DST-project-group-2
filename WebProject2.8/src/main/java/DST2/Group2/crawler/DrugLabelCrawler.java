package DST2.Group2.crawler;

import DST2.Group2.DAO.DrugDAO;
import DST2.Group2.DAO.DrugLabelDAO;
import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.DrugBean;
import DST2.Group2.bean.DrugLabelBean;
import com.google.gson.Gson;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @Description This is the description of class
 * Crawler for obtaining both drug and drug label information from PGKB web page.
 * @Date 2020/5/15
 * @Author DST group 2
 */
public class DrugLabelCrawler extends BaseCrawler {

    public static final String URL_DRUG_LABEL = "https://api.pharmgkb.org/v1/site/labelsByDrug";
    public static final String URL_DRUG_LABEL_DETAIL = "https://api.pharmgkb.org/v1/site/page/drugLabels/%s?view=base";

    private DrugDAO drugDAO = new DrugDAO();
    private DrugLabelDAO drugLabelDAO = new DrugLabelDAO();

    public void doCrawlerDrug() {
        String content = this.getURLContent(URL_DRUG_LABEL);
        Gson gson = new Gson();
        Map drugLabels = gson.fromJson(content, Map.class);
        List<Map> data = (List<Map>) drugLabels.get("data");
        data.stream().forEach(x -> {
            Map drug = ((Map) x.get("drug"));
            String id = (String) drug.get("id");
            String name = (String) drug.get("name");
            String objCls = (String) drug.get("objCls");
            String drugUrl = (String) x.get("drugUrl");
            boolean biomarker = ((Boolean) x.get("biomarker"));
            if (!drugDAO.existsById(id)){
                DrugBean drugBean = new DrugBean(id, name, biomarker, drugUrl, objCls);
                drugDAO.saveDrug(drugBean);
            }
        });
    }

    public void doCrawlerDrugLabel() {
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from drug");
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String content = this.getURLContent(String.format(URL_DRUG_LABEL_DETAIL, id));
                    Gson gson = new Gson();
                    Map result = gson.fromJson(content, Map.class);
                    Map data = (Map) result.get("data");
                    List<Map> drugLabels = (List<Map>) data.get("drugLabels");
                    drugLabels.stream().forEach(x -> {
                        String labelId = (String) x.get("id");
                        String name = (String) x.get("id");
                        String objCls = (String) x.get("objCls");
                        boolean alternateDrugAvailable = (Boolean) x.get("alternateDrugAvailable");
                        boolean dosingInformation = (Boolean) x.get("dosingInformation");
                        String prescribingMarkdown = "";
                        if (x.containsKey("prescribingMarkdown")) {
                            prescribingMarkdown = ((String) ((Map) x.get("prescribingMarkdown")).get("html"));
                        }
                        String source = (String) x.get("source");
                        String textMarkdown = ((String) ((Map) x.get("textMarkdown")).get("html"));
                        String summaryMarkdown = ((String) ((Map) x.get("summaryMarkdown")).get("html"));
                        String raw = gson.toJson(x);
                        String drugId = ((String) ((List<Map>) x.get("relatedChemicals")).get(0).get("id"));
                        DrugLabelBean drugLabelBean = new DrugLabelBean(labelId, name, objCls, alternateDrugAvailable, dosingInformation
                                , prescribingMarkdown, source, textMarkdown, summaryMarkdown, raw, drugId);
                        if (!drugLabelDAO.existsById(labelId)) {
                            drugLabelDAO.saveDrugLabel(drugLabelBean);
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

}
