/**
 * Model class representing a Ringtone.
 */
public class Ringtone {
    private String name;
    private String genre;
    private double duration;

    public Ringtone(String name, String genre, double duration) {
        this.name = name;
        this.genre = genre;
        this.duration = duration;
    }

    // Getters
    public String getName() { return name; }
    public String getGenre() { return genre; }
    public double getDuration() { return duration; }

    @Override
    public String toString() {
        return name + " [" + genre + "] - " + duration + "s";
    }
}