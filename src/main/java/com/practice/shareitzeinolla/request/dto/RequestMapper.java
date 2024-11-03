package com.practice.shareitzeinolla.request.dto;

import com.practice.shareitzeinolla.request.Request;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RequestMapper {
    public Request fromRequestCreate(RequestCreateDto requestCreateDto) {
        Request request = new Request();
        request.setDescription(requestCreateDto.getDescription());
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public RequestResponseDto toResponse(Request request) {
        RequestResponseDto responseDto = new RequestResponseDto();
        responseDto.setId(request.getId());
        responseDto.setUser(request.getUser());
        responseDto.setDescription(request.getDescription());
        responseDto.setCreated(request.getCreated());
        return responseDto;
    }
}
