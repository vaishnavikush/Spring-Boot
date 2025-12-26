package com.example.kaisi_lagi.ReportMaster;

import com.example.kaisi_lagi.ReviewMaster.ReviewMaster;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import jakarta.persistence.*;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import java.time.LocalDate;

@Entity
@Table(name = "report_master")
public class ReportMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int report_id;
    private String reason;
    private LocalDate report_date;

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private UserMaster user_id;
    @ManyToOne
//    @JoinColumn(name = "owner")
    private UserMaster owner;

    @ManyToOne
//    @JoinColumn(name = "reporter")
    private UserMaster Reporter;

    @ManyToOne
    @JoinColumn(name = "review_id")
    private ReviewMaster review_id;

    public ReviewMaster getReview_id() {
        return review_id;
    }

    public void setReview_id(ReviewMaster review_id) {
        this.review_id = review_id;
    }

//    public UserMaster getUser_id() {
//        return user_id;
//    }
//
//    public void setUser_id(UserMaster user_id) {
//        this.user_id = user_id;
//    }

    public LocalDate getReport_date() {
        return report_date;
    }

    public void setReport_date(LocalDate report_date) {
        this.report_date = report_date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getReport_id() {
        return report_id;
    }

    public void setReport_id(int report_id) {
        this.report_id = report_id;
    }

    public ReportMaster(String reason, int report_id, LocalDate report_date, UserMaster user_id, ReviewMaster review_id) {
        this.reason = reason;
        this.report_id = report_id;
        this.report_date = report_date;
//        this.user_id = user_id;
        this.review_id = review_id;
    }

    public UserMaster getOwner() {
        return owner;
    }

    public void setOwner(UserMaster owner) {
        this.owner = owner;
    }

    public UserMaster getReporter() {
        return Reporter;
    }

    public void setReporter(UserMaster reporter) {
        Reporter = reporter;
    }

    public ReportMaster() {
    }
}
