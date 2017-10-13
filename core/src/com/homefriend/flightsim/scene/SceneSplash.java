package com.homefriend.flightsim.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.homefriend.flightsim.FlightSim;
import com.homefriend.flightsim.core.AssetTag;
import com.homefriend.flightsim.core.Data;

public class SceneSplash extends SceneBase {
    private Stage ui;
    private SpriteBatch batch;
    private Label loadingLbl, loadPercent;
    private float percent;
    public SceneSplash(FlightSim game) {
        super(game);
    }

    @Override
    public void resize(int width, int height){
        super.resize(width, height);
        ui.clear();
        // camera control
        Vector2 dim = pixelsToMeters(width, height, density);
        ui.getViewport().setWorldSize(width, height);
        ui.getViewport().update(width, height, true);
        ui.getCamera().viewportWidth = dim.x;
        ui.getCamera().viewportHeight = dim.y;
        ui.getCamera().position.set(dim.x/2f, dim.y/2f, 0);
        batch.setProjectionMatrix(ui.getCamera().combined);
        // end camera control
        batch.flush();
    }
    @Override
    public void show(){
        game.preload();
        game.assets.finishLoading();
        super.show();
        fpsTxt = game.get(AssetTag.Font_Main, BitmapFont.class);
        fpsTxt.setColor(Color.BLACK);
        // background
        ui = new Stage();
        batch = new SpriteBatch();
        percent = 0;
        game.load();
        Data.load();
    }

    @Override
    public void render(float delta){
        if(game.assets.update()){
            game.setup();
            game.loadSettings();
            game.nextScene(new SceneMainMenu(game));
        }
        super.render(delta);
        batch.begin();
        drawBg(batch);
        drawFps(batch);
        String loadTxt = String.format("%s\n%d%%", strings.get("UI_LOADING"),(int)(percent * 100));
        fpsTxt.draw(batch, loadTxt, centerPoint.x, centerPoint.y,0, Align.center, true);
        batch.end();
        percent = game.assets.getProgress();
        ui.act(delta);
        ui.draw();
    }

}
