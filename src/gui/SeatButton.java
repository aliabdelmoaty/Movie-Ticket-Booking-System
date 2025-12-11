package gui;

import javax.swing.*;
import java.awt.*;

public class SeatButton extends JButton {
    private String seatLabel;
    private boolean isOccupied;
    private boolean isSelected;
    private BookTicket parentFrame;
    
    public SeatButton(String seatLabel, boolean isOccupied, BookTicket parentFrame) {
        super(seatLabel);
        this.seatLabel = seatLabel;
        this.isOccupied = isOccupied;
        this.isSelected = false;
        this.parentFrame = parentFrame;
        
        setFont(new Font("Spline Sans", Font.BOLD, 11));
        setFocusPainted(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(50, 50));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isOccupied) {
            setBackground(new Color(220, 53, 69));
            setForeground(Color.WHITE);
            setEnabled(false);
        } else {
            setBackground(new Color(59, 67, 84));
            setForeground(Color.WHITE);
        }
        
        addActionListener(e -> toggleSeat());
    }
    
    private void toggleSeat() {
        if (isOccupied) return;
        
        isSelected = !isSelected;
        
        if (isSelected) {
            setBackground(new Color(19, 91, 236));
            parentFrame.addSelectedSeat(this);
        } else {
            setBackground(new Color(59, 67, 84));
            parentFrame.removeSelectedSeat(this);
        }
        
        parentFrame.updateBookingInfo();
    }
    
    public String getSeatLabel() {
        return seatLabel;
    }
    
    public boolean isOccupied() {
        return isOccupied;
    }
    
    public boolean isSelected() {
        return isSelected;
    }
}

