// =============================================================================
// FILE        : HabitTrackerApp.java
// DESCRIPTION : A streamlined student-level Desktop Habit Tracker (Java Swing).
// SYLLABUS    : [✓] Classes [✓] Interface [✓] Vector [✓] Exception Handling 
//               [✓] Multithreading [✓] GUI [✓] File I/O
// =============================================================================

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

interface Trackable {
    void markComplete();

    void resetStreak();

    String getStatus();
}

/** [Syllabus 2.1 - OOP/Encap] representing a single Habit. */
class Habit implements Trackable {
    private final String id, createdAt;
    private String title, category;
    private int streakCount;
    private final Set<LocalDate> completedDays;

    public Habit(String title, String category) {
        this(String.valueOf(System.currentTimeMillis()), title, category, 0,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), "");
    }

    public Habit(String id, String title, String category, int streak, String created, String daysCSV) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.streakCount = streak;
        this.createdAt = created;
        this.completedDays = Arrays.stream(daysCSV.split("\\|"))
                .filter(s -> !s.isEmpty()).map(LocalDate::parse).collect(Collectors.toSet());
    }

    @Override
    public void markComplete() {
        LocalDate today = LocalDate.now();
        if (completedDays.add(today)) {
            if (streakCount > 0 && !completedDays.contains(today.minusDays(1)))
                streakCount = 0;
            streakCount++;
        }
    }

    @Override
    public void resetStreak() {
        streakCount = 0;
        completedDays.clear();
    }

    @Override
    public String getStatus() {
        return completedDays.contains(LocalDate.now()) ? "✔ Done" : "○ Pending";
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setCategory(String c) {
        this.category = c;
    }

    public String toCSV() {
        return String.join("~", id, title, category, String.valueOf(streakCount), createdAt,
                completedDays.stream().map(LocalDate::toString).collect(Collectors.joining("|")));
    }

    public static Habit fromCSV(String line) {
        try {
            String[] p = line.split("~", -1);
            return new Habit(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4], p[5]);
        } catch (Exception e) {
            return null;
        }
    }
}

/** [Syllabus - File I/O] Handles saving and loading habits. */
class HabitFileManager {
    private static final String FILE = "habits_data.txt";

    public static void saveHabits(Vector<Habit> habits) {
        try {
            Files.write(Paths.get(FILE), habits.stream().map(Habit::toCSV).collect(Collectors.toList()));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save Error: " + e.getMessage());
        }
    }

    public static Vector<Habit> loadHabits() {
        Vector<Habit> habits = new Vector<>();
        try {
            if (Files.exists(Paths.get(FILE))) {
                Files.readAllLines(Paths.get(FILE)).forEach(l -> {
                    Habit h = Habit.fromCSV(l);
                    if (h != null)
                        habits.add(h);
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Load Error: " + e.getMessage());
        }
        return habits;
    }

    public static void exportReport(Vector<Habit> habits, Component parent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("habit_report.txt"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION)
            return;

        try (PrintWriter pw = new PrintWriter(chooser.getSelectedFile())) {
            pw.println("╔" + "═".repeat(54) + "╗");
            pw.println("║          HABIT TRACKER — PROGRESS REPORT           ║");
            pw.println("╚" + "═".repeat(54) + "╝");
            pw.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    + "\n");
            pw.printf("%-28s %-12s %-8s %-10s%n", "HABIT", "CATEGORY", "STREAK", "STATUS");
            pw.println("-".repeat(62));
            habits.forEach(h -> pw.printf("%-28s %-12s %-8d %-10s%n", h.getTitle(), h.getCategory(), h.getStreakCount(),
                    h.getStatus()));
            pw.println("\nTotal habits: " + habits.size());
            JOptionPane.showMessageDialog(parent, "Report saved!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Export failed: " + e.getMessage());
        }
    }
}

/** [Syllabus 4.2 - Multithreading] Background clock thread. */
class LiveClockThread extends Thread {
    private final JLabel clockLabel;
    private volatile boolean running = true;

    public LiveClockThread(JLabel label) {
        this.clockLabel = label;
        setDaemon(true);
    }

    @Override
    public void run() {
        while (running) {
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy | HH:mm:ss"));
            SwingUtilities.invokeLater(() -> clockLabel.setText("🕐 " + time));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }

    public void stopClock() {
        running = false;
        interrupt();
    }
}

/** [Swing - Custom Table Model] Backs the JTable with Vector<Habit>. */
class HabitTableModel extends AbstractTableModel {
    private static final String[] COLS = { "#", "Habit Title", "Category", "Streak 🔥", "Status", "Created" };
    private final Vector<Habit> habits;

    public HabitTableModel(Vector<Habit> habits) {
        this.habits = habits;
    }

    @Override
    public int getRowCount() {
        return habits.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int c) {
        return COLS[c];
    }

    @Override
    public Object getValueAt(int r, int c) {
        Habit h = habits.get(r);
        return switch (c) {
            case 0 -> r + 1;
            case 1 -> h.getTitle();
            case 2 -> h.getCategory();
            case 3 -> h.getStreakCount();
            case 4 -> h.getStatus();
            case 5 -> h.getCreatedAt();
            default -> "";
        };
    }
}

// =============================================================================
// CLASS: HabitTrackerApp (Main Frame / Entry Point)
//
// The top-level JFrame. Wires all components together:
// • TOP PANEL – input fields + "Add Habit" button
// • CENTER PANEL– JTable showing the habit Vector
// • BUTTON BAR – Mark Complete, Reset Streak, Delete, Export
// • FOOTER – LiveClockThread label + stats
// =============================================================================
/** [Syllabus 5.2 - Java Swing GUI] The main application frame. */
public class HabitTrackerApp extends JFrame {
    private final Vector<Habit> habitList = new Vector<>();
    private HabitTableModel tableModel;
    private JTable habitTable;
    private JTextField titleField;
    private JComboBox<String> categoryBox;
    private JLabel clockLabel, statsLabel;
    private LiveClockThread clockThread;

    private static final Color BG = new Color(18, 18, 30), PANEL = new Color(28, 28, 45),
            TEXT = new Color(235, 235, 245), ACCENT = new Color(130, 90, 230);

    public HabitTrackerApp() {
        super("Habit Tracker ◈ Java Edition");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(980, 680);
        setLocationRelativeTo(null);

        habitList.addAll(HabitFileManager.loadHabits());
        buildUI();
        clockThread = new LiveClockThread(clockLabel);
        clockThread.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                HabitFileManager.saveHabits(habitList);
                clockThread.stopClock();
                System.exit(0);
            }
        });
        setVisible(true);
    }

