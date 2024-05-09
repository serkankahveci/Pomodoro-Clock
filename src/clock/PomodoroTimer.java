package clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class PomodoroTimer {
    private JFrame frame;
    private Timer timer;
    private JLabel timeLabel;
    private int timeInSeconds;
    private boolean isFocusTime;
    private int breakSessionCount; // To keep track of break sessions completed
    private boolean isLongBreakTime; // To indicate if it's long break time

    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_FOCUS_TIME = 5;
    private static final int MINUTES_IN_BREAK_TIME = 1;
    private static final int MINUTES_IN_LONG_BREAK_TIME = 10;
    private static final int BREAK_SESSIONS_BEFORE_LONG_BREAK = 4;

    public PomodoroTimer() {
        frame = new JFrame("Pomodoro Timer");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        timeLabel = new JLabel();
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(timeLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton decreaseButton = new JButton("-");
        decreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeInSeconds -= SECONDS_IN_MINUTE;
                updateTimeLabel();
            }
        });
        buttonPanel.add(decreaseButton);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startTimer();
            }
        });
        buttonPanel.add(startButton);

        JButton increaseButton = new JButton("+");
        increaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeInSeconds += SECONDS_IN_MINUTE;
                updateTimeLabel();
            }
        });
        buttonPanel.add(increaseButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(panel);
    }

    private void startTimer() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                timeInSeconds--;
                if (timeInSeconds >= 0) {
                    updateTimeLabel();
                } else {
                    stopTimer();
                    playAlarmSound();
                    if (isLongBreakTime) {
                        JOptionPane.showMessageDialog(frame, "Focus Time!");
                        timeInSeconds = MINUTES_IN_FOCUS_TIME * SECONDS_IN_MINUTE;
                        isLongBreakTime = false;
                    } else {
                        if (isFocusTime) {
                            JOptionPane.showMessageDialog(frame, "Break Time!");
                            timeInSeconds = MINUTES_IN_BREAK_TIME * SECONDS_IN_MINUTE;
                        } else {
                            breakSessionCount++;
                            if (breakSessionCount % BREAK_SESSIONS_BEFORE_LONG_BREAK == 0) {
                                JOptionPane.showMessageDialog(frame, "Long Break Time!");
                                timeInSeconds = MINUTES_IN_LONG_BREAK_TIME * SECONDS_IN_MINUTE;
                                isLongBreakTime = true;
                            } else {
                                JOptionPane.showMessageDialog(frame, "Focus Time!");
                                timeInSeconds = MINUTES_IN_FOCUS_TIME * SECONDS_IN_MINUTE;
                            }
                        }
                    }
                    isFocusTime = !isFocusTime;
                    updateTimeLabel();
                    startTimer();
                }
            }
        });
        timer.start();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void updateTimeLabel() {
        int hours = timeInSeconds / (SECONDS_IN_MINUTE * SECONDS_IN_MINUTE);
        int minutes = (timeInSeconds % (SECONDS_IN_MINUTE * SECONDS_IN_MINUTE)) / SECONDS_IN_MINUTE;
        int seconds = timeInSeconds % SECONDS_IN_MINUTE;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void playAlarmSound() {
        try {
            File file = new File("/Clock/Sound/alarm1.mp3");
            if (!file.exists()) {
                System.err.println("File not found: " + file.getAbsolutePath());
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error playing sound: " + e.getMessage(),
                    "Sound Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showGUI() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PomodoroTimer pomodoro = new PomodoroTimer();
                pomodoro.timeInSeconds = MINUTES_IN_FOCUS_TIME * SECONDS_IN_MINUTE; // Initial Focus Time: 5 minutes
                pomodoro.isFocusTime = true;
                pomodoro.breakSessionCount = 0; // Initialize break session count
                pomodoro.isLongBreakTime = false; // Initialize long break flag
                pomodoro.updateTimeLabel();
                pomodoro.showGUI();
            }
        });
    }
}
