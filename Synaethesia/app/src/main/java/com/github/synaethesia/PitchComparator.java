package com.github.synaethesia;

import android.util.Log;

import java.util.Arrays;

class PitchComparator {

    static PitchDifference retrieveNote(float pitch) {
        Tuning tuning = MainActivity.getCurrentTuning();

        Note[] notes = tuning.getNotes();
        Arrays.sort(notes, (o1, o2) -> Float.compare(o1.getFrequency(), o2.getFrequency()));

        double minCentDifference = Float.POSITIVE_INFINITY;
        Note closest = notes[0];
        for (Note note : notes) {
            double centDifference = 1200d * log2(pitch / note.getFrequency());//pitch is the true frequency, use this!  //find where this came from

            if (Math.abs(centDifference) < Math.abs(minCentDifference)) {
                minCentDifference = centDifference;

                note.setMicFrequency(pitch);
                closest = note;
            }
        }

        //Log.d("Frequency: ", String.valueOf(pitch));
        return new PitchDifference(closest, minCentDifference);
    }

    private static double log2(float number) {
        return Math.log(number) / Math.log(2);
    }
}
