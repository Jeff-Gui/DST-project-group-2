package controller;

import bean.VarDrugAnnBean;
import dao.VarDrugAnnDAO;
import dao.VepDAO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        VarDrugAnnDAO varDrugAnnDAO = new VarDrugAnnDAO();
        List<VarDrugAnnBean> VarDrugAnns = varDrugAnnDAO.getAnn();
        ArrayList<Object> rt = new ArrayList<>();
        List<VarDrugAnnBean> matchedAnns=new ArrayList<>();
        HashMap< String, HashMap<String, String>> matched_sampleInfo = new HashMap<>();
        VepDAO vepDAO = new VepDAO();
        ArrayList<ArrayList<String>> refGenes = vepDAO.getsampleGenes(0);
        System.out.println(VarDrugAnns.size());
        System.out.println(refGenes.size());
        for (VarDrugAnnBean ann:VarDrugAnns) {
            String annGene = ann.getGene();
            if (!(annGene == null)) {
                for (ArrayList<String> row : refGenes) {
                    String gene = row.get(3);
                    if (annGene.contains(gene)) {
                        if (!matchedAnns.contains(ann)) {
                            matchedAnns.add(ann);
                        }
                        updateSampleReturn(matched_sampleInfo, row, gene);
                    }
                }
            }
        }

        rt.add(matchedAnns);
        rt.add(matched_sampleInfo);
        System.out.println(matchedAnns.size());
        System.out.println(matched_sampleInfo.size());
    }

    private static void updateSampleReturn(HashMap<String, HashMap<String, String>> matched_sampleInfo, ArrayList<String> row, String gene) {
        // refactored by IDEA automatically
        if (matched_sampleInfo.containsKey(gene)){
            matched_sampleInfo.get(gene).put(row.get(0), row.get(1));
        } else {
            HashMap<String, String> submap = new HashMap<>();
            submap.put(row.get(0), row.get(1));
            matched_sampleInfo.put(gene,submap);
        }
    }

}
