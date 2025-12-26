package com.example.kaisi_lagi.ReviewMaster;

import com.example.kaisi_lagi.ImageMaster.ImageMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//public interface ReviewRepository extends JpaRepository<ReviewMaster, Long> {
////    List<ReviewMaster> findByPeopleMasterPeopleId(Long pid);
////List<ReviewMaster> findByPeople_PeopleId(Long pid);
//List<ReviewMaster> findByPeople_pid(Long pid);
//    // ⭐ Get AVG rating of people
//    @Query("SELECT AVG(r.rating) FROM ReviewMaster r WHERE r.people.pid = :pid")
//    Double getAvgRatingByPeople(Long pid);
//
//    // ⭐ Get AVG rating of movie
//    @Query("SELECT AVG(r.rating) FROM ReviewMaster r WHERE r.movie.movieId = :mid")
//    Double getAvgRatingByMovie(Long mid);

public interface ReviewRepository extends JpaRepository<ReviewMaster, Long> {

    // Find reviews by PeopleMaster ID
    List<ReviewMaster> findByPeopleMaster_Pid(Long pid);

    // Avg rating by PeopleMaster
    @Query("SELECT AVG(r.rating) FROM ReviewMaster r WHERE r.peopleMaster.pid = :pid")
    Double getAvgRatingByPeople(Long pid);

    // Avg rating by Movie
    @Query("SELECT AVG(r.rating) FROM ReviewMaster r WHERE r.movie.movieId = :mid")
    Double getAvgRatingByMovie(Long mid);

}