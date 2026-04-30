import javax.swing.*;
import javax.swing.border.*;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class RingtoneSelectionUI extends JFrame {

    private RingtoneManager manager;
    private JList<Ringtone>  ringtoneJList;
    private JLabel           currentStatusLabel;
    private JLabel           nowPlayingLabel;

    // Audio playback state
    private Clip        activeClip   = null;
    private int         playingIndex = -1;

    // ── constructor ──────────────────────────────────────────────────────────

    public RingtoneSelectionUI(String audioBaseDir) {
        manager = new RingtoneManager(audioBaseDir);
        initUI();
    }

    // ── UI setup ─────────────────────────────────────────────────────────────

    private void initUI() {
        setTitle("Ringtone Selection System");
        setSize(520, 460);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 250));

        // ── Header ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 2));
        header.setBackground(new Color(30, 30, 60));
        header.setBorder(new EmptyBorder(14, 16, 14, 16));

        JLabel title = new JLabel("Device Sound Settings", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        currentStatusLabel = new JLabel(
            "Active: " + manager.getCurrentRingtone().getName(), SwingConstants.CENTER);
        currentStatusLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        currentStatusLabel.setForeground(new Color(150, 200, 255));

        nowPlayingLabel = new JLabel("", SwingConstants.CENTER);
        nowPlayingLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        nowPlayingLabel.setForeground(new Color(100, 255, 160));

        header.add(title);
        header.add(currentStatusLabel);
        header.add(nowPlayingLabel);
        add(header, BorderLayout.NORTH);

        // ── List ────────────────────────────────────────────────────────────
        DefaultListModel<Ringtone> model = new DefaultListModel<>();
        for (Ringtone r : manager.getLibrary()) model.addElement(r);

        ringtoneJList = new JList<>(model);
        ringtoneJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ringtoneJList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        ringtoneJList.setBackground(new Color(255, 255, 255));
        ringtoneJList.setFixedCellHeight(38);
        ringtoneJList.setBorder(new EmptyBorder(4, 10, 4, 10));

        // ▶ Play on single click
        ringtoneJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = ringtoneJList.getSelectedIndex();
                if (idx >= 0) playRingtone(idx);
            }
        });

        JScrollPane scroll = new JScrollPane(ringtoneJList);
        scroll.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 220), 1),
            "  Available Ringtones  ",
            TitledBorder.LEFT, TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(60, 60, 120)));
        scroll.setBackground(Color.WHITE);
        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(new EmptyBorder(6, 10, 4, 10));
        center.setBackground(new Color(245, 245, 250));
        center.add(scroll);
        add(center, BorderLayout.CENTER);

        // ── Buttons ─────────────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 10));
        btnPanel.setBackground(new Color(245, 245, 250));

        JButton stopBtn = makeButton("[ ] Stop",    new Color(180, 60, 60));
        JButton setBtn  = makeButton("Set as Default", new Color(34, 139, 34));

        stopBtn.addActionListener(e -> stopPlayback());

        setBtn.addActionListener(e -> {
            int idx = ringtoneJList.getSelectedIndex();
            if (idx < 0) { warn(); return; }
            manager.setCurrentRingtone(idx);
            currentStatusLabel.setText("Active: " + manager.getCurrentRingtone().getName());
            JOptionPane.showMessageDialog(this,
                "\"" + manager.getCurrentRingtone().getName() + "\" set as default ringtone!",
                "Ringtone Updated", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(stopBtn);
        btnPanel.add(setBtn);

        JLabel hint = new JLabel("Click a ringtone to preview it");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        hint.setForeground(new Color(140, 140, 160));
        JPanel south = new JPanel(new BorderLayout());
        south.setBackground(new Color(245, 245, 250));
        south.add(btnPanel, BorderLayout.CENTER);
        south.add(hint, BorderLayout.SOUTH);
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        south.setBorder(new EmptyBorder(0, 0, 8, 0));
        add(south, BorderLayout.SOUTH);

        // Stop audio when window closes
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { stopPlayback(); }
        });

        setVisible(true);
    }

    // ── audio playback ───────────────────────────────────────────────────────

    private void playRingtone(int index) {
        stopPlayback();   // stop whatever is playing

        Ringtone r = manager.getLibrary().get(index);
        File wavFile = new File(r.getAudioPath());

        if (!wavFile.exists()) {
            nowPlayingLabel.setText("[!] Audio file not found: " + wavFile.getName());
            return;
        }

        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
            activeClip   = AudioSystem.getClip();
            activeClip.open(ais);
            activeClip.start();
            playingIndex = index;
            nowPlayingLabel.setText(">> Now playing: " + r.getName());

            // Clear label when clip finishes
            activeClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    SwingUtilities.invokeLater(() -> {
                        if (playingIndex == index)
                            nowPlayingLabel.setText("");
                    });
                }
            });
        } catch (Exception ex) {
            nowPlayingLabel.setText("[!] Playback error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void stopPlayback() {
        if (activeClip != null && activeClip.isRunning()) {
            activeClip.stop();
        }
        if (activeClip != null) {
            activeClip.close();
            activeClip = null;
        }
        playingIndex = -1;
        SwingUtilities.invokeLater(() -> nowPlayingLabel.setText(""));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void warn() {
        JOptionPane.showMessageDialog(this,
            "Please select a ringtone from the list first.",
            "No Selection", JOptionPane.WARNING_MESSAGE);
    }

    // ── main ─────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Resolve audio dir relative to the JAR / class location
        String audioDir = args.length > 0 ? args[0]
            : System.getProperty("user.dir") + "/audio";

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) {}

        String finalAudioDir = audioDir;
        SwingUtilities.invokeLater(() -> new RingtoneSelectionUI(finalAudioDir));
    }
}