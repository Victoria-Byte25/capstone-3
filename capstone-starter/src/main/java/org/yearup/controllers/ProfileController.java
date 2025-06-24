package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.models.Profile;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController {

    private final ProfileDao profileDao;

    public ProfileController(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Profile> getProfile(Authentication auth) {
        String email = auth.getName();
        System.out.println("/profile endpoint was hit with user: " + email);
        Profile profile = profileDao.findByUsername(email);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(profile);
    }
    @GetMapping("/test")
    public  String testProfileController() {
        return "Profile controller is working";
    }
}
