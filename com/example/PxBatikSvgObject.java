package com.example;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;
import processing.core.PGraphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.Reader;
import java.io.InputStream;
import java.io.FileReader;

public class PxBatikSvgObject extends PxObject {
    private GraphicsNode svgGraphicsNode;
    private BridgeContext ctx;
    private SVGDocument doc;           // <-- 修正：添加为成员变量
    private float x, y, scale = 1.0f, rotation = 0;
    private double svgTime = 0.0; // SVG动画时间，单位秒

    public PxBatikSvgObject(Reader svgReader, float x, float y) {
        this.x = x; this.y = y;
        try {
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            doc = (SVGDocument) factory.createSVGDocument(null, svgReader);  // <-- 赋值给成员变量
            GVTBuilder builder = new GVTBuilder();
            ctx = new BridgeContext(new UserAgentAdapter());
            svgGraphicsNode = builder.build(ctx, doc);                       // <-- 使用成员变量
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PxBatikSvgObject(InputStream svgStream, float x, float y) {
        this(new java.io.InputStreamReader(svgStream), x, y);
    }

    public PxBatikSvgObject(String svgFilePath, float x, float y) throws java.io.FileNotFoundException {
        this(new FileReader(svgFilePath), x, y);
    }

    public PxBatikSvgObject setScale(float scale) { this.scale = scale; return this; }
    public PxBatikSvgObject setRotation(float angle) { this.rotation = angle; return this; }
    public PxBatikSvgObject setSvgTime(double t) { this.svgTime = t; return this; }
    public double getSvgTime() { return svgTime; }

    @Override
    public void update() {
        if (doc != null && doc.getDocumentElement() instanceof org.apache.batik.anim.dom.SVGOMSVGElement) {
            ((org.apache.batik.anim.dom.SVGOMSVGElement) doc.getDocumentElement()).setCurrentTime((float)svgTime);
            svgTime += 1.0 / 60.0; // 60fps
        }
    }

    @Override
    public void draw(PGraphics g) {
        if (svgGraphicsNode == null) return;
        if (!(g instanceof processing.awt.PGraphicsJava2D)) return;
        Graphics2D g2 = (Graphics2D) ((processing.awt.PGraphicsJava2D) g).g2;
        AffineTransform old = g2.getTransform();
        try {
            g2.translate(x, y);
            g2.rotate(Math.toRadians(rotation));
            g2.scale(scale, -scale);
            svgGraphicsNode.paint(g2);
        } finally {
            g2.setTransform(old);
        }
    }
public float getBoundingSize(){throw new UnsupportedOperationException("Not supported yet.");}
    
}