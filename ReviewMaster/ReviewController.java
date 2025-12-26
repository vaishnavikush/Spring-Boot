package com.example.kaisi_lagi.ReviewMaster;

import com.example.kaisi_lagi.ImageMaster.ImageMaster;
import com.example.kaisi_lagi.ImageMaster.ImageRepository;
import com.example.kaisi_lagi.MovieMaster.MovieMaster;
import com.example.kaisi_lagi.MovieMaster.MovieRepository;
import com.example.kaisi_lagi.PeopleMaster.PeopleMaster;
import com.example.kaisi_lagi.PeopleMaster.PeopleRepository;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import com.example.kaisi_lagi.UserMaster.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ReviewController {

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PeopleRepository peopleRepository;
    @Autowired
    MovieRepository movieRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/showpeoplerevi")
    public String showPeopleAndMovies(
            @RequestParam(defaultValue = "movies") String tab,
            @RequestParam(defaultValue = "rating_desc") String sort,
            Model model) {


        List<PeopleMaster> peopleList = peopleRepository.findAll();
        Map<Long, Double> peopleAvgRatings = new HashMap<>();

        for (PeopleMaster p : peopleList) {
            Double avg10 = reviewRepository.getAvgRatingByPeople(p.getPid());
            double avg5 = (avg10 == null ? 0 : avg10 / 2);
            peopleAvgRatings.put(p.getPid(), avg5);
        }

        model.addAttribute("peopleshow", peopleList);
        model.addAttribute("peopleAvgRatings", peopleAvgRatings);


        List<MovieMaster> movieList = movieRepository.findAll();
        Map<Long, Double> movieAvgRatings = new HashMap<>();

        for (MovieMaster m : movieList) {
            Double avg10 = reviewRepository.getAvgRatingByMovie(m.getMovieId());
            double avg5 = (avg10 == null ? 0 : avg10 / 2);
            movieAvgRatings.put(m.getMovieId(), avg5);
        }

        model.addAttribute("movieshow", movieList);
        model.addAttribute("movieAvgRatings", movieAvgRatings);



        List<Map.Entry<Long, Double>> sortedPeople =
                peopleAvgRatings.entrySet().stream()
                        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                        .toList();

        Map<String, Long> peopleMedals = new HashMap<>();
        if (sortedPeople.size() > 0) peopleMedals.put("GOLD", sortedPeople.get(0).getKey());
        if (sortedPeople.size() > 1) peopleMedals.put("SILVER", sortedPeople.get(1).getKey());
        if (sortedPeople.size() > 2) peopleMedals.put("BRONZE", sortedPeople.get(2).getKey());

        model.addAttribute("peopleMedals", peopleMedals);


        //  MOVIES — Top 3 medals
        List<Map.Entry<Long, Double>> sortedMovies =
                movieAvgRatings.entrySet().stream()
                        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                        .toList();

        Map<String, Long> movieMedals = new HashMap<>();
        if (sortedMovies.size() > 0) movieMedals.put("GOLD", sortedMovies.get(0).getKey());
        if (sortedMovies.size() > 1) movieMedals.put("SILVER", sortedMovies.get(1).getKey());
        if (sortedMovies.size() > 2) movieMedals.put("BRONZE", sortedMovies.get(2).getKey());

        model.addAttribute("movieMedals", movieMedals);


        //  ROLE-WISE TOP 3 MEDALS (Actors, Actress, Director…)
        Map<String, List<PeopleMaster>> groupedByRole =
                peopleList.stream().collect(Collectors.groupingBy(PeopleMaster::getRole));

        Map<String, Map<String, Long>> roleMedals = new HashMap<>();

        for (String role : groupedByRole.keySet()) {

            List<PeopleMaster> list = groupedByRole.get(role);

            List<Map.Entry<Long, Double>> sorted =
                    list.stream()
                            .map(p -> Map.entry(p.getPid(), peopleAvgRatings.get(p.getPid())))
                            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                            .toList();

            Map<String, Long> medals = new HashMap<>();
            if (sorted.size() > 0) medals.put("GOLD", sorted.get(0).getKey());
            if (sorted.size() > 1) medals.put("SILVER", sorted.get(1).getKey());
            if (sorted.size() > 2) medals.put("BRONZE", sorted.get(2).getKey());

            roleMedals.put(role, medals);
        }

        model.addAttribute("roleMedals", roleMedals);




        //  APPLY SORTING (MOVIES / ACTOR / ACTRESS / DIRECTOR…)

        switch (tab) {

            case "movies":
                sortMovies(movieList, movieAvgRatings, sort);
                break;

            case "actor":
                List<PeopleMaster> actorList = groupedByRole.get("Actor");
                sortPeople(actorList, peopleAvgRatings, sort);
                model.addAttribute("peopleshow", actorList);
                break;

            case "actress":
                List<PeopleMaster> actressList = groupedByRole.get("Actress");
                sortPeople(actressList, peopleAvgRatings, sort);
                model.addAttribute("peopleshow", actressList);
                break;

            case "director":
                List<PeopleMaster> directorList = groupedByRole.get("Director");
                sortPeople(directorList, peopleAvgRatings, sort);
                model.addAttribute("peopleshow", directorList);
                break;

            case "music":
                List<PeopleMaster> musicList = groupedByRole.get("Music Director");
                sortPeople(musicList, peopleAvgRatings, sort);
                model.addAttribute("peopleshow", musicList);
                break;
        }

        model.addAttribute("tab", tab);
        model.addAttribute("sort", sort);

        return "ReviewMasterPackage/ReviewShowPeopleFinal";

    }

    private void sortPeople(List<PeopleMaster> list, Map<Long, Double> avg, String sort) {

        switch (sort) {

            case "rating_desc": // HIGH → LOW
                list.sort(Comparator.comparingDouble(
                        (PeopleMaster p) -> avg.get(p.getPid())
                ).reversed());
                break;

            case "rating_asc":
                list.sort(Comparator.comparingDouble(
                        p -> avg.get(p.getPid())
                ));
                break;

            case "name_asc":
                list.sort(Comparator.comparing(PeopleMaster::getPeopleName));
                break;

            case "name_desc":
                list.sort(Comparator.comparing(PeopleMaster::getPeopleName).reversed());
                break;
        }
    }


    private void sortMovies(List<MovieMaster> list, Map<Long, Double> avg, String sort) {

        switch (sort) {

            case "rating_desc":
                list.sort(Comparator.comparingDouble(
                        (MovieMaster m) -> avg.get(m.getMovieId())
                ).reversed());
                break;

            case "rating_asc":
                list.sort(Comparator.comparingDouble(
                        m -> avg.get(m.getMovieId())
                ));
                break;

            case "name_asc":
                list.sort(Comparator.comparing(MovieMaster::getMovieName));
                break;

            case "name_desc":
                list.sort(Comparator.comparing(MovieMaster::getMovieName).reversed());
                break;
        }
    }



    //  People Image
    @GetMapping("/getimagerevi/{pid}")
    public ResponseEntity<byte[]> getImage(@PathVariable("pid") Long pid) {
        PeopleMaster PM = peopleRepository.findById(pid).orElse(null);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(PM.getImage());
    }



    //  MOVIE IMAGE
//    @GetMapping("/getimagesok/{movieId}")
//    public ResponseEntity<byte[]> getMovieImage(@PathVariable("movieId") Long movieId) {
//     MovieMaster img = movieRepository.findById(movieId).orElse(null);
//        ImageMaster img = imageRepository.findById(movieId).orElse(null);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
//                .body(img.getMovieImages());
//    }
    @GetMapping("/getimagesok/{id}")
    public ResponseEntity<byte[]> getMovieImage(@PathVariable Long id) {

        ImageMaster img = imageRepository.findFirstByMovieMovieId(id);

        if (img == null || img.getMovieImages() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(img.getMovieImages());
    }


//    //  SHOW RATING FOR PEOPLE
//    @GetMapping("/showrating/{pid}")
//    public String showRating(@PathVariable("pid") Long pid, Model model) {
//
//        PeopleMaster person = peopleRepository.findById(pid).orElse(null);
//        List<ReviewMaster> ratingList = reviewRepository.findByPeople_pid(pid);
//
//        model.addAttribute("person", person);
//        model.addAttribute("ratings", ratingList);
//
//        return "ReviewMasterPackage/ShowPeopleRating";
//    }

    // SHOW RATING FOR PEOPLE
    @GetMapping("/showrating/{pid}")
    public String showRating(@PathVariable("pid") Long pid, Model model) {

        PeopleMaster person = peopleRepository.findById(pid).orElse(null);
        List<ReviewMaster> ratingList = reviewRepository.findByPeopleMaster_Pid(pid);

        model.addAttribute("person", person);
        model.addAttribute("ratings", ratingList);

//        return "ReviewMasterPackage/ShowPeopleRating";
        return "ReviewMasterPackage/ShowPeopleRating";
    }




}