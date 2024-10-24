import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TwoDownloadManagerGUI {
    private JFrame frame;
    private JTextField urlField1;
    private JTextField urlField2;
    private JTextArea logArea;
    private JButton downloadButton;
    private JProgressBar progressBar1;
    private JProgressBar progressBar2;

    private ExecutorService executorService;

    public TwoDownloadManagerGUI() {
        initialize();
        executorService = Executors.newFixedThreadPool(2); // İki iş parçacığı için sabit havuz
    }

    private void initialize() {
        // Ana pencere
        frame = new JFrame("İki Dosya Eş Zamanlı İndirme Yöneticisi");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        // URL giriş alanları
        JPanel urlPanel = new JPanel();
        urlPanel.setLayout(new GridLayout(3, 2));

        urlPanel.add(new JLabel("1. Dosya URL'si:"));
        urlField1 = new JTextField();
        urlPanel.add(urlField1);

        urlPanel.add(new JLabel("2. Dosya URL'si:"));
        urlField2 = new JTextField();
        urlPanel.add(urlField2);

        // İndir butonu
        downloadButton = new JButton("İndir");
        urlPanel.add(downloadButton);

        // İndir butonuna tıklama işlemi
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url1 = urlField1.getText();
                String url2 = urlField2.getText();

                if (!url1.isEmpty() && !url2.isEmpty()) {
                    startDownloads(url1, url2);
                } else {
                    logArea.append("Lütfen iki URL de girin!\n");
                }
            }
        });

        frame.getContentPane().add(urlPanel, BorderLayout.NORTH);

        // İlerleme çubukları
        progressBar1 = new JProgressBar(0, 100);
        progressBar1.setStringPainted(true);
        progressBar2 = new JProgressBar(0, 100);
        progressBar2.setStringPainted(true);

        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new GridLayout(2, 1));
        progressPanel.add(progressBar1);
        progressPanel.add(progressBar2);

        frame.getContentPane().add(progressPanel, BorderLayout.CENTER);

        // Log alanı
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        frame.getContentPane().add(logScrollPane, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void startDownloads(String url1, String url2) {
        // 1. dosyanın kaydedileceği yol
        String userHome = System.getProperty("user.home");
        String downloadsDir = Paths.get(userHome, "Downloads").toString();
        String fileName1 = url1.substring(url1.lastIndexOf("/") + 1);
        String destination1 = Paths.get(downloadsDir, fileName1).toString();

        // 2. dosyanın kaydedileceği yol
        String fileName2 = url2.substring(url2.lastIndexOf("/") + 1);
        String destination2 = Paths.get(downloadsDir, fileName2).toString();

        logArea.append("1. ve 2. Dosya için indirme başlatıldı.\n");

        // 1. dosya için indirme işlemini bir iş parçacığında başlat
        executorService.submit(() -> downloadFile(url1, destination1, progressBar1, 1));

        // 2. dosya için indirme işlemini başka bir iş parçacığında başlat
        executorService.submit(() -> downloadFile(url2, destination2, progressBar2, 2));
    }

    private void downloadFile(String url, String destination, JProgressBar progressBar, int fileNumber) {
        // Burada dosya indirme işlemi gerçekleşir
        try {
            // İndirme işlemi ve ilerleme çubuğunun güncellenmesi
            DownloadManager downloadManager = new DownloadManager(1); // Tek iş parçacıklı havuz
            downloadManager.downloadFile(url, destination, new DownloadListener() {
                @Override
                public void onProgressUpdate(int progress) {
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }

                @Override
                public void onDownloadComplete(String destination) {
                    SwingUtilities.invokeLater(() -> logArea.append(fileNumber + ". Dosya indirildi: " + destination + "\n"));
                }

                @Override
                public void onDownloadError(String errorMessage) {
                    SwingUtilities.invokeLater(() -> logArea.append(fileNumber + ". Dosya indirme hatası: " + errorMessage + "\n"));
                }
            });
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> logArea.append(fileNumber + ". Dosya indirme hatası: " + e.getMessage() + "\n"));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TwoDownloadManagerGUI());
    }
}
