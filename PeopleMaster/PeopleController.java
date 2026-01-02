package com.example.kaisi_lagi.PeopleMaster;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class PeopleController {
    @Autowired
    PeopleRepository peopleRepository;

    @GetMapping("/setpeople")
    public String setPeople() {
        return "PeopleMasterHTML/PeopleMasterForm";
    }

    @PostMapping("/peopleset")
    public String peopleset(@RequestParam String peopleName, @RequestParam String role
            , @RequestParam MultipartFile image, @RequestParam String people_bio, @RequestParam String people_awards, @RequestParam String people_debut, @RequestParam LocalDate people_dob,
                            @RequestParam int debut_date) {
        PeopleMaster people = new PeopleMaster();
        //for saving data
        String[] parts = peopleName.trim().split("\\s+");
        StringBuilder finalName = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                finalName.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1).toLowerCase())
        
                    .append(" ");
            }
        }

        peopleName = finalName.toString().trim();


//        String name=peopleName.substring(0,1).toUpperCase()+peopleName.substring(1).toLowerCase();
        people.setPeopleName(peopleName);
        people.setPeople_bio(people_bio);
        people.setRole(role);
        people.setPeople_awards(people_awards);
        people.setPeople_debut(people_debut);
        people.setPeople_dob(people_dob);
        people.setDebut_date(debut_date);
        //for saving image
        System.out.println("Hola kaskkj...");
//        people.setPoints(points);
        try {
            if (image != null && !image.isEmpty()) {
                people.setImage(image.getBytes());
                System.out.println("Hola....");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        peopleRepository.save(people);
        return "DATASAVEFORM";
    }

    //for showing data
    @GetMapping("/showpeople")
    public String showpeople(Model model) {
        Iterable<PeopleMaster> people = peopleRepository.findAll();
        List<PeopleMaster> listpeople = new ArrayList<>();
        people.forEach(listpeople::add);
        model.addAttribute("peopleshow", listpeople);
        return "PeopleMasterHTML/PeopleMasterShowForm";
//        return "PeopleMasterHTML/PeopleFullShow";
    }

    //for showing image
    @GetMapping("/getimage/{pid}")
    public ResponseEntity<byte[]> getImage(@PathVariable("pid") Long pid) {
//     int i=Integer.parseInt(pid);
        PeopleMaster peopleMaster = peopleRepository.findById(pid).orElse(null);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(peopleMaster.getImage());
    }

    @GetMapping("/delete/{pid}")
    public String DeletePeople(@PathVariable long pid) {
        peopleRepository.deleteById(pid);
//        return "redirect:/showpeople";
        return "redirect:/showpeople";
    }
    //for update
    @GetMapping  ("/updatepeople/{pid}")
    public String getPageUpadate(@PathVariable("pid") long pid , Model model) {
        Optional<PeopleMaster> optionalpeople = peopleRepository.findById(pid);
        model.addAttribute("updatepeoplemaster",optionalpeople.get());
        return "PeopleMasterHTML/updatepage";
    }

    //
    @PostMapping("/updatepeople")
    public String updatebyid( @ModelAttribute("updatepeoplemaster") PeopleMaster peopleMaster,
                              @RequestParam("img") MultipartFile img) {
        Optional<PeopleMaster> optionalpeople = peopleRepository.findById(peopleMaster.getPid());
        PeopleMaster master=optionalpeople.get();
        master.setPeopleName(peopleMaster.getPeopleName());
        master.setRole(peopleMaster.getRole());
////        master.setPoints(peopleMaster.getPoints());
//        master.setImage(peopleMaster.getImage());
        try {
            if (img!=null && !img.isEmpty()) {
//                master.setImage(peopleMaster.getImage());
                master.setImage(img.getBytes());
                System.out.println("Hola....");
            }
        } catch (Exception e) {
            System.out.print(e);
            throw new RuntimeException(e);
        }
        peopleRepository.save(master);
        return "redirect:/showpeople";

    }


    // SEARCH BY ROLE (SHOW IN SAME PAGE)
    @PostMapping("/searchbyrole")
    public String SearchByRole(@RequestParam("role") String role, Model model) {

        List<PeopleMaster> searchlist;

        if (role == null || role.isEmpty()) {
            searchlist = peopleRepository.findAll();
        } else {
            searchlist = peopleRepository.findByRoleIgnoreCase(role);
        }

        model.addAttribute("peopleshow", searchlist);

        return "PeopleMasterHTML/PeopleMasterShowForm"; // same page
    }



    // SEARCH BY NAME (SHOW IN SAME PAGE)
    @GetMapping("/serachbyname")
    public String SearchByName(@RequestParam("peopleName") String peopleName, Model model) {

        List<PeopleMaster> searchList;

        if (peopleName == null || peopleName.isEmpty()) {
            searchList = peopleRepository.findAll();
        } else {
            searchList = peopleRepository.findByPeopleNameContainingIgnoreCase(peopleName);
        }

        model.addAttribute("peopleshow", searchList);

        return "PeopleMasterHTML/PeopleMasterShowForm"; // same page
    }


    @GetMapping("/filmography")
    public String filmographypage(){
        return "PeopleMasterHTML/FilmoGraphyForm";
    }

}
