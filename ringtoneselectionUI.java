import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RingtoneSelectionUI extends JFrame {
    private RingtoneManager manager;
    private JList<Ringtone> ringtoneJList;
    private JLabel currentStatusLabel;

    public RingtoneSelectionUI() {
        manager = new RingtoneManager();
        initUI();
    }

    private void initUI() {
        // --- Window Setup ---
        setTitle("Ringtone Selection System");
        setSize(500, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));

        // --- Header Section ---
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(new Color(240, 240, 240));
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Device Sound Settings", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        
        currentStatusLabel = new JLabel("Active Ringtone: " + manager.getCurrentRingtone().getName(), SwingConstants.CENTER);
        currentStatusLabel.setForeground(new Color(0, 102, 204));
        
        headerPanel.add(titleLabel);
        headerPanel.add(currentStatusLabel);
        add(headerPanel, BorderLayout.NORTH);

        // --- List Section ---
        DefaultListModel<Ringtone> listModel = new DefaultListModel<>();
        for (Ringtone r : manager.getLibrary()) {
            listModel.addElement(r);
        }

        ringtoneJList = new JList<>(listModel);
        ringtoneJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ringtoneJList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(ringtoneJList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Ringtones"));
        add(scrollPane, BorderLayout.CENTER);

        // --- Button Section ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton previewBtn = new JButton("Preview Tone");
        JButton setBtn = new JButton("Set as Default");
        setBtn.setBackground(new Color(34, 139, 34));
        setBtn.setForeground(Color.WHITE);
        setBtn.setFocusPainted(false);

        // Action: Preview
        previewBtn.addActionListener(e -> {
            Ringtone selected = ringtoneJList.getSelectedValue();
            if (selected != null) {
                JOptionPane.showMessageDialog(this, 
                    "♪ Playing Preview: " + selected.getName() + " ♪\nDuration: " + selected.getDuration() + "s", 
                    "Ringtone Preview", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                showWarning();
            }
        });

        // Action: Set Ringtone
        setBtn.addActionListener(e -> {
            int index = ringtoneJList.getSelectedIndex();
            if (index != -1) {
                manager.setCurrentRingtone(index);
                Ringtone active = manager.getCurrentRingtone();
                currentStatusLabel.setText("Active Ringtone: " + active.getName());
                JOptionPane.showMessageDialog(this, "Success! Ringtone updated.");
            } else {
                showWarning();
            }
        });

        buttonPanel.add(previewBtn);
        buttonPanel.add(setBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void showWarning() {
        JOptionPane.showMessageDialog(this, "Please select a ringtone from the list first.", "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        // Set Look and Feel to System Default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(RingtoneSelectionUI::new);
    }
}