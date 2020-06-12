package com.github.synaethesia.tuning;

import com.github.synaethesia.Note;
import com.github.synaethesia.NoteName;
import com.github.synaethesia.Tuning;

import static com.github.synaethesia.NoteName.*;

public class BassTuning implements Tuning {

    @Override
    public Note[] getNotes() {
        return Pitch.values();
    }

    @Override
    public Note findNote(String name) {
        return Pitch.valueOf(name);
    }

    private enum Pitch implements Note {

        E1(E, 1, 41.204f),
        A1(A, 1, 55f),
        D2(D, 2, 73.416f),
        G2(G, 2, 97.999f);

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