    private void buildUI() {
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        JPanel north = new JPanel(new BorderLayout());
        north.setBackground(BG);
        north.add(headerPanel(), BorderLayout.NORTH);
        north.add(inputPanel(), BorderLayout.CENTER);

        add(north, BorderLayout.NORTH);
        add(centerPanel(), BorderLayout.CENTER);
        add(footerPanel(), BorderLayout.SOUTH);
        updateStats();
    }

    private JPanel headerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(PANEL);
        p.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("◈ HABIT TRACKER");
        title.setFont(new Font("Monospaced", Font.BOLD, 22));
        title.setForeground(ACCENT);

        statsLabel = new JLabel();
        statsLabel.setForeground(TEXT.darker());
        p.add(title, BorderLayout.WEST);
        p.add(statsLabel, BorderLayout.EAST);
        return p;
    }

    private JPanel inputPanel() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        p.setBackground(BG);
        titleField = new JTextField(20);
        setupField(titleField, "Habit Name...");
        categoryBox = new JComboBox<>(new String[] { "Health", "Work", "Personal", "Other" });

        JButton addBtn = btn("＋ Add", ACCENT);
        addBtn.addActionListener(e -> addHabit());
        titleField.addActionListener(e -> addHabit());

        p.add(lbl("Name:"));
        p.add(titleField);
        p.add(lbl("Category:"));
        p.add(categoryBox);
        p.add(addBtn);
        return p;
    }

    private JPanel centerPanel() {
        tableModel = new HabitTableModel(habitList);
        habitTable = new JTable(tableModel);
        styleTable(habitTable);

        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(BG);
        p.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));
        p.add(new JScrollPane(habitTable), BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setBackground(BG);
        JButton mark = btn("✔ Complete", new Color(72, 199, 142)),
                reset = btn("↺ Reset", new Color(255, 165, 60)),
                del = btn("✕ Delete", new Color(240, 80, 80)),
                exp = btn("⬇ Export", new Color(60, 140, 200));

        mark.addActionListener(e -> action(h -> h.markComplete()));
        reset.addActionListener(e -> {
            if (confirm("Reset?"))
                action(h -> h.resetStreak());
        });
        del.addActionListener(e -> {
            if (confirm("Delete?")) {
                habitList.remove(habitTable.getSelectedRow());
                refresh();
            }
        });
        exp.addActionListener(e -> HabitFileManager.exportReport(habitList, this));

        btns.add(mark);
        btns.add(reset);
        btns.add(del);
        btns.add(exp);
        p.add(btns, BorderLayout.SOUTH);
        return p;
    }

    private JPanel footerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(15, 15, 25));
        p.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        clockLabel = new JLabel();
        clockLabel.setForeground(TEXT.darker());
        p.add(clockLabel, BorderLayout.WEST);
        return p;
    }

    private void addHabit() {
        String t = titleField.getText().trim();
        if (t.isEmpty() || t.equals("Habit Name..."))
            return;
        habitList.add(new Habit(t, (String) categoryBox.getSelectedItem()));
        titleField.setText("");
        refresh();
    }

    private void action(java.util.function.Consumer<Habit> act) {
        int r = habitTable.getSelectedRow();
        if (r >= 0) {
            act.accept(habitList.get(r));
            refresh();
        }
    }

    private void refresh() {
        HabitFileManager.saveHabits(habitList);
        tableModel.fireTableDataChanged();
        updateStats();
    }

    private void updateStats() {
        long done = habitList.stream().filter(h -> h.getStatus().contains("Done")).count();
        statsLabel.setText(habitList.size() + " habits | " + done + " done today");
    }

    private boolean confirm(String m) {
        return JOptionPane.showConfirmDialog(this, m, "Confirm", JOptionPane.YES_NO_OPTION) == 0;
    }

    // --- Styling Helpers ---
    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(TEXT);
        return l;
    }

    private JButton btn(String t, Color c) {
        JButton b = new JButton(t);
        b.setBackground(c);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return b;
    }

    private void setupField(JTextField f, String ph) {
        f.setBackground(PANEL);
        f.setForeground(TEXT);
        f.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        f.setText(ph);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(ph))
                    f.setText("");
            }

            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty())
                    f.setText(ph);
            }
        });
    }

    private void styleTable(JTable t) {
        t.setBackground(new Color(22, 22, 38));
        t.setForeground(TEXT);
        t.setRowHeight(30);
        t.getTableHeader().setBackground(PANEL);
        t.getTableHeader().setForeground(ACCENT);
        t.setSelectionBackground(ACCENT.darker());
        t.setShowGrid(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HabitTrackerApp::new);
    }
}
