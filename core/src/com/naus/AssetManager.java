package com.naus;

import com.badlogic.gdx.graphics.Texture;

public class AssetManager {

    public static Texture nauImage;
    public static Texture asteroideImage;
    public static Texture starImage;

    private final static int WIDTH = 800;
    private final static int HEIGHT = 480;

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public static void load() {
        nauImage = new Texture("nau.png");
        asteroideImage = new Texture("asteroide.png");
        starImage = new Texture("star.png");
    }

    public static void dispose() {
        nauImage.dispose();
        asteroideImage.dispose();
        starImage.dispose();
    }
}