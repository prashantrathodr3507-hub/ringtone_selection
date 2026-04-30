/**
 * Model class representing a Ringtone.
 */
public class Ringtone {
    private String name;
    private String genre;
    private double duration;
    private String audioPath; // relative path to WAV file

    public Ringtone(String name, String genre, double duration, String audioPath) {
        this.name      = name;
        this.genre     = genre;
        this.duration  = duration;
        this.audioPath = audioPath;
    }

    // Getters
    public String getName()      { return name; }
    public String getGenre()     { return genre; }
    public double getDuration()  { return duration; }
    public String getAudioPath() { return audioPath; }

    @Override
    public String toString() {
        return name + " [" + genre + "] - " + duration + "s";
    }
}