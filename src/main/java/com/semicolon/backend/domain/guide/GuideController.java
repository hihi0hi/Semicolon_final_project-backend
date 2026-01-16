package com.semicolon.backend.domain.guide;

import com.semicolon.backend.domain.guide.dto.GuideDTO;
import com.semicolon.backend.domain.guide.dto.GuideReqDTO;
import com.semicolon.backend.domain.guide.dto.GuideUploadDTO;
import com.semicolon.backend.domain.guide.entity.GuideCategory;
import com.semicolon.backend.domain.guide.entity.GuideUpload;
import com.semicolon.backend.domain.guide.service.GuideService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value; // [추가]
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guide")
@RequiredArgsConstructor
@Slf4j
public class GuideController {

    private final GuideService service;

    // [추가] 설정 파일에서 경로 가져오기
    @Value("${com.semicolon.backend.upload}")
    private String uploadDir;

    @GetMapping("/{category}")
    public ResponseEntity<?> get (@PathVariable GuideCategory category){
        log.info("save get=>{}", category);
        return ResponseEntity.ok(service.get(category));
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload (@ModelAttribute GuideReqDTO guideReqDTO){
        log.info("save upload=>{}", guideReqDTO);
        service.upload(guideReqDTO);
        return ResponseEntity.ok("guide upload 완료");
    }

    @GetMapping("/view/{fileName}")
    public Resource view (@PathVariable String fileName) {
        // [수정] 하드코딩 제거하고 변수 사용
        return new FileSystemResource(uploadDir + "/guide/" + fileName);
    }
}