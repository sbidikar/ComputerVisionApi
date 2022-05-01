package com.systemintegration.computervisionapi.service;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageAnalysis;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.ImageTag;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.VisualFeatureTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ImageAnalysisService {

    static String subscriptionKey = "467d3d353dfd4fffbf74b1c1931a658d";
    @Value("${computervision.endpoint}")
    private String endpoint;

    public ResponseEntity imageAnalysis(String imagePath){
        // Create an authenticated Computer Vision client.
        ComputerVisionClient compVisClient = Authenticate(subscriptionKey, endpoint);

        // Analyze local and remote images
        return analyzeRemoteImage(compVisClient, imagePath);
    }

    public ComputerVisionClient Authenticate(String subscriptionKey, String endpoint){
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }


    public ResponseEntity analyzeRemoteImage(ComputerVisionClient compVisClient, String imagePath) {
        /*
         * Analyze an image from a URL:
         *
         * Set a string variable equal to the path of a remote image.
         */
        //String pathToRemoteImage = "https://github.com/Azure-Samples/cognitive-services-sample-data-files/raw/master/ComputerVision/Images/faces.jpg";

        // This list defines the features to be extracted from the image.
        List<VisualFeatureTypes> featuresToExtractFromRemoteImage = new ArrayList<>();
        featuresToExtractFromRemoteImage.add(VisualFeatureTypes.TAGS);

        System.out.println("\n\nAnalyzing an image from a URL ...");

        try {
            // Call the Computer Vision service and tell it to analyze the loaded image.
            ImageAnalysis analysis = compVisClient.computerVision().analyzeImage().withUrl(imagePath)
                    .withVisualFeatures(featuresToExtractFromRemoteImage).execute();


            // Display image tags and confidence values.
            System.out.println("\nTags: ");
            for (ImageTag tag : analysis.tags()) {
                System.out.printf("\'%s\' with confidence %f\n", tag.name(), tag.confidence());
            }
            return ResponseEntity.ok(analysis.tags());
        }

        catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
