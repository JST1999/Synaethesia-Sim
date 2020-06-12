package com.github.synaethesia;

public interface Note {

    NoteName getName();

    int getOctave();

    String getSign();

    float getFrequency();
}
