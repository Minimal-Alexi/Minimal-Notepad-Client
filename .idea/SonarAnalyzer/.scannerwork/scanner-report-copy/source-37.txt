package utils;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import javafx.scene.image.Image;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class GoogleDriveUploader {

    private static final String APPLICATION_NAME = "Drive Uploader";
    private static final List<String> SCOPES = Collections.singletonList("https://www.googleapis.com/auth/drive.file");
    String credentialsPath = "./src/main/resources/service-account.json";

    public String upload(String localFilePath) throws IOException {
        String targetFolderId = "1Pa1MjtBeTdZAMbQad4nWpNvae5xHnwmJ";

        // 1. Load the server credentials
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(SCOPES);

        // 2. Create Drive service instance
        Drive drive = new Drive.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // 3. Set the Metadata
        File fileMetadata = new File();
        fileMetadata.setName(new java.io.File(localFilePath).getName());
        fileMetadata.setParents(Collections.singletonList(targetFolderId));

        // 4. Prepare the content
        java.io.File localFile = new java.io.File(localFilePath);
        FileContent mediaContent = new FileContent("application/octet-stream", localFile);

        // 5. Upload
        try {
            File uploadFile = drive.files().create(fileMetadata, mediaContent)
                    .setFields("id, name, webViewLink")
                    .execute();
            System.out.println("Upload successful, file ID: " + uploadFile.getId());
            System.out.println("Web View Link: " + uploadFile.getWebViewLink());
            return uploadFile.getId();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public Image download(String googlePath) throws IOException {
        try {
            // 1. get credentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                            new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive.readonly"));

            Drive drive = new Drive.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("DriveImageLoader")
                    .build();

            // 2. Get the target image
            File imageFile = drive.files().list()
                    .setFields("files(id, name, webContentLink)")
                    .execute()
                    .getFiles()
                    .get(0);

            // 3. Download the image
            try (InputStream inputStream = drive.files()
                    .get(imageFile.getId())
                    .executeMediaAsInputStream()) {

                // 4. Transfer it to JavaFx image
                byte[] imageBytes = inputStream.readAllBytes();

                return new Image(new ByteArrayInputStream(imageBytes));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
