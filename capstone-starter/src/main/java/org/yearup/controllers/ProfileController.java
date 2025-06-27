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

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Profile> updateProfile(@RequestBody Profile updatedProfile, Authentication auth) {
        String email = auth.getName();
        System.out.println("/profile PUT endpoint was hit with user: " + email);

        Profile existingProfile = profileDao.findByUsername(email);

        if (existingProfile== null) {
            return ResponseEntity.notFound().build();
        }

        existingProfile.setFirstName(updatedProfile.getFirstName());
        existingProfile.setLastName(updatedProfile.getLastName());
        existingProfile.setPhone(updatedProfile.getPhone());
        existingProfile.setAddress(updatedProfile.getAddress());
        if (updatedProfile.getCity() != null && !updatedProfile.getCity().isBlank()) {
            existingProfile.setCity(updatedProfile.getCity());
        }
        if (updatedProfile.getState() != null && !updatedProfile.getState().isBlank()){
            existingProfile.setState(updatedProfile.getState());
        }
        if (updatedProfile.getZip() != null && !updatedProfile.getZip().isBlank()) {
            existingProfile.setZip(updatedProfile.getZip());
        }

        profileDao.update(existingProfile);


        return ResponseEntity.ok(existingProfile);
    }
    @GetMapping("/test")
    public  String testProfileController() {
        return "Profile controller is working";
    }

    @GetMapping("/get")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Profile> getProfile(Authentication auth) {
        String email = auth.getName();
        Profile profile = profileDao.findByUsername(email);

        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return  ResponseEntity.ok(profile);
    }
}
