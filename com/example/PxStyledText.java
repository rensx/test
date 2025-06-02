package com.example;

import processing.core.PGraphics;
import processing.core.PFont;

import java.awt.Font;

public class PxStyledText extends PxObject {
    private String text;
    private float x, y, fontSize = 24, rotation = 0;
    private int color = 0xff000000;
    private String fontName = "Arial";
    private boolean bold = false, italic = false;
    private float scale = 100.0f;
    private PFont pFont; // Store the PFont

    public PxStyledText(String text, float x, float y) {
        this.text = text; this.x = x; this.y = y;
    }

    public PxStyledText setColor(int c) { this.color = c; return this; }
    public PxStyledText setFontSize(float f) { this.fontSize = f; return this; }
    public PxStyledText setRotation(float r) { this.rotation = r; return this; }
    public PxStyledText setFontName(String name) { this.fontName = name; return this; }
    public PxStyledText setBold(boolean b) { this.bold = b; return this; }
    public PxStyledText setItalic(boolean i) { this.italic = i; return this; }

    // Initialize the PFont when the object is created or when the font changes
    public void initFont(PGraphics g) {
        int style = Font.PLAIN;
        if (bold && italic) style = Font.BOLD | Font.ITALIC;
        else if (bold) style = Font.BOLD;
        else if (italic) style = Font.ITALIC;

        Font awtFont = new Font(fontName, style, Math.round(fontSize));
        pFont = new PFont(awtFont, true);
    }


    @Override
    public void draw(PGraphics g) {
        //if (!visible) return;
        g.pushMatrix();
        try {
            float screenX = g.width / 2f + x * scale;
            float screenY = g.height / 2f - y * scale;

             g.translate(screenX, screenY);
            g.rotate((float)Math.toRadians(rotation));
            g.fill(color);

            // Initialize font if it hasn't been already or if parameters have changed
            if (pFont == null) {
                initFont(g);
            }

            g.textFont(pFont, fontSize);
            g.textAlign(PGraphics.CENTER, PGraphics.CENTER);
            g.text(text, 0, 0);
        } finally {
            g.popMatrix();
        }
    }
    public float getBoundingSize(){throw new UnsupportedOperationException("Not supported yet.");}
public void update(){}

}