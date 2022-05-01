package com.systemintegration.computervisionapi.controller;

import com.systemintegration.computervisionapi.service.ImageAnalysisService;
import com.systemintegration.computervisionapi.service.OCRService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return ocrService.ocr(imagePath, null);
    }

    @GetMapping("/analyzeImageWithPath")
    public ResponseEntity analyzeImageWithPath(String imagePath) {
        return imageAnalysisService.imageAnalysis(imagePath, null);
    }

    @PostMapping(value = "/analyzeOCRUpload", consumes={ MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity analyzeOCRUpload(@RequestParam("file") MultipartFile file) {
        return ocrService.ocr(null, file);
    }

    @PostMapping(value = "/analyzeImageWithUpload", consumes={ MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity analyzeImageWithUpload(@RequestParam("file") MultipartFile file) {
        return imageAnalysisService.imageAnalysis(null, file);
    }


}
