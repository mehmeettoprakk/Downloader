/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package downloadmanager;

/**
 *
 * @author mehme
 */
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;

public class DownloadTask extends Thread {
    private final String fileUrl;
    private final JProgressBar progressBar;

    public DownloadTask(String fileUrl, JProgressBar progressBar) {
        this.fileUrl = fileUrl;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();  // Dosya boyutunu al

            if (fileSize <= 0) {
                System.out.println("Dosya boyutu alınamadı: " + fileUrl);
                return;
            }

            progressBar.setMaximum(fileSize);  // İlerleme çubuğunun maksimum değerini ayarla

            try (BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(
                         System.getProperty("user.home") + "/Downloads/" + Paths.get(url.getPath()).getFileName())) {

                byte[] dataBuffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;

                while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    progressBar.setValue(totalBytesRead);  // İlerleme çubuğunu güncelle
                }
                System.out.println("Download completed: " + fileUrl);
            }

        } catch (IOException e) {
            System.out.println("Error downloading file: " + fileUrl);
            e.printStackTrace();
        }
    }
}


