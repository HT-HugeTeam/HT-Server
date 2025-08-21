package com.ht.htserver.video.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VideoGenerationNotFoundException extends RuntimeException {
    public VideoGenerationNotFoundException() {
        super("해당 영상 생성 요청을 찾을 수 없습니다.");
    }
}