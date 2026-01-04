package com.example.kaisi_lagi.ReviewMaster;

import com.example.kaisi_lagi.ImageMaster.ImageMaster;
import com.example.kaisi_lagi.ImageMaster.ImageRepository;
import com.example.kaisi_lagi.MovieCast.MovieCast;
import com.example.kaisi_lagi.MovieCast.MovieCastRepository;
import com.example.kaisi_lagi.MovieMaster.MovieMaster;
import com.example.kaisi_lagi.MovieMaster.MovieRepository;
import com.example.kaisi_lagi.PeopleMaster.PeopleMaster;
import com.example.kaisi_lagi.PeopleMaster.PeopleRepository;
import com.example.kaisi_lagi.UserMaster.UserMaster;
import com.example.kaisi_lagi.UserMaster.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private PeopleRepository peopleRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private MovieCastRepository movieCastRepository;

    // ---------------- PEOPLE & MOVIE RATINGS DISPLAY ----------------
    @GetMapping("/showpeoplerevi")
    public String showPeopleAndMovies(
            @RequestParam(defaultValue = "movies") String tab,
            @RequestParam(defaultValue = "rating_desc") String sort,
            Model model) {

        List<PeopleMaster> peopleList = peopleRepository.findAll();
        Map<Long, Double> peopleAvgRatings = new HashMap<>();
        for (PeopleMaster p : peopleList) {
            Double avg10 = reviewRepository.getAvgRatingByPeople(p.getPid());
            peopleAvgRatings.put(p.getPid(), avg10 == null ? 0 : avg10 / 2);
        }

        List<MovieMaster> movieList = movieRepository.findAll();
        Map<Long, Double> movieAvgRatings = new HashMap<>();
        for (MovieMaster m : movieList) {
            Double avg10 = reviewRepository.getAvgRatingByMovie(m.getMovieId());
            movieAvgRatings.put(m.getMovieId(), avg10 == null ? 0 : avg10 / 2);
        }

        model.addAttribute("peopleshow", peopleList);
        model.addAttribute("peopleAvgRatings", peopleAvgRatings);
        model.addAttribute("movieshow", movieList);
        model.addAttribute("movieAvgRatings", movieAvgRatings);

        // Top 3 medals
        model.addAttribute("peopleMedals", getTop3Medals(peopleAvgRatings));
        model.addAttribute("movieMedals", getTop3Medals(movieAvgRatings));

        // Role-wise medals
        Map<String, List<PeopleMaster>> groupedByRole = new HashMap<>();
        for (PeopleMaster p : peopleList) {
            groupedByRole.computeIfAbsent(p.getRole(), k -> new ArrayList<>()).add(p);
        }
        Map<String, Map<String, Long>> roleMedals = new HashMap<>();
        for (String role : groupedByRole.keySet()) {
            roleMedals.put(role, getTop3MedalsForPeople(groupedByRole.get(role), peopleAvgRatings));
        }
        model.addAttribute("roleMedals", roleMedals);

        // Apply sorting
        switch (tab) {
            case "movies":
                sortMovies(movieList, movieAvgRatings, sort);
                break;
            case "actor":
            case "actress":
            case "director":
            case "music":
                List<PeopleMaster> list = groupedByRole.getOrDefault(
                        tab.equals("music") ? "Music Director" : capitalize(tab), new ArrayList<>());
                sortPeople(list, peopleAvgRatings, sort);
                model.addAttribute("peopleshow", list);
                break;
        }

        model.addAttribute("tab", tab);
        model.addAttribute("sort", sort);
        return "ReviewShowPeopleFinal";
    }

    private Map<String, Long> getTop3Medals(Map<Long, Double> avgRatings) {
        Map<String, Long> medals = new HashMap<>();
        avgRatings.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .forEachOrdered(e -> {
                    if (!medals.containsKey("GOLD")) medals.put("GOLD", e.getKey());
                    else if (!medals.containsKey("SILVER")) medals.put("SILVER", e.getKey());
                    else medals.put("BRONZE", e.getKey());
                });
        return medals;
    }

    private Map<String, Long> getTop3MedalsForPeople(List<PeopleMaster> list, Map<Long, Double> avgRatings) {
        Map<String, Long> medals = new HashMap<>();
        list.stream()
                .map(p -> Map.entry(p.getPid(), avgRatings.get(p.getPid())))
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .forEachOrdered(e -> {
                    if (!medals.containsKey("GOLD")) medals.put("GOLD", e.getKey());
                    else if (!medals.containsKey("SILVER")) medals.put("SILVER", e.getKey());
                    else medals.put("BRONZE", e.getKey());
                });
        return medals;
    }

    private void sortPeople(List<PeopleMaster> list, Map<Long, Double> avg, String sort) {
        switch (sort) {
            case "rating_desc" -> list.sort(Comparator.comparingDouble((PeopleMaster p) -> avg.get(p.getPid())).reversed());
            case "rating_asc" -> list.sort(Comparator.comparingDouble(p -> avg.get(p.getPid())));
            case "name_asc" -> list.sort(Comparator.comparing(PeopleMaster::getPeopleName));
            case "name_desc" -> list.sort(Comparator.comparing(PeopleMaster::getPeopleName).reversed());
        }
    }

    private void sortMovies(List<MovieMaster> list, Map<Long, Double> avg, String sort) {
        switch (sort) {
            case "rating_desc" -> list.sort(Comparator.comparingDouble((MovieMaster m) -> avg.get(m.getMovieId())).reversed());
            case "rating_asc" -> list.sort(Comparator.comparingDouble(m -> avg.get(m.getMovieId())));
            case "name_asc" -> list.sort(Comparator.comparing(MovieMaster::getMovieName));
            case "name_desc" -> list.sort(Comparator.comparing(MovieMaster::getMovieName).reversed());
        }
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    // ---------------- PEOPLE IMAGE ----------------
    @GetMapping("/getimagerevi/{pid}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long pid) {
        PeopleMaster PM = peopleRepository.findById(pid).orElse(null);
        if (PM == null || PM.getImage() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(PM.getImage());
    }

    // ---------------- MOVIE IMAGE ----------------
    @GetMapping("/getimagesok/{id}")
    public ResponseEntity<byte[]> getMovieImage(@PathVariable Long id) {
        ImageMaster img = imageRepository.findFirstByMovieMovieId(id);
        if (img == null || img.getMovieImages() == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(img.getMovieImages());
    }

    // ---------------- PEOPLE REVIEW ----------------
    @GetMapping("/showrating/{pid}")
    public String showRating(@PathVariable Long pid, Model model) {
        PeopleMaster person = peopleRepository.findById(pid).orElse(null);
        List<ReviewMaster> ratingList = reviewRepository.findByPeopleMaster_Pid(pid);
        model.addAttribute("person", person);
        model.addAttribute("ratings", ratingList);
        return "ShowPeopleRating";
    }

    // ---------------- MOVIE RATING SUBMIT ----------------
    @PostMapping("/setRating")
    public String setReview(
            HttpSession session,
            @RequestParam int rating,
            @RequestParam String comment,
            @RequestParam Long movieId,
            RedirectAttributes redirectAttributes
    ) {
        UserMaster user1 = (UserMaster) session.getAttribute("loggedUser");
        System.out.println(user1.getEmail() +" "+user1.getUsername());
        if (user1 == null) return "redirect:/login";
        Long userId = user1.getId();
        System.out.println(user1);

        UserMaster user = userRepository.findById(userId).orElse(null);
        MovieMaster movie = movieRepository.findById(movieId).orElse(null);
        if (user == null || movie == null) return "redirect:/error";

        ReviewMaster review = reviewRepository.findByUserAndMovieAndPeopleMasterIsNull(user, movie)
                .orElse(new ReviewMaster());
        review.setUser(user);
        review.setMovie(movie);
        review.setRating(rating);
        if (comment != null && !comment.isBlank()) review.setComment(comment);
        review.setReviewDate(LocalDateTime.now());
        reviewRepository.save(review);

        redirectAttributes.addAttribute("MovieRateSuccess", true);
        return "redirect:/movies/" + movieId;
    }

    // ---------------- CAST RATING ----------------
    @GetMapping("/CastRating/{MovieId}")
    public String castRate(@PathVariable Long MovieId, Model mdl, HttpServletRequest request) {
        Optional<MovieMaster> movie = movieRepository.findById(MovieId);
        if (movie.isEmpty()) return "redirect:/not-found";

        List<MovieCast> cast = movieCastRepository.findAllByMovie(movie.get());
        Map<String, List<PeopleMaster>> roleMap = new HashMap<>();
        roleMap.put("actor", new ArrayList<>());
        roleMap.put("actress", new ArrayList<>());
        roleMap.put("singer", new ArrayList<>());
        roleMap.put("director", new ArrayList<>());
        roleMap.put("writer", new ArrayList<>());
        roleMap.put("producer", new ArrayList<>());
        roleMap.put("others", new ArrayList<>());

        for (MovieCast c : cast) {
            PeopleMaster p = c.getPeople();
            switch (p.getRole().toLowerCase()) {
                case "actor" -> roleMap.get("actor").add(p);
                case "actress" -> roleMap.get("actress").add(p);
                case "singer" -> roleMap.get("singer").add(p);
                case "director" -> roleMap.get("director").add(p);
                case "writer" -> roleMap.get("writer").add(p);
                case "producer" -> roleMap.get("producer").add(p);
                default -> roleMap.get("others").add(p);
            }
        }

        mdl.addAttribute("actor", roleMap.get("actor"));
        mdl.addAttribute("actress", roleMap.get("actress"));
        mdl.addAttribute("singer", roleMap.get("singer"));
        mdl.addAttribute("director", roleMap.get("director"));
        mdl.addAttribute("writer", roleMap.get("writer"));
        mdl.addAttribute("producer", roleMap.get("producer"));
        mdl.addAttribute("ppl", roleMap.get("others"));
        mdl.addAttribute("movie", movie.get());
        mdl.addAttribute("url", request.getHeader("Referer"));
        return "CastRatingPage";
    }

    // ---------------- CAST RATING SUBMIT ----------------
    @PostMapping("/CastRateSubmit")
    public String castRateSubmit(
            @RequestParam("peopleId[]") List<Long> castIds,
            @RequestParam("url") String url,
            @RequestParam("movieId") Long movieId,
            HttpSession session,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        UserMaster user1 = (UserMaster) session.getAttribute("loggedUser");
        System.out.println(user1.getEmail() +" "+user1.getUsername());
        if (user1 == null) return "redirect:/login";
        Long userId = user1.getId();
        System.out.println(user1);


        UserMaster user = userRepository.findById(userId).orElse(null);
        MovieMaster movie = movieRepository.findById(movieId).orElse(null);
        if (user == null || movie == null) return "redirect:/not-found";

        for (Long pid : castIds) {
            String ratingStr = request.getParameter("rating_" + pid);
            if (ratingStr == null || ratingStr.isEmpty()) continue;
            int rating = Integer.parseInt(ratingStr);
            if (rating == 0) continue;

            PeopleMaster people = peopleRepository.findById(pid).orElse(null);
            if (people == null) continue;

            ReviewMaster prev = reviewRepository.findByUserAndMovieAndPeopleMaster(user, movie, people);
            if (prev != null) reviewRepository.delete(prev);

            ReviewMaster review = new ReviewMaster();
            review.setUser(user);
            review.setMovie(movie);
            review.setPeopleMaster(people);
            review.setRating(rating);
            review.setReviewDate(LocalDateTime.now());
            reviewRepository.save(review);
        }

        redirectAttributes.addAttribute("MovieRateSuccess", true);
        return "redirect:/movies/" + movieId;
    }
}