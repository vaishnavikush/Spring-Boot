package com.example.kaisi_lagi.PeopleMaster;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeopleRepository extends JpaRepository<PeopleMaster,Long> {
    List<PeopleMaster>findByRoleIgnoreCase(String role);
    List<PeopleMaster>findByPeopleNameContainingIgnoreCase(String peopleName);
}
