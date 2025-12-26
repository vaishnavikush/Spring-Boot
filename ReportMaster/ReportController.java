package com.example.kaisi_lagi.ReportMaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
public class ReportController {
    @Autowired
    ReportRepository reportRepository;

    @GetMapping("/setreport")
    public String ReportSet(){
        return "ReportMasterPackage/SetReportForm";
    }
//    @PostMapping("/reportset")
//    public String SetReport(@RequestParam String reason){
//        ReportMaster reportMaster=new ReportMaster();
//        reportMaster.setReason(reason);
//        reportMaster.setReport_date(LocalDate.now());
//        reportRepository.save(reportMaster);
//     return "DATASAVEFORM";
//    }
@PostMapping("/reportset")
@ResponseBody
public ResponseEntity<String> SetReport(@RequestParam String reason){
    ReportMaster reportMaster = new ReportMaster();
    reportMaster.setReason(reason);
    reportMaster.setReport_date(LocalDate.now());
    reportRepository.save(reportMaster);
    return ResponseEntity.ok("Success");
}

}
