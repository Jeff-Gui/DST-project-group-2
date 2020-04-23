package DST2.Group2.Controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DST2.Group2.DAO.DrugLabelDAO;
import DST2.Group2.DAO.VarDrugAnnDAO;
import DST2.Group2.DAO.dosingGuidelineDAO;
import DST2.Group2.bean.DosingGuideline;
import DST2.Group2.bean.DrugLabel;
import DST2.Group2.bean.VarDrugAnn;
import DST2.Group2.servlet.DispatchServlet;

/**
 * Servlet implementation class search
 */
@WebServlet("/searchDrug")
@MultipartConfig
public class search extends HttpServlet {
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("searchDrug");
    	String drug=request.getParameter("drug");
    	
		System.out.println(drug);
		
		System.out.println(request.getAttribute("matchedDrugLabel"));
    	List<DrugLabel> filteredDrugLabel =null;
    	List<DosingGuideline> filteredDosingGuideline =null;
    	List<VarDrugAnn> filteredVarDrugAnn=null;
    	List<DrugLabel> matchedDrugLabel=(List<DrugLabel>) request.getAttribute("matchedDrugLabel");
    	List<DosingGuideline> matchedGuidelines =(List<DosingGuideline>) request.getAttribute("matchedGuideline");
    	List<VarDrugAnn> matchedAnns=(List<VarDrugAnn>) request.getAttribute("matchedVarDrugAnn");
		System.out.println(matchedDrugLabel);

    	filteredDrugLabel=DrugLabelDAO.searchByDrug(drug, matchedDrugLabel);
    	filteredDosingGuideline=dosingGuidelineDAO.searchByDrug(drug, matchedGuidelines);
    	filteredVarDrugAnn=VarDrugAnnDAO.searchByDrug(drug,matchedAnns);
    	//jsp
    	request.setAttribute("filteredDrugLabel",filteredDrugLabel);
    	request.setAttribute("filteredDosingGuideline", filteredDosingGuideline);
    	request.setAttribute("filteredVarDrugAnn",filteredVarDrugAnn);
        request.getRequestDispatcher("/pages/searchDrug.jsp").forward(request, response);

    	

}}
