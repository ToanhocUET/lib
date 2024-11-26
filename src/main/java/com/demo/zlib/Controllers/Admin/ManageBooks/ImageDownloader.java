package com.demo.zlib.Controllers.Admin.ManageBooks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownloader {

    public static String downloadImage(String imageUrl, String saveDirectory, String fileName) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();

                File directory = new File(saveDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                String filePath = saveDirectory + File.separator + fileName;

                try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                inputStream.close();
                return filePath;
            } else {
                System.out.println("Failed to download image. HTTP response code: " + connection.getResponseCode());
            }
        } catch (IOException e) {
            System.out.println("Error downloading image: " + e.getMessage());
        }
        return null;
    }
}

