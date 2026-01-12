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

import static org.apache.tomcat.util.IntrospectionUtils.capitalize;

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

    @GetMapping("/showpeoplerevi")
    public String showPeopleAndMovies(
            @RequestParam(defaultValue = "movies") String tab,
            @RequestParam(defaultValue = "rating_desc") String sort,
            Model model) {

        // ===================== PEOPLE =====================
        List<PeopleMaster> peopleList = peopleRepository.findAll();

        Map<Long, Double> peopleAvgRatings = new HashMap<>();
        Map<Long, Long> peopleVotes = new HashMap<>();

        for (PeopleMaster p : peopleList) {
            Double avg10 = reviewRepository.getAvgRatingByPeople(p.getPid());
            Long votes = reviewRepository.getVoteCountByPeople(p.getPid());

            peopleAvgRatings.put(p.getPid(), avg10 == null ? 0 : avg10 / 2);
            peopleVotes.put(p.getPid(), votes == null ? 0L : votes);
        }

        // ===================== MOVIES =====================
        List<MovieMaster> movieList = movieRepository.findAll();

        Map<Long, Double> movieAvgRatings = new HashMap<>();
        Map<Long, Long> movieVotes = new HashMap<>();

        for (MovieMaster m : movieList) {
            Double avg10 = reviewRepository.getAvgRatingByMovie(m.getMovieId());
            Long votes = reviewRepository.getVoteCountByMovie(m.getMovieId());

            movieAvgRatings.put(m.getMovieId(), avg10 == null ? 0 : avg10 / 2);
            movieVotes.put(m.getMovieId(), votes == null ? 0L : votes);
        }

        // ===================== GROUP PEOPLE BY ROLE =====================
        Map<String, List<PeopleMaster>> groupedByRole = new HashMap<>();
        for (PeopleMaster p : peopleList) {
            groupedByRole
                    .computeIfAbsent(p.getRole(), k -> new ArrayList<>())
                    .add(p);
        }

        // ===================== GROUP MOVIES BY CATEGORY =====================
//        Map<String, List<MovieMaster>> groupedByCategory = new HashMap<>();
//
//        for (MovieMaster m : movieList) {
//            if (m.getCategory() != null) {
//                String categoryName = m.getCategory().getName(); // ✅ correct getter
//                groupedByCategory
//                        .computeIfAbsent(categoryName, k -> new ArrayList<>())
//                        .add(m);
//            }
//        }

        // ===================== GROUP MOVIES BY CATEGORY =====================
        Map<String, List<MovieMaster>> groupedByCategory = new HashMap<>();

        for (MovieMaster m : movieList) {
            if (m.getCategory() != null && m.getCategory().getName() != null) {

                String categoryName = m.getCategory()
                        .getName()
                        .trim()
                        .toLowerCase();   // ✅ FIX HERE

                groupedByCategory
                        .computeIfAbsent(categoryName, k -> new ArrayList<>())
                        .add(m);
            }
        }


        // ===================== MODEL COMMON =====================
        model.addAttribute("peopleAvgRatings", peopleAvgRatings);
        model.addAttribute("peopleVotes", peopleVotes);
        model.addAttribute("movieAvgRatings", movieAvgRatings);
        model.addAttribute("movieVotes", movieVotes);

        // ===================== GLOBAL MEDALS =====================
        model.addAttribute("peopleMedals",
                getTop3Medals(peopleAvgRatings, peopleVotes));

        model.addAttribute("movieMedals",
                getTop3Medals(movieAvgRatings, movieVotes));

        // ===================== ROLE-WISE PEOPLE MEDALS =====================
        Map<String, Map<String, Long>> roleMedals = new HashMap<>();
        for (String role : groupedByRole.keySet()) {
            roleMedals.put(
                    role,
                    getTop3MedalsForPeople(
                            groupedByRole.get(role),
                            peopleAvgRatings,
                            peopleVotes
                    )
            );
        }
        model.addAttribute("roleMedals", roleMedals);

        // ===================== CATEGORY-WISE MOVIE MEDALS =====================
        Map<String, Map<String, Long>> categoryMedals = new HashMap<>();

        for (String category : groupedByCategory.keySet()) {

            Map<Long, Double> avgMap = new HashMap<>();
            Map<Long, Long> voteMap = new HashMap<>();

            for (MovieMaster m : groupedByCategory.get(category)) {
                avgMap.put(m.getMovieId(), movieAvgRatings.get(m.getMovieId()));
                voteMap.put(m.getMovieId(), movieVotes.get(m.getMovieId()));
            }

            categoryMedals.put(category, getTop3Medals(avgMap, voteMap));
        }
        model.addAttribute("categoryMedals", categoryMedals);

        // ===================== TAB & SORT LOGIC =====================
        switch (tab) {

            // ---------- MOVIE TABS ----------
//            case "movies":
//            case "webseries":
//            case "serial":
//
//                String category =
//                        tab.equals("movies") ? "Movie" :
//                                tab.equals("webseries") ? "Web Series" :
//                                        "Serial";
//
//                List<MovieMaster> movieTabList =
//                        groupedByCategory.getOrDefault(category, new ArrayList<>());
//
//                sortMovies(movieTabList, movieAvgRatings, movieVotes, sort);
//                model.addAttribute("movieshow", movieTabList);
//                break;

            case "movies":
            case "webseries":
            case "serial":

                String category =
                        tab.equals("movies") ? "movie" :
                                tab.equals("webseries") ? "web series" :
                                        "serial";

                List<MovieMaster> movieTabList =
                        groupedByCategory.getOrDefault(category, new ArrayList<>());

                sortMovies(movieTabList, movieAvgRatings, movieVotes, sort);
                model.addAttribute("movieshow", movieTabList);
                break;


            // ---------- PEOPLE TABS ----------
            case "actor":
            case "actress":
            case "director":
            case "music":

                String role =
                        tab.equals("music") ? "Music Director" : capitalize(tab);

                List<PeopleMaster> peopleTabList =
                        groupedByRole.getOrDefault(role, new ArrayList<>());

                sortPeople(peopleTabList, peopleAvgRatings, peopleVotes, sort);
                model.addAttribute("peopleshow", peopleTabList);
                break;
        }

        model.addAttribute("tab", tab);
        model.addAttribute("sort", sort);

        return "ReviewShowPeopleFinal";
    }
    private Map<String, Long> getTop3Medals(
            Map<Long, Double> avgRatings,
            Map<Long, Long> votes) {

        Map<String, Long> medals = new HashMap<>();

        avgRatings.entrySet().stream()
                .sorted(
                        Map.Entry.<Long, Double>comparingByValue().reversed()
                                .thenComparing(
                                        e -> votes.get(e.getKey()),
                                        Comparator.reverseOrder()
                                )
                )
                .limit(3)
                .forEachOrdered(e -> {
                    if (!medals.containsKey("GOLD")) medals.put("GOLD", e.getKey());
                    else if (!medals.containsKey("SILVER")) medals.put("SILVER", e.getKey());
                    else medals.put("BRONZE", e.getKey());
                });

        return medals;
    }

    private Map<String, Long> getTop3MedalsForPeople(
            List<PeopleMaster> list,
            Map<Long, Double> avgRatings,
            Map<Long, Long> votes) {

        Map<String, Long> medals = new HashMap<>();

        list.stream()
                .sorted(
                        Comparator
                                .comparingDouble((PeopleMaster p) -> avgRatings.get(p.getPid()))
                                .reversed()
                                .thenComparing(
                                        p -> votes.get(p.getPid()),
                                        Comparator.reverseOrder()
                                )
                )
                .limit(3)
                .forEachOrdered(p -> {
                    if (!medals.containsKey("GOLD")) medals.put("GOLD", p.getPid());
                    else if (!medals.containsKey("SILVER")) medals.put("SILVER", p.getPid());
                    else medals.put("BRONZE", p.getPid());
                });

        return medals;
    }

    private void sortPeople(
            List<PeopleMaster> list,
            Map<Long, Double> avg,
            Map<Long, Long> votes,
            String sort) {

        switch (sort) {
            case "rating_desc" ->
                    list.sort(
                            Comparator.comparingDouble((PeopleMaster p) -> avg.get(p.getPid()))
                                    .reversed()
                                    .thenComparing(p -> votes.get(p.getPid()), Comparator.reverseOrder())
                    );

            case "rating_asc" ->
                    list.sort(
                            Comparator.comparingDouble((PeopleMaster p) -> avg.get(p.getPid()))
                                    .thenComparing(p -> votes.get(p.getPid()), Comparator.reverseOrder())
                    );

            case "name_asc" ->
                    list.sort(Comparator.comparing(PeopleMaster::getPeopleName));

            case "name_desc" ->
                    list.sort(Comparator.comparing(PeopleMaster::getPeopleName).reversed());
        }
    }

    private void sortMovies(
            List<MovieMaster> list,
            Map<Long, Double> avg,
            Map<Long, Long> votes,
            String sort) {

        switch (sort) {
            case "rating_desc" ->
                    list.sort(
                            Comparator.comparingDouble((MovieMaster m) -> avg.get(m.getMovieId()))
                                    .reversed()
                                    .thenComparing(m -> votes.get(m.getMovieId()), Comparator.reverseOrder())
                    );

            case "rating_asc" ->
                    list.sort(
                            Comparator.comparingDouble((MovieMaster m) -> avg.get(m.getMovieId()))
                                    .thenComparing(m -> votes.get(m.getMovieId()), Comparator.reverseOrder())
                    );

            case "name_asc" ->
                    list.sort(Comparator.comparing(MovieMaster::getMovieName));

            case "name_desc" ->
                    list.sort(Comparator.comparing(MovieMaster::getMovieName).reversed());
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
