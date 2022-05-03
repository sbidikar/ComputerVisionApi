package com.systemintegration.computervisionapi.service;

import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVision;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionClient;
import com.microsoft.azure.cognitiveservices.vision.computervision.ComputerVisionManager;
import com.microsoft.azure.cognitiveservices.vision.computervision.implementation.ComputerVisionImpl;
import com.microsoft.azure.cognitiveservices.vision.computervision.models.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class OCRService {

    @Value("${computervision.subscriptionKey}")
    private String subscriptionKey;
    @Value("${computervision.endpoint}")
    private String endpoint;

    public ResponseEntity ocr(String imagePath, MultipartFile file){
        // Create an authenticated Computer Vision client.
        ComputerVisionClient compVisClient = authenticate(subscriptionKey, endpoint);

        // Read from remote image
        return readFromUrl(compVisClient, imagePath, file);
    }

    public ComputerVisionClient authenticate(String subscriptionKey, String endpoint){
        return ComputerVisionManager.authenticate(subscriptionKey).withEndpoint(endpoint);
    }

    /**
     * OCR with READ : Performs a Read Operation
     * @param client instantiated vision client
     * @param imagePath
     */
    private ResponseEntity readFromUrl(ComputerVisionClient client, String imagePath, MultipartFile file) {

        //String remoteTextImageURL = "https://raw.githubusercontent.com/Azure-Samples/cognitive-services-sample-data-files/master/ComputerVision/Images/printed_text.jpg";
        //System.out.println("Read with URL: " + imagePath);

        try {
            // Cast Computer Vision to its implementation to expose the required methods
            ComputerVisionImpl vision = (ComputerVisionImpl) client.computerVision();
            String operationLocation = null;

            if(StringUtils.isEmpty(imagePath)){
                ReadInStreamHeaders readInStreamHeaders = vision.readInStreamWithServiceResponseAsync(file.getBytes(),null)
                        .toBlocking()
                        .single()
                        .headers();
                operationLocation = readInStreamHeaders.operationLocation();
            }
            else{
                // Read in remote image and response header
                ReadHeaders responseHeader = vision.readWithServiceResponseAsync(imagePath, null)
                        .toBlocking()
                        .single()
                        .headers();

                // Extract the operation Id from the operationLocation header
                operationLocation = responseHeader.operationLocation();
            }

            System.out.println("Operation Location:" + operationLocation);

            String result = getResult(vision, operationLocation);

            return ResponseEntity.status(HttpStatus.OK).body(result);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    /**
     * Extracts the OperationId from a Operation-Location returned by the POST Read operation
     * @param operationLocation
     * @return operationId
     */
    private String extractOperationIdFromOpLocation(String operationLocation) {
        if (operationLocation != null && !operationLocation.isEmpty()) {
            String[] splits = operationLocation.split("/");

            if (splits != null && splits.length > 0) {
                return splits[splits.length - 1];
            }
        }
        throw new IllegalStateException("Something went wrong: Couldn't extract the operation id from the operation location");
    }

    /**
     * Polls for Read result and prints results to console
     * @param vision Computer Vision instance
     * @return operationLocation returned in the POST Read response header
     */
    private String getResult(ComputerVision vision, String operationLocation) throws InterruptedException {
        System.out.println("Polling for Read results ...");

        // Extract OperationId from Operation Location
        String operationId = extractOperationIdFromOpLocation(operationLocation);

        boolean pollForResult = true;
        ReadOperationResult readResults = null;

        while (pollForResult) {
            // Poll for result every second
            Thread.sleep(1000);
            readResults = vision.getReadResult(UUID.fromString(operationId));

            // The results will no longer be null when the service has finished processing the request.
            if (readResults != null) {
                // Get request status
                OperationStatusCodes status = readResults.status();

                if (status == OperationStatusCodes.FAILED || status == OperationStatusCodes.SUCCEEDED) {
                    pollForResult = false;
                }
            }
        }

        // Print read results, page per page
        StringBuilder builder = new StringBuilder();
        for (ReadResult pageResult : readResults.analyzeResult().readResults()) {
            System.out.println("");
            System.out.println("Printing Read results for page " + pageResult.page());
            for (Line line : pageResult.lines()) {
                builder.append(line.text());
                builder.append("\n");
            }
            System.out.println(builder.toString());
        }

        return builder.toString();
    }
}
