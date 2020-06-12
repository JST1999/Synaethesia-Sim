package com.github.synaethesia.tuning;

import com.github.synaethesia.Note;
import com.github.synaethesia.NoteName;
import com.github.synaethesia.Tuning;

import static com.github.synaethesia.NoteName.*;

public class CelloTuning implements Tuning {

    @Override
    public Note[] getNotes() {
        return Pitch.values();
    }

    @Override
    public Note findNote(String name) {
        return Pitch.valueOf(name);
    }

    private enum Pitch implements Note {

        C2(C, 2, 65.4f),
        G2(G, 2, 98f),
        D3(D, 3, 146.8f),
        A3(A, 3, 220f);

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
