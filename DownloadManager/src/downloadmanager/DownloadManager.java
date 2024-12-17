package downloadmanager;

// Gerekli Swing kütüphaneleri ve diğer yardımcı araçlar
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*; 
import java.util.ArrayList;

// Dosya İndirme Yöneticisi, JFrame kullanılarak arayüz oluşturulur
public class DownloadManager extends JFrame {
    private JPanel linkPanel;
    private JButton addButton;
    private JButton downloadButton;
    private ArrayList<JTextField> linkFields; // Kullanıcı tarafından girilen bağlantıları tutar
    private ArrayList<JProgressBar> progressBars;
    private ArrayList<JLabel> fileLabels; // Her dosyanın adını göstermek için etiketler
    private ArrayList<JLabel> sizeLabels; // İndirilen boyut bilgilerini göstermek için etiketler
    private ArrayList<JLabel> timeLabels; // Kalan süreyi göstermek için etiketler
    private ArrayList<JLabel> speedLabels; // İndirme hızını göstermek için etiketler
    private ArrayList<DownloadTask> downloadTasks; // Her indirme için bir görev listesi
// Arayüz elemanlarının oluşturulması ve yapılandırılması
    public DownloadManager() {
        setTitle("File Download Manager");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Uygulama kapandığında program sonlanır
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(210, 220, 240));

        
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(180, 200, 230));
        JLabel titleLabel = new JLabel("Dosya İndirme Yöneticisi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        linkPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        linkPanel.setBackground(new Color(230, 240, 255));

        addButton = new JButton("Yeni Link Ekle");
        styleButton(addButton, new Color(100, 180, 200));

        downloadButton = new JButton("İndirmeyi Başlat");
        styleButton(downloadButton, new Color(120, 150, 220));

        linkFields = new ArrayList<>(); 
        progressBars = new ArrayList<>(); 
        fileLabels = new ArrayList<>(); // Her dosyanın adını göstermek için etiketler
        sizeLabels = new ArrayList<>(); 
        timeLabels = new ArrayList<>();
        speedLabels = new ArrayList<>();
        downloadTasks = new ArrayList<>(); // Her indirme için bir görev listesi

     // Butonlara tıklama olayları ekleme
        addButton.addActionListener(e -> addDownloadLink());
        downloadButton.addActionListener(e -> startDownloads());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(230, 240, 255));
        buttonPanel.add(addButton);
        buttonPanel.add(downloadButton);

        add(new JScrollPane(linkPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
    // Yeni bir indirme bağlantısı ekleme
    private void addDownloadLink() {
        JPanel singleLinkPanel = new JPanel();
        singleLinkPanel.setLayout(new BoxLayout(singleLinkPanel, BoxLayout.Y_AXIS));
        singleLinkPanel.setBackground(new Color(230, 240, 255));
        singleLinkPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField newLinkField = new JTextField(30); 
        JProgressBar newProgressBar = new JProgressBar();
        newProgressBar.setStringPainted(true);

        JLabel fileLabel = new JLabel("Dosya: -");
        JLabel sizeLabel = new JLabel("Boyut: 0 MB / 0 MB");
        JLabel timeLabel = new JLabel("Kalan Süre: -");
        JLabel speedLabel = new JLabel("Hız: -");

        fileLabels.add(fileLabel);
        sizeLabels.add(sizeLabel);
        timeLabels.add(timeLabel);
        speedLabels.add(speedLabel);

        JButton pauseButton = new JButton("Durdur");
        JButton resumeButton = new JButton("Devam");
        JButton cancelButton = new JButton("İptal");

        styleButton(pauseButton, new Color(220, 100, 100));
        styleButton(resumeButton, new Color(255, 210, 100));
        styleButton(cancelButton, new Color(150, 100, 180));

        int index = linkFields.size(); // Yeni bağlantının indeksini belirleme

        linkFields.add(newLinkField);
        progressBars.add(newProgressBar);

        singleLinkPanel.add(new JLabel("Dosya URL:"));
        singleLinkPanel.add(newLinkField);
        singleLinkPanel.add(fileLabel);
        singleLinkPanel.add(newProgressBar);
        singleLinkPanel.add(sizeLabel);
        singleLinkPanel.add(timeLabel);
        singleLinkPanel.add(speedLabel);

        JPanel actionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionButtonPanel.add(pauseButton);
        actionButtonPanel.add(resumeButton);
        actionButtonPanel.add(cancelButton);
        singleLinkPanel.add(actionButtonPanel);

        linkPanel.add(singleLinkPanel); // Ana panelde yeni bağlantı panelini ekleme
        linkPanel.revalidate(); // Bileşenin geçerli olduğunu belirtme ve bileşenin boyutunu güncelleme
        linkPanel.repaint(); //Bileşenin görsel olarak yeniden çizilmesini sağlama.

        pauseButton.addActionListener(e -> pauseDownload(index));
        resumeButton.addActionListener(e -> resumeDownload(index));
        cancelButton.addActionListener(e -> cancelDownload(index, singleLinkPanel));
    }

    // İndirme işlemini başlatma
    private void startDownloads() {
        // Tüm indirme bağlantılarını dolaşma
        for (int i = 0; i < linkFields.size(); i++) {
            String url = linkFields.get(i).getText();
            if (!url.isEmpty()) {
                JProgressBar progressBar = progressBars.get(i);
                JLabel fileLabel = fileLabels.get(i);
                JLabel sizeLabel = sizeLabels.get(i);
                JLabel timeLabel = timeLabels.get(i);
                JLabel speedLabel = speedLabels.get(i);
    
                
                JPanel parentPanel = (JPanel) linkPanel.getComponent(i); // İndirme bağlantılarını içeren ana panel
    
                // Yeni indirme görevi oluşturma
                DownloadTask downloadTask = new DownloadTask(url, progressBar, speedLabel, fileLabel, sizeLabel, timeLabel, parentPanel, linkPanel);
    
                // İndirme görevini listeye ekleme
                if (i < downloadTasks.size()) {
                    downloadTasks.set(i, downloadTask);
                } else {
                    downloadTasks.add(downloadTask);
                }
    
                // İndirme işlemi bir iş parçacığında başlatılır.
                Thread downloadThread = new Thread(downloadTask);
                downloadThread.start();
            }
        }
    }
    
    

    private void updateButtonListeners() {
        for (int i = 0; i < downloadTasks.size(); i++) {
            int currentIndex = i;
            DownloadTask task = downloadTasks.get(i);

            
            JPanel parentPanel = (JPanel) linkPanel.getComponent(i);
            JButton pauseButton = (JButton) ((JPanel) parentPanel.getComponent(parentPanel.getComponentCount() - 1)).getComponent(0);
            JButton resumeButton = (JButton) ((JPanel) parentPanel.getComponent(parentPanel.getComponentCount() - 1)).getComponent(1);
            JButton cancelButton = (JButton) ((JPanel) parentPanel.getComponent(parentPanel.getComponentCount() - 1)).getComponent(2);

            pauseButton.addActionListener(e -> task.pauseDownload());
            resumeButton.addActionListener(e -> task.resumeDownload());
            cancelButton.addActionListener(e -> cancelDownload(currentIndex, parentPanel));
        }
    }

    private void pauseDownload(int index) {
        if (index >= 0 && index < downloadTasks.size()) {
            downloadTasks.get(index).pauseDownload();
        }
    }

    private void resumeDownload(int index) {
        if (index >= 0 && index < downloadTasks.size()) {
            downloadTasks.get(index).resumeDownload();
        }
    }

    private void cancelDownload(int index, JPanel singleLinkPanel) {
        if (index >= 0 && index < downloadTasks.size()) {
            DownloadTask task = downloadTasks.get(index);
            task.cancelDownload();
            task.deleteFile();

            // İndirme bağlantısını ve ilgili bileşenleri kaldırma
            downloadTasks.remove(index);
            linkFields.remove(index);
            progressBars.remove(index);
            fileLabels.remove(index);
            sizeLabels.remove(index);
            timeLabels.remove(index);
            speedLabels.remove(index);

            linkPanel.remove(singleLinkPanel); // İlgili indirme bağlantısını ana panelden kaldırma
            linkPanel.revalidate(); // Bileşenin geçerli olduğunu belirtme ve bileşenin boyutunu güncelleme
            linkPanel.repaint(); //Bileşenin görsel olarak yeniden çizilmesini sağlama.

            updateButtonListeners();
        }
    }

    public static void main(String[] args) {
        // Uygulama başlatma
        SwingUtilities.invokeLater(() -> new DownloadManager().setVisible(true));
    }
}
