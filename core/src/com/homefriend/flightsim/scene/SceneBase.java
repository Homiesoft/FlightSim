package com.homefriend.flightsim.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;
import com.homefriend.flightsim.FlightSim;
import com.homefriend.flightsim.core.AssetTag;
import com.homefriend.flightsim.core.Consts;
import com.homefriend.flightsim.core.SkinTag;

public abstract class SceneBase implements Screen, ISceneBase{
    final FlightSim game;
    // Drawables
    InputMultiplexer multiplexer;
    // Background
    Sprite bg;
    // UI elements
    Skin skin;
    I18NBundle strings;
    // fps
    BitmapFont fpsTxt;
    float fpsTxtHeight;
    // pixel density
    float density;
    // music effect checker
    boolean mfxFlag;
    // center of the screen
    Vector2 centerPoint;

    SceneBase(FlightSim game){
        this.game = game;
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
    @Override
    public void resize(int width, int height){
        changeBg(game.get(width < height  ? AssetTag.Art_BG_Vert : AssetTag.Art_BG, Texture.class), width, height);
        fpsTxtHeight = height - fpsTxt.getLineHeight();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        this.density = game.getDensity();
        if(game.isLoaded(AssetTag.Art_Skin, Skin.class)){
            skin = game.get(AssetTag.Art_Skin, Skin.class);
            fpsTxt = skin.getFont(game.get(SkinTag.Font_Main));
            fpsTxt.setColor(Color.BLACK);
        }
        if(game.isLoaded(AssetTag.Data_Txt, I18NBundle.class)){
            strings = game.get(AssetTag.Data_Txt, I18NBundle.class);
        }
        // background
        multiplexer = new InputMultiplexer();
        mfxFlag = false;
        centerPoint = new Vector2(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        Gdx.input.setCatchBackKey(true);
    }

    // Main scene parts
    @Override
    public void changeBg(Texture bgTex, int width, int height){
        // background
        try{
            if(bg != null)
                bg.setTexture(bgTex);
            else
                bg = new Sprite(bgTex);
            bg.setSize(width, height);
        }
        catch(Exception e){
            bg = null;
        }
    }
    @Override
    public void drawBg(SpriteBatch batch){
        if(bg != null && batch.isDrawing()) bg.draw(batch);

    }
    @Override
    public void drawFps(SpriteBatch batch){
        if(batch == null || !batch.isDrawing()  || !Consts.DEBUG) return;
        String fps = "fps: " + Gdx.graphics.getFramesPerSecond();
        fpsTxt.draw(batch, fps, 5, fpsTxtHeight);
    }
    @Override
    public void playClick(){
        game.playSfx(AssetTag.Sfx_Click);
    }

    // Callbacks
    @Override
    public I18NBundle getStrings(){
        return strings;
    }
    @Override
    public Skin getSkin(){
        return skin;
    }
    @Override
    public void saveSettings() {
        game.saveSettings();
    }
    @Override
    public void pauseBgm() {
        game.pauseBgm();
    }
    @Override
    public void resumeLastBgm() {
        game.resumeLastBgm();
    }
    @Override
    public void setOrientationPortrait(){
        game.config.setOrientationPortrait();
    }
    @Override
    public void setOrientationLandscape(){
        game.config.setOrientationLandscape();
    }
    @Override
    public void prevScene(){
        game.prevScene();
    }
    @Override
    public boolean isMobileApp(){
        return game.isMobileApp();
    }
    @Override
    public String get(SkinTag tag){
        return game.get(tag);
    }
    @Override
    public void resumeGame(){

    }
    @Override
    public Vector2 pixelsToMeters(float x, float y, float ppm){
        final float w = Gdx.graphics.getWidth();
        final float h = Gdx.graphics.getHeight();
        float ratio = (w > h ? w / h: h / w) * ppm;
        return new Vector2(x / ppm , y / ppm);
    }

}
