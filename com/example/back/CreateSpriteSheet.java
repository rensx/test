package com.example;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class CreateSpriteSheet {
    public static void main(String[] args) {
        int frameWidth = 32;
        int frameHeight = 32;
        int columns = 4;
        BufferedImage spriteSheet = new BufferedImage(frameWidth * columns, frameHeight, BufferedImage.TYPE_INT_ARGB);

        // Create frames
        int[] colors = {0xFF00FF00, 0xFF0000FF, 0xFFFF0000, 0xFFFFFF00}; // Green, Blue, Red, Yellow
        for (int i = 0; i < columns; i++) {
            for (int x = 0; x < frameWidth; x++) {
                for (int y = 0; y < frameHeight; y++) {
                    spriteSheet.setRGB(i * frameWidth + x, y, colors[i]);
                }
            }
        }

        // Save sprite sheet
        try {
            ImageIO.write(spriteSheet, "png", new File("spritesheet.png"));
            System.out.println("Sprite sheet created: src/main/resources/spritesheet.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}