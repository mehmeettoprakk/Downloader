package downloadmanager;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author mehme
 */
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;

public class DownloadManager extends JFrame {
    private JPanel linkPanel;
    private JButton addButton;
    private JButton downloadButton;
    private ArrayList<JTextField> linkFields;
    private ArrayList<JProgressBar> progressBars;
    private ArrayList<DownloadTask> downloadTasks;

    public DownloadManager() {
        setTitle("File Download Manager");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(210, 220, 240));  // Açık mavi arka plan rengi

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(180, 200, 230));  // Başlık paneli için daha açık bir mavi
        JLabel titleLabel = new JLabel("Dosya İndirme Yöneticisi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50)); // Daha koyu gri tonlu yazı rengi
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        linkPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        linkPanel.setBackground(new Color(230, 240, 255)); // Link paneli için çok açık mavi

        addButton = new JButton("Yeni Link Ekle");
        styleButton(addButton, new Color(100, 180, 200));
        
        downloadButton = new JButton("İndirmeyi Başlat");
        styleButton(downloadButton, new Color(120, 150, 220));

        linkFields = new ArrayList<>();
        progressBars = new ArrayList<>();
        downloadTasks = new ArrayList<>();

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

    private void addDownloadLink() {
        JPanel singleLinkPanel = new JPanel(new BorderLayout(5, 5));
        singleLinkPanel.setBackground(new Color(230, 240, 255));
        
        JTextField newLinkField = new JTextField(30);
        JProgressBar newProgressBar = new JProgressBar();
        newProgressBar.setStringPainted(true);

        JButton pauseButton = new JButton("Durdur");
        JButton resumeButton = new JButton("Devam");
        JButton cancelButton = new JButton("İptal");

        styleButton(pauseButton, new Color(220, 100, 100));
        styleButton(resumeButton, new Color(255, 210, 100));
        styleButton(cancelButton, new Color(150, 100, 180));

        linkFields.add(newLinkField);
        progressBars.add(newProgressBar);

        singleLinkPanel.add(new JLabel("Dosya URL: "), BorderLayout.WEST);
        singleLinkPanel.add(newLinkField, BorderLayout.CENTER);
        singleLinkPanel.add(newProgressBar, BorderLayout.SOUTH);

        JPanel actionButtonPanel = new JPanel(new FlowLayout());
        actionButtonPanel.setBackground(new Color(230, 240, 255));
        actionButtonPanel.add(pauseButton);
        actionButtonPanel.add(resumeButton);
        actionButtonPanel.add(cancelButton);
        singleLinkPanel.add(actionButtonPanel, BorderLayout.EAST);

        linkPanel.add(singleLinkPanel);
        linkPanel.revalidate();
        linkPanel.repaint();

        pauseButton.addActionListener(e -> pauseDownload(linkFields.size() - 1));
        resumeButton.addActionListener(e -> resumeDownload(linkFields.size() - 1));
        cancelButton.addActionListener(e -> cancelDownload(linkFields.size() - 1));
    }

    private void startDownloads() {
        for (int i = 0; i < linkFields.size(); i++) {
            String url = linkFields.get(i).getText();
            if (!url.isEmpty()) {
                JProgressBar progressBar = progressBars.get(i);
                DownloadTask downloadTask = new DownloadTask(url, progressBar);
                downloadTasks.add(downloadTask);
                downloadTask.start();
            }
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

    private void cancelDownload(int index) {
        if (index >= 0 && index < downloadTasks.size()) {
            downloadTasks.get(index).cancelDownload();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DownloadManager().setVisible(true));
    }
}




