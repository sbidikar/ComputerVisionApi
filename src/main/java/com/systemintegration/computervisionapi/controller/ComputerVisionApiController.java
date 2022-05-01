package com.systemintegration.computervisionapi.controller;

import com.systemintegration.computervisionapi.service.ImageAnalysisService;
import com.systemintegration.computervisionapi.service.OCRService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComputerVisionApiController {

    private final ImageAnalysisService imageAnalysisService;

    private final OCRService ocrService;

    public ComputerVisionApiController(ImageAnalysisService imageAnalysisService, OCRService ocrService) {
        this.imageAnalysisService = imageAnalysisService;
        this.ocrService = ocrService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello User";
    }

    @GetMapping("/analyzeOCRWithPath")
    public ResponseEntity analyzeOCRWithPath(String imagePath) {
        return ocrService.ocr(imagePath);
    }

    @GetMapping("/analyzeImageWithPath")
    public ResponseEntity analyzeImageWithPath(String imagePath) {
        return imageAnalysisService.imageAnalysis(imagePath);
    }


}
