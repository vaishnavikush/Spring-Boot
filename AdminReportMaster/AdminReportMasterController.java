package com.example.kaisi_lagi.AdminReportMaster;

//import ch.qos.logback.core.model.Model;
import com.example.kaisi_lagi.ReportMaster.ReportMaster;
import com.example.kaisi_lagi.ReportMaster.ReportRepository;
import com.example.kaisi_lagi.ReviewMaster.ReviewMaster;
import com.example.kaisi_lagi.ReviewMaster.ReviewRepository;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import com.example.kaisi_lagi.UserMaster.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @Autowired
    AdminEmailService emailService;

    @Autowired
    AdminMailDelete adminMailDelete;
    @Autowired
    AdminMailDeleteReporter adminMailDeleteReporter;
    @Autowired
    AdminEmailServiceReporter adminEmailServiceReporter;


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

    @GetMapping("/deletereport/{report_id}")
    public String delete(@PathVariable Long report_id){
        ReportMaster reportMaster=reportRepository.findById(report_id).orElse(null);
        if(reportMaster==null){
            return "redirect:/ShowReports";
        }
        UserMaster userMaster=reportMaster.getOwner();
        if (userMaster==null){
            return "redirect:/login";
        }
        UserMaster userMaster1=reportMaster.getReporter();
        if (userMaster1==null){
            return "redirect:/login";
        }
        adminMailDelete.DeleteMailBox(userMaster.getEmail());
        adminMailDeleteReporter.DeleteMailBoxReporter(userMaster1.getEmail());
        reportRepository.deleteById(report_id);
        return "redirect:/ShowReports";

    }
//
//    @GetMapping("/sendWarningMail/{ownerId}")
//    public String sendMail(@PathVariable("ownerId") Long id){
//
//        System.out.println("----------" + id);
//      UserMaster user =   userRepository.findById(id).orElse(null);
//      if(user==null){
//          return "redirect:/login";
//      }
//
//        emailService.sendWarningMail(user.getEmail());
//        adminEmailServiceReporter.sendWarningMailReporter(user.getEmail());
//
//
////        System.out.println("user email : " + user.getEmail());
////      String email = user.getEmail();
//               return "redirect:/ShowReports";
//    }
    @GetMapping("/sendWarningMail/{report_id}")
    public String sendMail(@PathVariable("report_id") Long report_id){
        ReportMaster reportMaster=reportRepository.findById(report_id).orElse(null);
        if(reportMaster==null){
            return "redirect:/ShowReports";
        }
        UserMaster userMaster=reportMaster.getOwner();
        if(userMaster==null){
            return "redirect:/login";
        }
        UserMaster userMaster1=reportMaster.getReporter();
        if(userMaster1==null){
            return "redirect:/login";
        }
        emailService.sendWarningMail(userMaster.getEmail());
        adminEmailServiceReporter.sendWarningMailReporter(userMaster1.getEmail());
        return "redirect:/ShowReports";

    }



}
