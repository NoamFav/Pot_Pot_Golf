package com.pot_pot_golf.Core.Utils;

/**
 * A class that represents a timer for a button.
 * It is used to measure the time a button is pressed.
 */
@Deprecated
public class ButtonTimer {

    private long startTime;

    /**
     * Start the timer.
     */
    @Deprecated
    public void startTimer() {
        System.out.println("Timer started");
        startTime = System.nanoTime();
    }

    /**
     * Stop the timer.
     */
    @Deprecated
    public void stopTimer() {
        System.out.println("Timer stopped");
    }

    /**
     * Get the current elapsed time of the timer.
     *
     * @return The elapsed time of the timer in milliseconds.
     */
    @Deprecated
    public long getTime() {
        return (System.nanoTime() - startTime) / 1_000_000; // Convert nanoseconds to milliseconds
    }

    /**
     * Get the current elapsed time of the timer in a formatted string.
     *
     * @return The elapsed time of the timer in a formatted string.
     */
    @Deprecated
    public String getFormattedTime() {
        long elapsedTime = System.nanoTime() - startTime;
        double seconds = elapsedTime / 1_000_000_000.0; // Convert nanoseconds to seconds
        return String.format("%.3f s", seconds); // Format to 3 decimal places
    }
}