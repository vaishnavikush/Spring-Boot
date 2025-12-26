package com.example.kaisi_lagi.AdminReportMaster;

//import ch.qos.logback.core.model.Model;
import com.example.kaisi_lagi.ReportMaster.ReportMaster;
import com.example.kaisi_lagi.ReportMaster.ReportRepository;
import jakarta.persistence.GeneratedValue;
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

//    @GetMapping("/ShowReports")
//    public String ShowReports(Model model ){
//        Iterable<AdminReportMaster>reports=adminReportMasterRepository.findAll();
//        List<AdminReportMaster>listreports=new ArrayList<>();
//        reports.forEach(listreports::add);
//        model.addAttribute("showAllReports", listreports);
//        return "AdminReportMasterForm";
//
//    }

    @GetMapping("/ShowReports")
    public String ShowReports(Model model ){
        Iterable<ReportMaster>reports=reportRepository.findAll();

        List<ReportMaster>listreports=new ArrayList<>();
        reports.forEach(listreports::add);
        model.addAttribute("showAllReports", listreports);
        return "AdminReportMasterForm";

    }

    @GetMapping("/delete/{admin_id}")
    public String deleteReport(@PathVariable long admin_id){
        adminReportMasterRepository.deleteById(admin_id);
        return "redirect:/ShowReports";
    }
}
