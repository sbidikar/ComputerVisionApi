package com.systemintegration.computervisionapi.service;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageAnalysisService {

    @Value("${subscriptionKey}")
    private String subscriptionKey;
    @Value("${computervision.endpoint}")
    private String endpoint;

    public ResponseEntity  imageAnalysis(String imagePath, MultipartFile imageFile){
        // Create an authenticated Computer Vision client.
        ComputerVisionClient compVisClient = Authenticate(subscriptionKey, endpoint);

        // Analyze local and remote images
        return analyzeRemoteImage(compVisClient, imagePath, imageFile);
    }

    public ComputerVisionClient Authenticate(String subscriptionKey, String endpoint){
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }


    public ResponseEntity analyzeRemoteImage(ComputerVisionClient compVisClient, String imagePath, MultipartFile imageFile) {
        /*
         * Analyze an image from a URL:
         *
         * Set a string variable equal to the path of a remote image.
         */
        //String pathToRemoteImage = "https://github.com/Azure-Samples/cognitive-services-sample-data-files/raw/master/ComputerVision/Images/faces.jpg";

        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromRemoteImage = new ArrayList<>();
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.DESCRIPTION);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.CATEGORIES);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.TAGS);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.FACES);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.ADULT);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.COLOR);
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.IMAGE_TYPE);
        ImageAnalysis analysis;

        try {
            if(StringUtils.isAllEmpty(imagePath)){
                analysis = compVisClient.computerVision().analyzeImageInStream().withImage(imageFile.getBytes())
                        .withVisualFeatures(featuresToExtractFromRemoteImage).execute();
            }
            else{
                // Call the Computer Vision service and tell it to analyze the loaded image.
                analysis = compVisClient.computerVision().analyzeImage().withUrl(imagePath)
                        .withVisualFeatures(featuresToExtractFromRemoteImage).execute();

            }
            // Display image tags and confidence values.
            System.out.println("\nTags: ");
            for (ImageTag tag : analysis.tags()) {
                System.out.printf("\'%s\' with confidence %f\n", tag.name(), tag.confidence());
            }
            return ResponseEntity.ok(analysis);
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
