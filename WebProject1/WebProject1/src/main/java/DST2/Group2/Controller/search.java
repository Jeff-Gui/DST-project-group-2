//package DST2.Group2.Controller;
//
//import java.io.IOException;
//import java.util.List;
//
//import javax.servlet.ServletException;
//import javax.servlet.annotation.MultipartConfig;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import DST2.Group2.DAO.DrugLabelDAO;
//import DST2.Group2.DAO.VarDrugAnnDAO;
//import DST2.Group2.DAO.dosingGuidelineDAO;
//import DST2.Group2.bean.DosingGuideline;
//import DST2.Group2.bean.DrugLabel;
//import DST2.Group2.bean.VarDrugAnn;
//import DST2.Group2.servlet.DispatchServlet;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.ui.Model;
//import org.springframework.web.servlet.ModelAndView;
//
///**
// * Servlet implementation class search
// */
//
//@MultipartConfig
//public class search extends HttpServlet {
//
//	private static final Logger log = LoggerFactory.getLogger(upload.class);
//
//	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		// TODO Auto-generated method stub
//
//		ModelAndView mv=new ModelAndView();
//
//		System.out.println("searchDrug");
//    	String drug=request.getParameter("drug");
//    	String phen=request.getParameter("Phenotype");
//		System.out.println(drug);
//
//		System.out.println(request.getAttribute("matchedDrugLabel"));
//    	List<DrugLabel> filteredDrugLabel =null;
//    	List<DosingGuideline> filteredDosingGuideline =null;
//    	List<VarDrugAnn> filteredVarDrugAnn=null;
//    	List<DrugLabel> matchedDrugLabel=(List<DrugLabel>) request.getAttribute("matchedDrugLabel");
//    	List<DosingGuideline> matchedGuidelines =(List<DosingGuideline>) request.getAttribute("matchedGuideline");
//    	List<VarDrugAnn> matchedAnns=(List<VarDrugAnn>) request.getAttribute("matchedVarDrugAnn");
//		System.out.println(matchedDrugLabel);
//
//    	filteredDrugLabel=DrugLabelDAO.search(drug,phen,matchedDrugLabel);
//    	filteredDosingGuideline=dosingGuidelineDAO.search(drug,phen, matchedGuidelines);
//    	filteredVarDrugAnn=VarDrugAnnDAO.search(drug,phen,matchedAnns);
//    	//jsp
//		mv.addObject("filteredDrugLabel",filteredDrugLabel);
//    	mv.addObject("filteredDosingGuideline", filteredDosingGuideline);
//    	mv.addObject("filteredVarDrugAnn",filteredVarDrugAnn);
//        //request.getRequestDispatcher("/view/searchDrug.jsp").forward(request, response);
//		mv.setViewName("searchDrug");
//
//}}
