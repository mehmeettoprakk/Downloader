import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

interface DownloadListener {
    void onProgressUpdate(int progress);
    void onDownloadComplete(String destination);
    void onDownloadError(String errorMessage);
}

class DownloadTask implements Runnable {
    private final String url;
    private final String destination;
    private final DownloadListener listener;

    public DownloadTask(String url, String destination, DownloadListener listener) {
        this.url = url;
        this.destination = destination;
        this.listener = listener;
    }

    @Override
    public void run() {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            int fileSize = new URL(url).openConnection().getContentLength();

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                totalBytesRead += bytesRead;
                fileOutputStream.write(dataBuffer, 0, bytesRead);

                // İlerleme yüzdesini hesapla
                int progress = (int) ((totalBytesRead * 100.0) / fileSize);
                listener.onProgressUpdate(progress);
            }

            listener.onDownloadComplete(destination);
        } catch (IOException e) {
            listener.onDownloadError(e.getMessage());
        }
    }
}

class DownloadManager {
    private final ExecutorService executorService;

    public DownloadManager(int threadPoolSize) {
        // Thread havuzunu başlatıyoruz
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void downloadFile(String url, String destination, DownloadListener listener) {
        // Her indirme işlemi için ayrı bir iş parçacığı oluşturuluyor
        DownloadTask downloadTask = new DownloadTask(url, destination, listener);
        executorService.execute(downloadTask);
    }

    public void shutdown() {
        executorService.shutdown(); // İş parçacıkları havuzunu kapatıyoruz
    }
}
