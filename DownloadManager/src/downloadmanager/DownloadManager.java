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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class DownloadManager extends JFrame {
    private final JPanel linkPanel;
    private final JButton addButton;
    private final JButton downloadButton;
    private ArrayList<JTextField> linkFields;
    private ArrayList<JProgressBar> progressBars;

    public DownloadManager() {
        setTitle("File Download Manager");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Başlık Paneli
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Dosya İndirme Yöneticisi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Açıklama Metni
        JPanel descriptionPanel = new JPanel();
        JLabel descriptionLabel = new JLabel("İndirmek istediğiniz dosya bağlantılarını girin ve 'Link Ekle' ile yeni bağlantılar ekleyin.");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionPanel.add(descriptionLabel);
        add(descriptionPanel, BorderLayout.NORTH);

        // Linklerin ve İlerleme Çubuklarının Paneli
        linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));
        linkPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        addButton = new JButton("Yeni Link Ekle");
        downloadButton = new JButton("İndirmeyi Başlat");

        addButton.setFont(new Font("Arial", Font.PLAIN, 16));
        downloadButton.setFont(new Font("Arial", Font.PLAIN, 16));

        linkFields = new ArrayList<>();
        progressBars = new ArrayList<>();

        // Link Ekle Butonu Aksiyonu
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel singleLinkPanel = new JPanel(new BorderLayout(5, 5));
                JTextField newLinkField = new JTextField(30);
                JLabel linkLabel = new JLabel("Dosya URL: ");
                linkLabel.setFont(new Font("Arial", Font.PLAIN, 14));

                JProgressBar newProgressBar = new JProgressBar();
                newProgressBar.setStringPainted(true);

                linkFields.add(newLinkField);
                progressBars.add(newProgressBar);

                singleLinkPanel.add(linkLabel, BorderLayout.WEST);
                singleLinkPanel.add(newLinkField, BorderLayout.CENTER);
                singleLinkPanel.add(newProgressBar, BorderLayout.SOUTH);

                linkPanel.add(singleLinkPanel);
                linkPanel.revalidate();
                linkPanel.repaint();
            }
        });

        // İndirmeyi Başlat Butonu Aksiyonu
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < linkFields.size(); i++) {
                    String url = linkFields.get(i).getText();
                    if (!url.isEmpty()) {
                        JProgressBar progressBar = progressBars.get(i);
                        DownloadTask downloadTask = new DownloadTask(url, progressBar);
                        downloadTask.start();  // Her indirme için yeni bir thread başlat
                    }
                }
            }
        });

        // Buton Paneli
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(downloadButton);

        add(new JScrollPane(linkPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DownloadManager().setVisible(true);
            }
        });
    }
}

