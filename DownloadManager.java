import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {
    private ExecutorService executor;
    private List<DownloadTask> downloadTasks;

    // Constructor
    public DownloadManager(int numberOfThreads) {
        executor = Executors.newFixedThreadPool(numberOfThreads); // İş parçacığı havuzu
        downloadTasks = new ArrayList<>();
    }

    // İndirme işlemini başlat
    public void downloadFile(String fileUrl, String destination, DownloadListener listener) {
        DownloadTask task = new DownloadTask(fileUrl, destination, listener);
        downloadTasks.add(task);
        executor.submit(task); // İş parçacığını başlat
    }

    // Tüm indirmeler bitince iş parçacıklarını kapat
    public void shutdown() {
        executor.shutdown();
    }
}
