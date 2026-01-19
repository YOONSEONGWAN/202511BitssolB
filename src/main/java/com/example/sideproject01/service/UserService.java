package com.example.sideproject01.service;

import com.example.sideproject01.dto.UserLoginRequestDto;
import com.example.sideproject01.dto.UserResponseDto;
import com.example.sideproject01.dto.UserSignupRequestDto;

public interface UserService {

    Long signup(UserSignupRequestDto requestDto);

    String login(UserLoginRequestDto requestDto);

    UserResponseDto getMyInfo(String userName);

    void changeNickname(String userName, String newNickname);
    
    //  추가
    void changeEmail(String userName, String newEmail);
    void changePhone(String userName, String newPhone);
}
