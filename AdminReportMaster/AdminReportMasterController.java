package com.example.kaisi_lagi.AdminReportMaster;

//import ch.qos.logback.core.model.Model;
import com.example.kaisi_lagi.ReportMaster.ReportMaster;
import com.example.kaisi_lagi.ReportMaster.ReportRepository;
import com.example.kaisi_lagi.ReviewMaster.ReviewMaster;
import com.example.kaisi_lagi.ReviewMaster.ReviewRepository;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import com.example.kaisi_lagi.UserMaster.UserRepository;
import jakarta.persistence.GeneratedValue;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminReportMasterController {
    @Autowired
    AdminReportMasterRepository adminReportMasterRepository;
    @Autowired
    ReportRepository reportRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;


    @GetMapping("/ShowReports")
    public String ShowReports(Model model){
        Long reportMaster=reportRepository.count();
        Long reviewMaster=reviewRepository.count();
        Long userMaster=userRepository.count();
        List<ReportMaster>reportList=reportRepository.findAll();
        List<ReviewMaster>reviewList=reviewRepository.findAll();
        List<UserMaster>userList=userRepository.findAll();
        model.addAttribute("reportMaster",reportMaster);
        model.addAttribute("reviewMaster",reviewMaster);
        model.addAttribute("userMaster",userMaster);
        model.addAttribute("reportList",reportList);
        model.addAttribute("reviewList",reviewList);
        model.addAttribute("userList",userList);
        return "AdminReportMasterForm";
    }
}
