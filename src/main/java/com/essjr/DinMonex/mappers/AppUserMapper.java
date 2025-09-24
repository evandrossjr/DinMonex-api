package com.essjr.DinMonex.mappers;

import com.essjr.DinMonex.dtos.AppUserDTO;
import com.essjr.DinMonex.model.AppUser;

public class AppUserMapper {

    public static AppUserDTO toDto(AppUser appUser){

        if (appUser == null){
            return null;
        }

        return new AppUserDTO(
                appUser.getId(),
                appUser.getName(),
                appUser.getEmail(),
                appUser.getRole()
        );
    }


    public static AppUser toEntity(AppUserDTO appUserDTO){

        if (appUserDTO == null){
            return null;
        }

        return new AppUser(appUserDTO.id() ,appUserDTO.name(), appUserDTO.email(), appUserDTO.role );
    }
}
