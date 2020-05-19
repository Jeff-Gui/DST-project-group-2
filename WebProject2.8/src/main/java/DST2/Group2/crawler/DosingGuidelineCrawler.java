package DST2.Group2.crawler;


import DST2.Group2.DAO.DosingGuidelineDAO;
import DST2.Group2.bean.DosingGuidelineBean;
import com.google.gson.Gson;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * @Description This is the description of class
 * Crawler to obtain dosing guideline form PharmGKB web page.
 * @Date 2020/5/14
 * @Author DST group 2
 */
public class DosingGuidelineCrawler extends BaseCrawler {


    public static final String URL_BASE = "https://api.pharmgkb.org/v1/data%s";
    public static final String URL_GUIDELINES = "https://api.pharmgkb.org/v1/site/guidelinesByDrugs";

    private DosingGuidelineDAO dosingGuidelineDAO = new DosingGuidelineDAO();

    public int doCrawlerDosingGuidelineList() {
        AtomicInteger counter = new AtomicInteger();
        String content = this.getURLContent(URL_GUIDELINES);
        Gson gson = new Gson();
        Map drugLabels = gson.fromJson(content, Map.class);
        List<Map> data = (List<Map>) drugLabels.get("data");
        data.stream().forEach(x -> {
            List.of("cpic", "cpnds", "dpwg", "fda", "pro").forEach(source -> {
                List<Map> guidelineList = (List<Map>) x.get(source);
                guidelineList.forEach(guideline -> {
                    String url = (String) guideline.get("url");
                    counter.set(counter.get() + doCrawlerDosingGuideline(url));
                });
            });
        });
        return counter.get();
    }

    public int doCrawlerDosingGuideline(String url) {
        String content = this.getURLContent(String.format(URL_BASE, url));
        Gson gson = new Gson();
        Map guideline = gson.fromJson(content, Map.class);
        Map data = ((Map) guideline.get("data"));
        String id = (String) data.get("id");
        String objCls = (String) data.get("objCls");
        String name = (String) data.get("name");
        boolean recommendation = (Boolean) data.get("recommendation");
        String drugId = ((String) ((List<Map>) data.get("relatedChemicals")).get(0).get("id"));
        String source = (String) data.get("source");
        String summaryMarkdown = ((String) ((Map) data.get("summaryMarkdown")).get("html"));
        String textMarkdown = ((String) ((Map) data.get("textMarkdown")).get("html"));
        String raw = gson.toJson(guideline);
        DosingGuidelineBean dosingGuideline = new DosingGuidelineBean(id, objCls, name, recommendation, drugId, source, summaryMarkdown, textMarkdown, raw);
        if (!dosingGuidelineDAO.existsById(id)) {
            dosingGuidelineDAO.saveDosingGuideline(dosingGuideline);
            return 1;
        } else { return 0; }
    }

}
