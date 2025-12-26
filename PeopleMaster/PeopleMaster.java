package com.example.kaisi_lagi.PeopleMaster;

import com.example.kaisi_lagi.MovieCast.MovieCast;
import com.example.kaisi_lagi.ReviewMaster.ReviewMaster;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "people_master")
public class PeopleMaster {
    public Long getPid() {
        return pid;
    }

    public PeopleMaster() {
    }

    public String getPeople_awards() {
        return people_awards;
    }

    public void setPeople_awards(String people_awards) {
        this.people_awards = people_awards;
    }

    public LocalDate getPeople_dob() {
        return people_dob;
    }

    public void setPeople_dob(LocalDate people_dob) {
        this.people_dob = people_dob;
    }

    public String getPeople_debut() {
        return people_debut;
    }

    public void setPeople_debut(String people_debut) {
        this.people_debut = people_debut;
    }

    public String getPeople_bio() {
        return people_bio;
    }

    public void setPeople_bio(String people_bio) {
        this.people_bio = people_bio;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getPeopleName() {
        return peopleName;
    }

    public void setPeopleName(String peopleName) {
        this.peopleName = peopleName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public List<MovieCast> getMovieCasts() {
        return movieCasts;
    }

    public void setMovieCasts(List<MovieCast> movieCasts) {
        this.movieCasts = movieCasts;
    }

    public int getDebut_date() {
        return debut_date;
    }

    public void setDebut_date(int debut_date) {
        this.debut_date = debut_date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "people_id")
    private Long pid;
    @Column(name = "people_name",nullable = false)
    private String peopleName;
    @Column(name = "role")
    private String role;
    private int points;
    private byte[] image;
    private String people_bio;
    private String people_awards;
    private LocalDate people_dob;
    private String people_debut;
    private int debut_date;

    @OneToMany(mappedBy = "people",cascade = CascadeType.ALL)
    private List<MovieCast> movieCasts;

//    @OneToMany(mappedBy = "peopleMaster", cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<ReviewMaster> reviews;
@OneToMany(mappedBy = "peopleMaster", cascade = CascadeType.REMOVE, orphanRemoval = true)
private List<ReviewMaster> reviews;


}
