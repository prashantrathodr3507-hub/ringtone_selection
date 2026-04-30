import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;

/**
 * Generates 5 distinct WAV ringtone files using sine-wave synthesis.
 * Run once to populate the audio/ folder.
 */
public class WavGenerator {

    private static final int SAMPLE_RATE = 44100;
    private static final int BITS         = 16;
    private static final int CHANNELS     = 1;

    public static void main(String[] args) throws Exception {
        String outDir = args.length > 0 ? args[0] : "audio";
        Files.createDirectories(Paths.get(outDir));

        generate_ClassicDigital (outDir + "/classic_digital.wav");
        generate_SummerBreeze   (outDir + "/summer_breeze.wav");
        generate_NeonNights     (outDir + "/neon_nights.wav");
        generate_AcousticStrum  (outDir + "/acoustic_strum.wav");
        generate_MinimalistChic (outDir + "/minimalist_chic.wav");

        System.out.println("All 5 ringtones generated in: " + outDir);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    /** Sine tone at given frequency for given seconds, at given amplitude 0-1 */
    private static double[] tone(double freq, double seconds, double amp) {
        int n = (int)(SAMPLE_RATE * seconds);
        double[] buf = new double[n];
        for (int i = 0; i < n; i++)
            buf[i] = amp * Math.sin(2 * Math.PI * freq * i / SAMPLE_RATE);
        return buf;
    }

    /** Silence of given seconds */
    private static double[] silence(double seconds) {
        return new double[(int)(SAMPLE_RATE * seconds)];
    }

    /** Concatenate multiple double[] arrays */
    private static double[] concat(double[]... parts) {
        int total = 0;
        for (double[] p : parts) total += p.length;
        double[] out = new double[total];
        int pos = 0;
        for (double[] p : parts) { System.arraycopy(p, 0, out, pos, p.length); pos += p.length; }
        return out;
    }

    /** Mix (add & clamp) two equal-length arrays */
    private static double[] mix(double[] a, double[] b) {
        int len = Math.min(a.length, b.length);
        double[] out = new double[len];
        for (int i = 0; i < len; i++) out[i] = Math.max(-1, Math.min(1, a[i] + b[i]));
        return out;
    }

    /** Apply linear fade-out to last `fadeSamples` samples */
    private static double[] fadeOut(double[] buf, int fadeSamples) {
        double[] out = buf.clone();
        int start = out.length - fadeSamples;
        for (int i = start; i < out.length; i++)
            out[i] *= (double)(out.length - i) / fadeSamples;
        return out;
    }

    /** Write double[] PCM data to a WAV file */
    private static void writeWav(double[] samples, String path) throws Exception {
        byte[] bytes = new byte[samples.length * 2];
        for (int i = 0; i < samples.length; i++) {
            short s = (short)(samples[i] * Short.MAX_VALUE);
            bytes[i * 2]     = (byte)(s & 0xFF);
            bytes[i * 2 + 1] = (byte)((s >> 8) & 0xFF);
        }
        AudioFormat fmt = new AudioFormat(SAMPLE_RATE, BITS, CHANNELS, true, false);
        AudioInputStream ais = new AudioInputStream(
            new ByteArrayInputStream(bytes), fmt, samples.length);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(path));
        System.out.println("  Written: " + path + " (" + samples.length / SAMPLE_RATE + "s)");
    }

    // ── ringtone generators ──────────────────────────────────────────────────

    /** Classic beep-beep phone ring — two 440 Hz bursts, repeated 3 times */
    static void generate_ClassicDigital(String path) throws Exception {
        double[] beep = tone(440, 0.4, 0.7);
        double[] gap  = silence(0.2);
        double[] pair = concat(beep, gap, beep, silence(0.6));
        double[] full = concat(pair, pair, pair);
        writeWav(fadeOut(full, SAMPLE_RATE / 4), path);
    }

    /** Summer Breeze — gentle ascending C-major arpeggio, looped softly */
    static void generate_SummerBreeze(String path) throws Exception {
        double[] c4  = tone(261.63, 0.3, 0.5);
        double[] e4  = tone(329.63, 0.3, 0.5);
        double[] g4  = tone(392.00, 0.3, 0.5);
        double[] c5  = tone(523.25, 0.6, 0.5);
        double[] gap = silence(0.15);
        double[] bar = concat(c4, gap, e4, gap, g4, gap, c5, silence(0.4));
        double[] full = concat(bar, bar, bar);
        writeWav(fadeOut(full, SAMPLE_RATE / 2), path);
    }

    /** Neon Nights — pulsing electronic tone with vibrato modulation */
    static void generate_NeonNights(String path) throws Exception {
        int totalSamples = (int)(SAMPLE_RATE * 5.0);
        double[] buf = new double[totalSamples];
        for (int i = 0; i < totalSamples; i++) {
            double t    = (double) i / SAMPLE_RATE;
            double lfo  = Math.sin(2 * Math.PI * 6.0 * t);          // 6 Hz LFO
            double freq = 300 + 60 * lfo;                            // freq swings 300-360 Hz
            double pulse = (Math.sin(2 * Math.PI * 4.0 * t) > 0) ? 0.7 : 0.0; // 4 Hz gate
            buf[i] = pulse * Math.sin(2 * Math.PI * freq * t);
        }
        writeWav(fadeOut(buf, SAMPLE_RATE / 2), path);
    }

    /** Acoustic Strum — simulate strum by layering E-minor chord partials */
    static void generate_AcousticStrum(String path) throws Exception {
        // E minor: E2, B2, E3, G3, B3, E4
        double[] freqs = {82.41, 123.47, 164.81, 196.00, 246.94, 329.63};
        int len = (int)(SAMPLE_RATE * 1.5);
        double[] chord = new double[len];
        for (int fi = 0; fi < freqs.length; fi++) {
            double f   = freqs[fi];
            double delay = fi * 0.02; // stagger each string by 20 ms
            for (int i = 0; i < len; i++) {
                double t = (double) i / SAMPLE_RATE - delay;
                if (t < 0) continue;
                double env = Math.exp(-2.5 * t);   // decay envelope
                chord[i] = Math.max(-1, Math.min(1, chord[i] + env * 0.18 * Math.sin(2 * Math.PI * f * t)));
            }
        }
        double[] gap  = silence(0.5);
        double[] full = concat(chord, gap, chord, gap, chord);
        writeWav(fadeOut(full, SAMPLE_RATE / 2), path);
    }

    /** Minimalist Chic — a clean short two-note ding */
    static void generate_MinimalistChic(String path) throws Exception {
        int len = (int)(SAMPLE_RATE * 0.8);
        double[] ding = new double[len];
        for (int i = 0; i < len; i++) {
            double t   = (double) i / SAMPLE_RATE;
            double env = Math.exp(-4.0 * t);
            ding[i]    = env * 0.7 * Math.sin(2 * Math.PI * 880 * t);   // A5
        }
        int len2 = (int)(SAMPLE_RATE * 0.8);
        double[] dong = new double[len2];
        for (int i = 0; i < len2; i++) {
            double t   = (double) i / SAMPLE_RATE;
            double env = Math.exp(-4.0 * t);
            dong[i]    = env * 0.7 * Math.sin(2 * Math.PI * 659.25 * t); // E5
        }
        double[] full = concat(ding, silence(0.1), dong, silence(0.3),
                               ding, silence(0.1), dong);
        writeWav(fadeOut(full, SAMPLE_RATE / 4), path);
    }
}
