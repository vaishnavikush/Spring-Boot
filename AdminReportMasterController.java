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

//    @GetMapping("/deletereport/{report_id}")
//    public String delete(@PathVariable Long report_id) {
//        ReportMaster reportMaster = reportRepository.findById(report_id).orElse(null);
//        ReviewMaster reviewMaster = reviewRepository.findById(report_id).orElse(null);
//        if (reportMaster == null) {
//            return "redirect:/ShowReports";
//        }
//        UserMaster userMaster = reportMaster.getOwner();
//        if (userMaster == null) {
//            return "redirect:/login";
//        }
//        UserMaster userMaster1 = reportMaster.getReporter();
//        if (userMaster1 == null) {
//            return "redirect:/login";
//        }
//        adminMailDelete.DeleteMailBox(userMaster.getEmail());
//        adminMailDeleteReporter.DeleteMailBoxReporter(userMaster1.getEmail());
//        adminReportMasterRepository.deleteByReportMaster(reportMaster);
//        reportRepository.delete(reportMaster);
//        if (reportMaster != null) {
//            reviewRepository.delete(reviewMaster);
//        }
//        return "redirect:/ShowReports";
//    }


    @GetMapping("/deletereport/{report_id}")
    public String delete(@PathVariable Long report_id) {

        ReportMaster reportMaster = reportRepository.findById(report_id).orElse(null);
        if (reportMaster == null) {
            return "redirect:/ShowReports";
        }

        UserMaster owner = reportMaster.getOwner();
        UserMaster reporter = reportMaster.getReporter();
        if (owner == null || reporter == null) {
            return "redirect:/login";
        }

        adminMailDelete.DeleteMailBox(owner.getEmail());
        adminMailDeleteReporter.DeleteMailBoxReporter(reportMaster.getReporter().getEmail());

        adminReportMasterRepository.deleteByReportMaster(reportMaster);

//        ReviewMaster reviewMaster = reviewRepository.deleteById(reportMaster.getReview().getReviewId());
        reviewRepository.deleteById(reportMaster.getReview().getReviewId());
//        if (reviewMaster != null) {
//            reviewRepository.delete(reviewMaster);
//        }

        reportRepository.delete(reportMaster);

        return "redirect:/ShowReports";
    }


//theekkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
//    @GetMapping("/deletereview/{reviewId}")
//    public String deleteReview(@PathVariable Long reviewId) {
//        ReportMaster reportMaster=reportRepository.findById(reviewId).orElse(null);
//        ReviewMaster review = reviewRepository.findById(reviewId).orElse(null);
//        if (review == null) {
//            return "redirect:/ShowReports";
//        }
//
//        reviewRepository.delete(review);
//
//        UserMaster userMaster=reportMaster.getOwner();
//        if (userMaster==null){
//            return "redirect:/login";
//        }
//        UserMaster userMaster1=reportMaster.getReporter();
//        if (userMaster1==null){
//            return "redirect:/login";
//        }
//        adminMailDelete.DeleteMailBox(userMaster.getEmail());
//        adminMailDeleteReporter.DeleteMailBoxReporter(userMaster1.getEmail());
//
//        return "redirect:/ShowReports";
//    }
//thekkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk
//
//    @GetMapping("/deletereview/{reviewId}")
//    public String deleteReview(@PathVariable Long reviewId) {
//        ReviewMaster review = reviewRepository.findById(reviewId).orElse(null);
//        if (review == null) {
//            return "redirect:/ShowReports";
//        }
//        ReportMaster reportMaster=reportRepository.
//        reviewRepository.delete(review);
//
//        UserMaster userMaster=reportMaster.getOwner();
//        if (userMaster==null){
//            return "redirect:/login";
//        }
//        UserMaster userMaster1=reportMaster.getReporter();
//        if (userMaster1==null){
//            return "redirect:/login";
//        }
//        adminMailDelete.DeleteMailBox(userMaster.getEmail());
//        adminMailDeleteReporter.DeleteMailBoxReporter(userMaster1.getEmail());
//
//        return "redirect:/ShowReports";
//    }

//    @GetMapping("/deletereview/{reviewId}")
//    public String deleteReview(@PathVariable Long reviewId) {
//
//        ReviewMaster review = reviewRepository.findById(reviewId).orElse(null);
//        if (review == null) {
//            return "redirect:/ShowReports";
//        }
//
//        List<AdminReportMaster> reports =
//                adminReportMasterRepository.findByReviewMaster(review);
//
//        for (AdminReportMaster adminReport : reports) {
//
//            ReportMaster report = adminReport.getReportMaster();
//            if (report == null) continue;
//
//            UserMaster owner = report.getOwner();
//            UserMaster reporter = report.getReporter();
//
//            if (owner != null) {
//                adminMailDelete.DeleteMailBox(owner.getEmail());
//            }
//
//            if (reporter != null) {
//                adminMailDeleteReporter
//                        .DeleteMailBoxReporter(reporter.getEmail());
//            }
//        }

//        // delete admin reports linked to this review
//        adminReportMasterRepository.deleteByReviewMaster(review);
//
//        // delete review
//        reviewRepository.delete(review);
//
//        return "redirect:/ShowReports";
//    }


//    @GetMapping("/deletereview/{reviewId}")
//    public String deleteReview(@PathVariable Long reviewId) {
//
//        ReviewMaster review = reviewRepository.findById(reviewId).orElse(null);
//        if (review == null) {
//            return "redirect:/ShowReports";
//        }
//
//        // 1️⃣ Get all reports for this review
//        List<AdminReportMaster> reports =
//                adminReportMasterRepository.findByReview(review);
//
//        // 2️⃣ Send emails
//        for (AdminReportMaster report : reports) {
//            ReportMaster report = adminReport.getReportMaster();
//            if (report == null) continue;
//
//            UserMaster owner = report.getOwner();
//            UserMaster reporter = report.getReporter();
//
//            if (owner != null) {
//                adminMailDelete.DeleteMailBox(owner.getEmail());
//            }
//
//            if (reporter != null) {
//                adminMailDeleteReporter.DeleteMailBoxReporter(reporter.getEmail());
//            }
//        }
//
//        // 3️⃣ Delete all reports linked to review
//        adminReportMasterRepository.deleteByReview(review);
//
//        // 4️⃣ Delete review
//        reviewRepository.delete(review);
//
//        return "redirect:/ShowReports";
//    }


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
            ReportMaster reportMaster = reportRepository.findById(report_id).orElse(null);
            if (reportMaster == null) {
                return "redirect:/ShowReports";
            }
            UserMaster userMaster = reportMaster.getOwner();
            if (userMaster == null) {
                return "redirect:/login";
            }
            UserMaster userMaster1 = reportMaster.getReporter();
            if (userMaster1 == null) {
                return "redirect:/login";
            }
            emailService.sendWarningMail(userMaster.getEmail());
            adminEmailServiceReporter.sendWarningMailReporter(userMaster1.getEmail());
            return "redirect:/ShowReports";

        }
    }

