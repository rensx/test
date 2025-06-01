package com.example;

import processing.core.PGraphics;
import processing.core.PFont;

import java.awt.Font;
public class PxStyledText extends PxObject {
    private String text;
    private float x, y; // Pixel coordinates
    private float fontSize = 24, rotation = 0;
    private int color = 0xff000000;
    private String fontName = "Arial";
    private boolean bold = false, italic = false;
    private PFont pFont;

    public PxStyledText(String text, float x, float y) {
        this.text = text;
        this.x = x; // Pixels
        this.y = y; // Pixels
    }

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
        System.err.println("Drawing PxStyledText at pixel pos: " + x + ", " + y);
        g.pushMatrix();
        try {
            g.translate(x, y);
            g.rotate((float) Math.toRadians(rotation));
            g.fill(color);
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

    public PxStyledText setColor(int c) { this.color = c; return this; }
    public PxStyledText setFontSize(float f) { this.fontSize = f; return this; }
    public PxStyledText setRotation(float r) { this.rotation = r; return this; }
    public PxStyledText setFontName(String name) { this.fontName = name; return this; }
    public PxStyledText setBold(boolean b) { this.bold = b; return this; }
    public PxStyledText setItalic(boolean i) { this.italic = i; return this; }



}