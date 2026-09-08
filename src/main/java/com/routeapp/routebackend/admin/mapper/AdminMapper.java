package com.routeapp.routebackend.admin.mapper;

import com.routeapp.routebackend.admin.dto.response.AdminResponseDto;
import com.routeapp.routebackend.admin.entity.Admin;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    public AdminResponseDto toResponseDto(Admin admin) {
        return AdminResponseDto.from(admin);
    }
}