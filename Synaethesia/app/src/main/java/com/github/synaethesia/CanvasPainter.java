package com.github.synaethesia;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.Log;

import java.util.Objects;

import androidx.core.content.ContextCompat;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

class CanvasPainter {

    private static final double TOLERANCE = 10D;
    private static final int MAX_DEVIATION = 60;
    private static final int NUMBER_OF_MARKS_PER_SIDE = 6;
    private final Context context;

    private Canvas canvas;

    private TextPaint textPaint = new TextPaint(ANTI_ALIAS_FLAG);
    private TextPaint numbersPaint = new TextPaint(ANTI_ALIAS_FLAG);
    private Paint gaugePaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint symbolPaint = new TextPaint(ANTI_ALIAS_FLAG);

    private int textColor;

    private int colC;
    private int colCS;
    private int colD;
    private int colDS;
    private int colE;
    private int colF;
    private int colFS;
    private int colG;
    private int colGS;
    private int colA;
    private int colAS;
    private int colB;
    private int colN;

    private PitchDifference pitchDifference;

    private float gaugeWidth;
    private float x;
    private float y;
    private boolean useScientificNotation;
    private int referencePitch;

    private CanvasPainter(Context context) {
        this.context = context;
    }

    static CanvasPainter with(Context context) {
        return new CanvasPainter(context);
    }

    CanvasPainter paint(PitchDifference pitchDifference) {
        this.pitchDifference = pitchDifference;

        return this;
    }

    void on(Canvas canvas) {
        SharedPreferences preferences = context.getSharedPreferences(
                MainActivity.PREFS_FILE, Context.MODE_PRIVATE);

        useScientificNotation = preferences.getBoolean(
                MainActivity.USE_SCIENTIFIC_NOTATION, true);

        referencePitch = preferences.getInt(
                MainActivity.REFERENCE_PITCH, 440);

        this.canvas = canvas;

        colC = R.color.redC;
        colCS = R.color.orangeCS;
        colD = R.color.yellowD;
        colDS = R.color.greenDS;
        colE = R.color.greenE;
        colF = R.color.greenF;
        colFS = R.color.cyanFS;
        colG = R.color.blueG;
        colGS = R.color.blueGS;
        colA = R.color.purpleA;
        colAS = R.color.pinkAS;
        colB = R.color.pinkB;
        colN = R.color.blackN;

        textColor = Color.BLACK;
        if (MainActivity.isDarkModeEnabled()) {
            int color = context.getResources().getColor(R.color.colorPrimaryDark);
            this.canvas.drawColor(color);

            textColor = context.getResources().getColor(R.color.colorTextDarkCanvas);
        }

        gaugeWidth = 0.45F * canvas.getWidth();
        x = canvas.getWidth() / 2F;
        y = canvas.getHeight() / 2F;

        textPaint.setColor(textColor);
        int textSize = context.getResources().getDimensionPixelSize(R.dimen.noteTextSize);
        textPaint.setTextSize(textSize);

        if (pitchDifference != null && Math.abs(getNearestDeviation()) <= MAX_DEVIATION) {
            setBackground(pitchDifference.closest);
        } else {
            drawListeningIndicator();
        }
    }

    private void drawListeningIndicator() {
        int resourceId = R.drawable.ic_line_style_icons_mic;

        if (ListenerFragment.IS_RECORDING) {
            resourceId = R.drawable.ic_line_style_icons_mic_active;
        }

        Drawable drawable = ContextCompat.getDrawable(context, resourceId);

        int x = (int) (canvas.getWidth() / 2F);
        int y = (int) (canvas.getHeight() - canvas.getHeight() / 3F);

        int width = Objects.requireNonNull(drawable).getIntrinsicWidth() * 2;
        int height = drawable.getIntrinsicHeight() * 2;
        drawable.setBounds(
                x - width / 2, y,
                x + width / 2,
                y + height);


        drawable.draw(canvas);
    }

    private String getNote(NoteName name) {
        if (useScientificNotation) {
            return name.getScientific();
        }

        return name.getSol();
    }

    private void setBackground(Note note) {
        int colour = 0;

        String noteText = getNote(note.getName());
        String sign = note.getSign();

        float freq = note.getMicFrequency();
        double logarithm = Math.log(freq) / Math.log(2);
        int integer = (int)logarithm;
        double real = logarithm - integer;

        double hue = (real - 0.03)*360;
        int brightness = integer*7;
        int saturation = 128 - brightness;
        if (saturation > 99) saturation = 100;

        //Log.d("HSV test 1: ", "Freq:"+freq+" Log:"+logarithm+" Int:"+integer+" Float:"+real+" Hue:"+hue+" Brightness:"+brightness+" Saturation:"+saturation);

        if (noteText.equals("C") && sign.equals("#")) {
            colour = colCS;
        } else if (noteText.equals("C")) {
            colour = colC;
        } else if (noteText.equals("D") && sign.equals("#")) {
            colour = colDS;
        } else if (noteText.equals("D")) {
            colour = colD;
        } else if (noteText.equals("E")) {
            colour = colE;
        } else if (noteText.equals("F") && sign.equals("#")) {
            colour = colFS;
        } else if (noteText.equals("F")) {
            colour = colF;
        } else if (noteText.equals("G") && sign.equals("#")) {
            colour = colGS;
        } else if (noteText.equals("G")) {
            colour = colG;
        } else if (noteText.equals("A") && sign.equals("#")) {
            colour = colAS;
        } else if (noteText.equals("A")) {
            colour = colA;
        } else if (noteText.equals("B")) {
            colour = colB;
        } else{
            colour = colN;
        }
        canvas.drawColor(context.getResources().getColor(colour));
    }

    private int getNearestDeviation() {
        float deviation = (float) pitchDifference.deviation;
        int rounded = Math.round(deviation);

        return Math.round(rounded / 10f) * 10;
    }
}