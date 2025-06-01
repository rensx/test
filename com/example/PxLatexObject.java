package com.example;

import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
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
    private float imgW, imgH; // 物理单位（米）
private double scale;
    public PxLatexObject(String latex, double x, double y, MassType massType, double restitution, int fontSize, double scale) {
        super(Geometry.createRectangle(1, 1), x, y, massType); // 临时尺寸，后续调整
        this.latex = latex;
        this.fontSize = fontSize;
        this.scale = scale;

        generateLatexImage();

        // 用公式图片的像素尺寸设定物理碰撞体（以米为单位）
        double w = imgW / scale;
        double h = imgH / scale;
        this.body.removeAllFixtures();
        BodyFixture fixture = new BodyFixture(Geometry.createRectangle(w, h));
        fixture.setRestitution(restitution);
        this.body.addFixture(fixture);
        this.body.setMass(massType);
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

            // 记录像素尺寸
            imgW = img.getWidth();
            imgH = img.getHeight();

            // 转为 Processing PImage
            latexImage = new PImage(img);
        } catch (Exception e) {
            e.printStackTrace();
            latexImage = null;
        }
    }

    @Override
    public void draw(PGraphics g) {
        if (latexImage == null) return;

        float px = (float)(g.width / 2 + body.getTransform().getTranslationX() * scale);
        float py = (float)(g.height / 2 - body.getTransform().getTranslationY() * scale);
        float angle = (float)body.getTransform().getRotationAngle();

        g.pushMatrix();
        g.translate(px, py);
        g.rotate(-angle); // y轴反向

        // 居中贴图
        g.imageMode(PGraphics.CENTER);
        g.image(latexImage, 0, 0, imgW, imgH);

        g.popMatrix();
    }

    // 可选：支持动态切换 LaTeX 公式
    public void setLatex(String latex) {
        this.latex = latex;
        generateLatexImage();
    }

    // 可选：获取公式内容
    public String getLatex() {
        return latex;
    }
}