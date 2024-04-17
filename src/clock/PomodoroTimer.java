package clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PomodoroTimer {
    private JFrame frame;
    private Timer timer;
    private JLabel timeLabel;
    private int timeInSeconds;
    private boolean isFocusTime;

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
                timeInSeconds -= 60;
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
                timeInSeconds += 60;
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
                    if (isFocusTime) {
                        JOptionPane.showMessageDialog(frame, "Break Time!");
                        timeInSeconds = 60; // Break Time: 1 dakika
                        isFocusTime = false;
                    } else {
                        JOptionPane.showMessageDialog(frame, "Focus Time!");
                        timeInSeconds = 5 * 60; // Focus Time: 5 dakika
                        isFocusTime = true;
                    }
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
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;
        timeLabel.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    public void showGUI() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                PomodoroTimer pomodoro = new PomodoroTimer();
                pomodoro.timeInSeconds = 5 * 60; // Initial Focus Time: 5 minutes
                pomodoro.isFocusTime = true;
                pomodoro.updateTimeLabel();
                pomodoro.showGUI();
            }
        });
    }
}
