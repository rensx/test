package com.example;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import processing.core.PGraphics;
import processing.core.PImage;
import java.awt.*;
import java.awt.image.BufferedImage;

public class PxLatexObject extends PxPhysicsObject {
    private String latex;
    private int fontSize;
    private PImage latexImage;
    private float imgW, imgH;
    private MassType massType;

    public PxLatexObject(String latex, double x, double y, MassType massType, double restitution, int fontSize) {
        super(Geometry.createRectangle(1, 1), x, y, massType, restitution);
        this.latex = latex;
        this.fontSize = fontSize;
        this.massType = massType;
        this.setEnableFill(false);
        this.setEnableStroke(false);
        generateLatexImage();
    }

    private void generateLatexImage() {
        try {
            TeXFormula formula = new TeXFormula(latex);
            TeXIcon icon = formula.createTeXIcon(TeXFormula.SERIF, fontSize);
            BufferedImage img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setColor(new Color(0, 0, 0, 0));
            g2.fillRect(0, 0, img.getWidth(), img.getHeight());
            icon.paintIcon(null, g2, 0, 0);
            g2.dispose();

            imgW = img.getWidth();
            imgH = img.getHeight();
            latexImage = new PImage(img);

            double w = imgW;
            double h = imgH;
            this.body.removeAllFixtures();
            BodyFixture fixture = new BodyFixture(Geometry.createRectangle(w, h));
            fixture.setRestitution((float) this.getRestitution());
            this.body.addFixture(fixture);
            this.body.setMass(this.massType);
            System.out.println("Latex fixture size: " + w + "x" + h);
        } catch (Exception e) {
            System.err.println("Failed to generate LaTeX image: " + e.getMessage());
            e.printStackTrace();
            latexImage = null;
        }
    }

    @Override
    public void draw(PGraphics g) {
        if (latexImage == null) {
            System.err.println("PxLatexObject: latexImage is null");
            return;
        }

        Vector2 worldTranslation = body.getTransform().getTranslation();
        double worldRotation = body.getTransform().getRotationAngle();
        float screenX = (float) worldTranslation.x;
        float screenY = (float) worldTranslation.y;

        System.err.println("Drawing PxLatexObject at pixel pos: " + screenX + ", " + screenY);

        g.pushMatrix();
        try {
            g.translate(screenX, screenY);
            g.rotate((float) worldRotation);
            g.imageMode(PGraphics.CENTER);
            g.image(latexImage, 0, 0, imgW, imgH);
        } finally {
            g.popMatrix();
        }
    }

    public void setLatex(String latex) {
        this.latex = latex;
        generateLatexImage();
    }

    public String getLatex() {
        return latex;
    }
}