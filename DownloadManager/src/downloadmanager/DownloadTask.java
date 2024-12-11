package downloadmanager;

import javax.swing.*; // Swing bileşenlerini içerir
import java.io.*; // Dosya okuma ve yazma işlemleri için gerekli sınıfları içerir
import java.net.URL; 
import java.nio.file.Paths; 
import java.text.DecimalFormat; 
import java.util.concurrent.atomic.AtomicInteger; // İş parçası güvenliği için kullanılır

public class DownloadTask extends Thread {
    private String fileUrl;
    private JProgressBar progressBar;
    private JLabel speedLabel;
    private JLabel fileNameLabel;
    private JLabel sizeLabel;
    private JLabel timeLeftLabel;
    private JPanel parentPanel; // İlgili indirme bağlantısının kullanıcı arayüzü paneli
    private JPanel linkPanel; // Tüm indirme bağlantılarını içeren ana panel
    private volatile boolean isPaused = false;
    private volatile boolean isCancelled = false;
    private File downloadedFile; 

    //Gerekli bileşenleri ve indirme bilgilerini alma
    public DownloadTask(String fileUrl, JProgressBar progressBar, JLabel speedLabel, JLabel fileNameLabel, JLabel sizeLabel, JLabel timeLeftLabel, JPanel parentPanel, JPanel linkPanel) {
        this.fileUrl = fileUrl;
        this.progressBar = progressBar;
        this.speedLabel = speedLabel;
        this.fileNameLabel = fileNameLabel;
        this.sizeLabel = sizeLabel;
        this.timeLeftLabel = timeLeftLabel;
        this.parentPanel = parentPanel;
        this.linkPanel = linkPanel;
    }

    public void pauseDownload() {
        isPaused = true;
    }

    
    public void resumeDownload() {
        isPaused = false;
        synchronized (this) {
            notify();// İş parçacığını uyandırma
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
            URL url = new URL(fileUrl);
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            downloadedFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);

            // Dosya okuma ve yazma için akışlar oluşturma
            in = new BufferedInputStream(url.openStream());
            fileOutputStream = new FileOutputStream(downloadedFile);

            
            byte[] dataBuffer = new byte[1024];
            AtomicInteger totalBytesRead = new AtomicInteger(0);
            DecimalFormat df = new DecimalFormat("#.##");

            long lastUpdateTime = System.currentTimeMillis();
            long previousBytesRead = 0;
            int fileSize = url.openConnection().getContentLength();

            // Kullanıcı arayüzü etiketlerini güncelleme
            SwingUtilities.invokeLater(() -> {
                fileNameLabel.setText("Dosya: " + fileName);
                sizeLabel.setText("Boyut: 0 MB / " + df.format((double) fileSize / 1024 / 1024) + " MB");
                speedLabel.setText("Hız: 0 KB/s");
                timeLeftLabel.setText("Kalan Süre: -");
            });

            progressBar.setMaximum(fileSize);

            while (!isCancelled) {
                int bytesRead = in.read(dataBuffer, 0, dataBuffer.length);
                if (bytesRead == -1) break;

                synchronized (this) {
                    while (isPaused) {
                        wait();
                    }
                }

                fileOutputStream.write(dataBuffer, 0, bytesRead);
                totalBytesRead.addAndGet(bytesRead);
                progressBar.setValue(totalBytesRead.get());

                
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastUpdateTime >= 1000) {
                    long bytesInLastInterval = totalBytesRead.get() - previousBytesRead;
                    previousBytesRead = totalBytesRead.get();

                    double speed = (bytesInLastInterval / 1024.0); // KB/s
                    long timeLeft = (long) ((fileSize - totalBytesRead.get()) / (speed * 1024)); // saniye

                    // Kullanıcı arayüzü etiketlerini güncelleme
                    SwingUtilities.invokeLater(() -> {
                        speedLabel.setText("Hız: " + df.format(speed) + " KB/s");
                        sizeLabel.setText("Boyut: " + df.format((double) totalBytesRead.get() / 1024 / 1024) + " MB / " + df.format((double) fileSize / 1024 / 1024) + " MB");
                        timeLeftLabel.setText("Kalan Süre: " + (timeLeft > 0 ? timeLeft + " saniye" : "0 saniye"));
                    });

                    lastUpdateTime = currentTime;
                }
            }

            if (isCancelled) {
                
                closeStreams(fileOutputStream, in);
                deleteFile();

                
                SwingUtilities.invokeLater(() -> {
                    linkPanel.remove(parentPanel); // İptal edilen indirme bağlantısını arayüzden kaldırma
                    linkPanel.revalidate(); // Bileşenin geçerli olduğunu belirtme ve bileşenin boyutunu güncelleme
                    linkPanel.repaint(); //Bileşenin görsel olarak yeniden çizilmesini sağlama.
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    speedLabel.setText("Tamamlandı");
                    timeLeftLabel.setText("Kalan Süre: 0 saniye");
                });

                System.out.println("İndirme tamamlandı: " + fileUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeStreams(fileOutputStream, in);
        }
    }

    // Akışları güvenli bir şekilde kapatma
    private void closeStreams(FileOutputStream fileOutputStream, BufferedInputStream in) {
        try {
            if (fileOutputStream != null) fileOutputStream.close();
            if (in != null) in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // İndirme iptal edildiğinde dosyayı silme
    public void deleteFile() {
        try {
            URL url = new URL(fileUrl);
            String fileName = Paths.get(url.getPath()).getFileName().toString();
            File downloadedFile = new File(System.getProperty("user.home") + "/Downloads/" + fileName);
    
            if (downloadedFile.exists()) {
                if (downloadedFile.delete()) {
                    System.out.println("Dosya başarıyla silindi: " + downloadedFile.getAbsolutePath());
                } else {
                    System.out.println("Dosya silinemedi: " + downloadedFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.out.println("Dosya silinirken hata oluştu: " + e.getMessage());
        }
    }
}
    
