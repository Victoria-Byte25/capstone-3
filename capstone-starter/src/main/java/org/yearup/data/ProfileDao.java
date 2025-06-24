package org.yearup.data;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile findByUsername(String username);
    Profile create(Profile profile);
}
