import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Main extends JFrame {
    private JPanel linkPanel;
    private JButton addButton;
    private JButton downloadButton;
    private ArrayList<JTextField> linkFields;

    public Main() {
        setTitle("File Download Manager");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        linkPanel = new JPanel();
        linkPanel.setLayout(new BoxLayout(linkPanel, BoxLayout.Y_AXIS));

        addButton = new JButton("Link Ekle");
        downloadButton = new JButton("İndir");

        linkFields = new ArrayList<>();

        // Link eklemek için buton
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newLinkField = new JTextField(30);
                linkFields.add(newLinkField);
                linkPanel.add(newLinkField);
                linkPanel.revalidate();
                linkPanel.repaint();
            }
        });

        // İndirme işlemini başlatmak için buton
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (JTextField linkField : linkFields) {
                    String url = linkField.getText();
                    if (!url.isEmpty()) {
                        String savePath = "downloaded_" + System.currentTimeMillis() + ".file";
                        new DownloadTask(url, savePath).start();
                    }
                }
            }
        });

        // Üst ve alt bileşenleri ekle
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(downloadButton);

        add(new JScrollPane(linkPanel), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Main().setVisible(true);
            }
        });
    }
}
