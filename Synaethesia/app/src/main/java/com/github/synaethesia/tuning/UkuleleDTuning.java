package com.github.synaethesia.tuning;

import com.github.synaethesia.Note;
import com.github.synaethesia.NoteName;
import com.github.synaethesia.Tuning;

import static com.github.synaethesia.NoteName.*;

public class UkuleleDTuning implements Tuning {

    @Override
    public Note[] getNotes() {
        return Pitch.values();
    }

    @Override
    public Note findNote(String name) {
        return Pitch.valueOf(name);
    }

    private enum Pitch implements Note {

        A4(A, 4, 440f),
        D4(D, 4, 293.665f),
        F3_SHARP(F, 3, "#", 369.99f),
        B4(B, 4, 493.88f);

        private final String sign;
        private final int octave;
        private final float frequency;
        private NoteName name;

        Pitch(NoteName name, int octave, String sign, float frequency) {
            this.name = name;
            this.octave = octave;
            this.sign = sign;
            this.frequency = frequency;
        }

        Pitch(NoteName name, int octave, float frequency) {
            this.name = name;
            this.octave = octave;
            this.sign = "";
            this.frequency = frequency;
        }

        public NoteName getName() {
            return name;
        }

        public float getFrequency() {
            return frequency;
        }

        @Override
        public int getOctave() {
            return octave;
        }

        @Override
        public String getSign() {
            return sign;
        }
    }
}
