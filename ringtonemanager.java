import java.util.ArrayList;
import java.util.List;

/**
 * Controller class to manage the library and selection state.
 */
public class RingtoneManager {
    private List<Ringtone> library;
    private Ringtone currentRingtone;

    public RingtoneManager() {
        library = new ArrayList<>();
        loadDefaultRingtones();
        // Default selection
        currentRingtone = library.get(0);
    }

    private void loadDefaultRingtones() {
        library.add(new Ringtone("Classic Digital", "Retro", 15.0));
        library.add(new Ringtone("Summer Breeze", "Nature", 30.0));
        library.add(new Ringtone("Neon Nights", "Electronic", 22.5));
        library.add(new Ringtone("Acoustic Strum", "Guitar", 18.0));
        library.add(new Ringtone("Minimalist Chic", "Modern", 5.0));
    }

    public List<Ringtone> getLibrary() {
        return library;
    }

    public void setCurrentRingtone(int index) {
        if (index >= 0 && index < library.size()) {
            this.currentRingtone = library.get(index);
        }
    }

    public Ringtone getCurrentRingtone() {
        return currentRingtone;
    }
}