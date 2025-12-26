package com.example.kaisi_lagi.ReviewMaster;

import com.example.kaisi_lagi.MovieMaster.MovieMaster;
import com.example.kaisi_lagi.PeopleMaster.PeopleMaster;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_master")
public class ReviewMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    private int rating;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    @ManyToOne
    @JoinColumn(name = "movie_id")
    private MovieMaster movie;

    @ManyToOne
    @JoinColumn(name = "people_id")
    private PeopleMaster peopleMaster;   // <-- KEEP THIS ONE ONLY

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserMaster user;

    // GETTERS & SETTERS
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }

    public MovieMaster getMovie() { return movie; }
    public void setMovie(MovieMaster movie) { this.movie = movie; }

    public UserMaster getUser() { return user; }
    public void setUser(UserMaster user) { this.user = user; }

    public PeopleMaster getPeopleMaster() { return peopleMaster; }
    public void setPeopleMaster(PeopleMaster peopleMaster) { this.peopleMaster = peopleMaster; }
}
