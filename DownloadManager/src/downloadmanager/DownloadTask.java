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
    private String fileUrl;
    private JProgressBar progressBar;
    private volatile boolean isPaused = false;  // Duraklatma kontrolü için
    private volatile boolean isCancelled = false; // İptal etme kontrolü için

    public DownloadTask(String fileUrl, JProgressBar progressBar) {
        this.fileUrl = fileUrl;
        this.progressBar = progressBar;
    }

    // Duraklatma ve Devam Ettirme
    public void pauseDownload() {
        isPaused = true;
    }

    public void resumeDownload() {
        isPaused = false;
        synchronized (this) {
            notify(); // Duraklatılan thread'i uyandır
        }
    }

    public void cancelDownload() {
        isCancelled = true;
    }
    
    @Override
    public void run() {
        BufferedInputStream in = null;
        FileOutputStream fileOutputStream = null;
    
        try {
            // URL bağlantısı ve dosya boyutu alma
            URL url = new URL(fileUrl);
            URLConnection connection = url.openConnection();
            int fileSize = connection.getContentLength();
    
            if (fileSize <= 0) {
                System.out.println("Dosya boyutu alınamadı: " + fileUrl);
                return;
            }
    
            progressBar.setMaximum(fileSize);
    
            // Dosyayı indirirken işlem
            in = new BufferedInputStream(connection.getInputStream());
            fileOutputStream = new FileOutputStream(
                    System.getProperty("user.home") + "/Downloads/" + Paths.get(url.getPath()).getFileName());
    
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
    
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                if (isCancelled) {
                    System.out.println("İndirme iptal edildi: " + fileUrl);
                    progressBar.setValue(0);  // İlerlemeyi sıfırla
                    break; // İptal edildiyse döngüyü hemen sonlandır
                }
    
                // Duraklatma işlemi
                synchronized (this) {
                    while (isPaused) {
                        wait();
                    }
                }
    
                // Dosyayı kaydet
                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                progressBar.setValue(totalBytesRead);  // İlerleme çubuğunu güncelle
            }
    
            System.out.println("İndirme tamamlandı: " + fileUrl);
        } catch (IOException | InterruptedException e) {
            System.out.println("İndirme sırasında hata oluştu: " + fileUrl);
            e.printStackTrace();
        } finally {
            // Kaynakları hızlıca serbest bırak
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Dosya kapatılırken hata oluştu: " + e.getMessage());
            }
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("Giriş akışı kapatılırken hata oluştu: " + e.getMessage());
            }
    
            // İptal sonrası dosyayı hemen sil
            try {
                if (isCancelled) {
                    java.io.File tempFile = new java.io.File(System.getProperty("user.home") + "/Downloads/" + Paths.get(new URL(fileUrl).getPath()).getFileName());
                    if (tempFile.exists()) {
                        if (tempFile.delete()) {
                            System.out.println("Yarım dosya silindi: " + fileUrl);
                        } else {
                            System.out.println("Dosya silinemedi: " + fileUrl);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Dosya silinirken hata oluştu: " + e.getMessage());
            }
        }
    }}
