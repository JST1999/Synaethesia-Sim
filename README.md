# synaethesia-sim

### Intro
As part of Richard Abrahams's doctoral thesis on artificial synaesthesia, this application serves as a training tool for artificial synaesthetes to develop a pseudo-synaesthete musical practice by converting sound to colour.

The dominant sine of a complext sound signal is converted - using Ulf's Formula - to a corresponding colour. The bottom 30% of the screen is the hue of the nearest perfect half-step note, the top 70% is a direct conversion with brightness representing octave and hue representing intra-octave pitch. To participate in the research please contact:

Original program design - Richard Abrahams (richard.abrahams@plymouth.ac.uk)
Coding - Jason Tungay (jtungay@gmail.com)

### Privacy
This app records audio with audioRecord but cannot save or upload any audio file. This can be verified by checking the source code in this repository

### Download
The Android app can be found here https://play.google.com/store/apps/details?id=com.github.synaesthesia&hl=en_GB

### Libraries
The Tarsos DSP library (https://github.com/JorenSix/TarsosDSP) is used for pitch detection.
