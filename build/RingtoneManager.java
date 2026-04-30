import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to manage the library and selection state.
 */
public class RingtoneManager {
    private List<Ringtone> library;
    private Ringtone currentRingtone;

    // Base directory where audio files live (set at runtime)
    private String audioBaseDir;

    public RingtoneManager(String audioBaseDir) {
        this.audioBaseDir = audioBaseDir;
        library = new ArrayList<>();
        loadDefaultRingtones();
        currentRingtone = library.get(0);
    }

    private void loadDefaultRingtones() {
        library.add(new Ringtone("Classic Digital",  "Retro",      15.0, audioBaseDir + "/classic_digital.wav"));
        library.add(new Ringtone("Summer Breeze",    "Nature",     30.0, audioBaseDir + "/summer_breeze.wav"));
        library.add(new Ringtone("Neon Nights",      "Electronic", 22.5, audioBaseDir + "/neon_nights.wav"));
        library.add(new Ringtone("Acoustic Strum",   "Guitar",     18.0, audioBaseDir + "/acoustic_strum.wav"));
        library.add(new Ringtone("Minimalist Chic",  "Modern",      5.0, audioBaseDir + "/minimalist_chic.wav"));
    }

    public List<Ringtone> getLibrary() { return library; }

    public void setCurrentRingtone(int index) {
        if (index >= 0 && index < library.size())
            this.currentRingtone = library.get(index);
    }

    public Ringtone getCurrentRingtone() { return currentRingtone; }
}