package com.github.synaethesia.tuning;

import com.github.synaethesia.Note;
import com.github.synaethesia.NoteName;
import com.github.synaethesia.Tuning;

import static com.github.synaethesia.NoteName.*;

public class ViolaTuning implements Tuning {

    @Override
    public Note[] getNotes() {
        return Pitch.values();
    }

    @Override
    public Note findNote(String name) {
        return Pitch.valueOf(name);
    }

    private enum Pitch implements Note {

        C3(C, 3, 130.81f),
        G3(G, 3, 196.00f),
        D4(D, 4, 293.66f),
        A4(A, 4, 440.00f);

        private final String sign;
        private final int octave;
        private final float frequency;
        private NoteName name;

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
