package com.example;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class App extends JFrame {

    private static final String FOLDER_PATH = ".\\src\\main\\java\\com\\example\\testFile";

    private JTextArea textArea;
    private JComboBox<String> fileComboBox;
    private JButton saveButton;
    private JButton start;
    private JLabel statusLabel;
    private Timer autoSaveTimer;
    private String error;

    public App() {
        setTitle("Мини редактор кода");
        setSize(650, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        String[] files = loadTxtFilesFromDirectory();
        fileComboBox = new JComboBox<>(files);
        fileComboBox.addActionListener(e -> loadSelectedFile());

        saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> saveCurrentFile(false));

        start = new JButton("Запустить");
        start.addActionListener(e -> runCompiledCode());

        statusLabel = new JLabel(" Папка: " + FOLDER_PATH);

        bottomPanel.add(fileComboBox);
        bottomPanel.add(saveButton);
        bottomPanel.add(start);
        bottomPanel.add(statusLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        setupCtrlSHotkey();

        autoSaveTimer = new Timer(5000, e -> saveCurrentFile(true));
        autoSaveTimer.start();

        if (files.length > 0 && !files[0].startsWith("Нет доступных")) {
            loadSelectedFile();
        }
    }

    private void runCompiledCode() {
        String codeText = textArea.getText();

        saveCurrentFile(false);

        new Thread(() -> {
            try {
                error = CompilerManager.start(codeText);

                File classFile = new File("Main.class");
                if (!classFile.exists()) {
                    SwingUtilities.invokeLater(() -> {
                        showConsoleWindow("Ошибка: Файл Main.class не был создан компилятором.");
                    });
                    return;
                }

                ProcessBuilder processBuilder = new ProcessBuilder("java", "Main");
                Process process = processBuilder.start();

                java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8)
                );

                java.io.BufferedReader errorReader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8)
                );

                StringBuilder outputLog = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null)
                    outputLog.append(line).append("\n");

                StringBuilder errorLog = new StringBuilder();
                while ((line = errorReader.readLine()) != null)
                    errorLog.append(line).append("\n");

                process.waitFor();

                String finalResult;
                if (!errorLog.isEmpty()) finalResult = "--- Ошибка выполнения ---\n" + errorLog.toString();
                else if (outputLog.isEmpty()) finalResult = "[Программа успешно выполнилась, но ничего не вывела в консоль]";
                else finalResult = outputLog.toString();

                SwingUtilities.invokeLater(() -> showConsoleWindow(finalResult));

            } catch (IOException | InterruptedException ex) {
                SwingUtilities.invokeLater(() -> showConsoleWindow("Ошибка среды выполнения: " + ex.getMessage()));
            }
        }).start();
    }

    private void showConsoleWindow(String consoleText) {
        JDialog consoleDialog = new JDialog(this, "Консоль", true);
        consoleDialog.setSize(650, 450);
        consoleDialog.setLocationRelativeTo(this);
        consoleDialog.setLayout(new BorderLayout());

        JTextArea consoleTextArea = new JTextArea(error.equals("0") ? consoleText + "\nCode error 0" : error);
        consoleTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 16));
        consoleTextArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(consoleTextArea);
        consoleDialog.add(scrollPane, BorderLayout.CENTER);

        consoleDialog.setVisible(true);
    }

    private String[] loadTxtFilesFromDirectory() {
        File folder = new File(App.FOLDER_PATH);
        if (!folder.exists()) folder.mkdirs();

        File[] textFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (textFiles == null || textFiles.length == 0) return new String[]{"Нет доступных .txt файлов в папке"};

        String[] names = new String[textFiles.length];
        for (int i = 0; i < textFiles.length; i++)
            names[i] = textFiles[i].getName();

        return names;
    }

    private void loadSelectedFile() {
        String selectedFileName = (String) fileComboBox.getSelectedItem();
        if (selectedFileName == null || selectedFileName.startsWith("Нет доступных")) return;

        String fullPath = FOLDER_PATH + "/" + selectedFileName;
        try {
            String content = Files.readString(Paths.get(fullPath));
            textArea.setText(content);
            statusLabel.setText(" Открыт файл: " + selectedFileName);
        } catch (IOException e) {
            textArea.setText("Ошибка при чтении файла с диска: " + e.getMessage());
        }
    }

    private void saveCurrentFile(boolean isAutoSave) {
        String selectedFileName = (String) fileComboBox.getSelectedItem();
        if (selectedFileName == null || selectedFileName.startsWith("Нет доступных")) return;

        String fullPath = FOLDER_PATH + "/" + selectedFileName;
        try {
            Files.writeString(Paths.get(fullPath), textArea.getText());

            if (isAutoSave)
                statusLabel.setText(" [Автосохранение выполнено в " + java.time.LocalTime.now().toString().substring(0, 8) + "]");
            else statusLabel.setText(" Файл успешно сохранен вручную!");
        } catch (IOException e) {
            statusLabel.setText(" Ошибка записи файла: " + e.getMessage());
        }
    }

    private void setupCtrlSHotkey() {
        String actionKey = "saveAction";
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK);

        textArea.getInputMap().put(stroke, actionKey);
        textArea.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentFile(false);
                statusLabel.setText(" Сохранено комбинацией Ctrl + S");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}
