package controller;

import bean.VarDrugAnnBean;
import dao.VarDrugAnnDAO;
import dao.VepDAO;
import utils.ListMatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("");
        System.out.println(ListMatch.listMatch("asdf,safd,2rw",list));
    }
}
