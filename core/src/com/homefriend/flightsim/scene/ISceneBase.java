package com.homefriend.flightsim.scene;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.homefriend.flightsim.core.SkinTag;

public interface ISceneBase {
    void changeBg(Texture bgTex, int width, int height);
    void drawBg(SpriteBatch batch);
    void drawFps(SpriteBatch batch);
    void playClick();
    I18NBundle getStrings();
    Skin getSkin();
    void saveSettings();
    void pauseBgm();
    void resumeLastBgm();
    void setOrientationPortrait();
    void setOrientationLandscape();
    void prevScene();
    boolean isMobileApp();
    String get(SkinTag tag);
    void resumeGame();
    Vector2 pixelsToMeters(float x, float y, float ppm);
}
