package com.elevate.auth.service;

import com.elevate.auth.dto.ApiResponse;
import com.elevate.auth.dto.UserClassReqDTO;
import com.elevate.auth.dto.UserClassResDTO;
import com.elevate.auth.entity.UserClass;
import com.elevate.auth.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegService {

    private final UserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserRegService(UserRepo userRepo) {
        this.userRepo = userRepo;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public ApiResponse<?> registerNewUser(UserClassReqDTO userClassReqDTO){
        if(userRepo.findByUsername(userClassReqDTO.getUsername())!=null){
            return new ApiResponse<>("User already exist",409,null);
        }
        UserClass newUser = new UserClass(
                userClassReqDTO.getUsername(),
                bCryptPasswordEncoder.encode(userClassReqDTO.getPassword()),
                userClassReqDTO.getRole()
        );
        userRepo.save(newUser);
        UserClassResDTO userClassResponse = new UserClassResDTO(newUser);
        return new ApiResponse<>("User registered successfully",200,userClassResponse);
    }

    public ApiResponse<?> returnUser(UserClassReqDTO userClassReqDTO) {
        UserClass user =  userRepo.findByUsername(userClassReqDTO.getUsername());
        if(user==null){
            return new ApiResponse<>("User not found",404,null);
        }
        else if(bCryptPasswordEncoder.matches(userClassReqDTO.getPassword(),user.getPassword())){
            UserClassResDTO userResponse = new UserClassResDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getRole()
            );
            return new ApiResponse<>("User found successfully",200,userResponse);
        }
        else {
            return new ApiResponse<>("Incorrect password",401,null);
        }
    }
}
