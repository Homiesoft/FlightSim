package com.homefriend.flightsim.scene;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.homefriend.flightsim.FlightSim;
import com.homefriend.flightsim.core.AssetTag;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class SceneMainMenu extends SceneBase {
    private Stage ui;
    private SpriteBatch batch;
    private Image logo;
    private boolean isAnim;
    private Button start, opts, quit;

    public SceneMainMenu(FlightSim game) {
        super(game);
    }

    private void initUI(){
        // Logo
        start.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                //Stats.inst.init(game);
                //LevelStats.clear();
                //game.stopBgm(AssetTag.Bgm_Theme);
                game.nextScene(new SceneGame(game));
            }
        });
        opts.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                //game.nextScene(new SceneOptions(game));
            }
        });
        quit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                playClick();
                quit();
            }
        });

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
        ui.addActor(logo);
        logo.setPosition(dim.x/2 - logo.getPrefWidth()/2, dim.y/2 - logo.getPrefHeight()/2 + dim.y/4);
        logo.setOrigin(Align.center);
        // Main menu window
        Window uiMain = new Window("", skin);
        uiMain.add(start).expandX().uniform().row();
        uiMain.add(opts).expandX().uniform().row();
        // For desktop only
        if(Gdx.app.getType() == Application.ApplicationType.Desktop){
            uiMain.add(quit).expandX().uniform().row();
        }
        uiMain.pad(width < height ? height/75 : width/75);
        uiMain.setMovable(false);
        uiMain.pack();
        float xPos = dim.x/2 - uiMain.getPrefWidth()/2;
        float yPos = dim.y/3 - uiMain.getPrefHeight()/2;
        uiMain.setPosition(xPos, 0);
        uiMain.addAction(
                sequence(moveTo(xPos,  yPos + dim.y/32, .3f),
                        delay(.01f),
                        moveTo(xPos, yPos - dim.y/32, .15f),
                        delay(.02f),
                        moveTo(xPos, yPos, .1f)));
        ui.addActor(uiMain);

    }
    private void initInput(){
        final InputAdapter backBtnInput = new InputAdapter(){
            @Override
            public boolean keyUp(int keycode){
                if(keycode == Input.Keys.BACK) {
                    quit();
                }
                return false;
            }
        };
        multiplexer.addProcessor(backBtnInput);
        multiplexer.addProcessor(ui);
        Gdx.input.setInputProcessor(multiplexer);

    }

    private void quit(){
        game.dispose();
        ui.dispose();
        Gdx.app.exit();
    }

    @Override
    public void show(){
        super.show();
        batch = new SpriteBatch();
        ui = new Stage();
        logo = new Image(game.get(AssetTag.Art_Title, Texture.class));
        start = new TextButton(strings.get("UI_START"), skin);
        opts = new TextButton(strings.get("UI_OPTIONS"), skin);
        quit = new TextButton(strings.get("UI_QUIT"), skin);
        initUI();
        initInput();
        multiplexer.addProcessor(ui);
        game.playBgm(AssetTag.Bgm_Theme);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta){
        super.render(delta);
        batch.begin();
        drawBg(batch);
        drawFps(batch);
        batch.end();
        ui.act(delta);
        ui.draw();
    }


}
