package com.homefriend.flightsim.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.homefriend.flightsim.FlightSim;
import com.homefriend.flightsim.core.GenTag;

public class SceneGame extends SceneBase {
    // 2D view (gui)
    private Stage ui;
    private SpriteBatch batch;
    // 3D view
    PerspectiveCamera cam;
    CameraInputController camController;
    ModelBatch modelBatch;
    Array<ModelInstance> instances;
    Environment environment;
    //3d Models
    ArrayMap<String, Model> models;

    public SceneGame(FlightSim game) {
        super(game);
    }

    @Override
    public void show () {
        super.show();
        ui = new Stage();
        batch = new SpriteBatch();
        modelBatch = new ModelBatch();
        // 3d env
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        // 3d camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(10f, 10f, 10f);
        cam.lookAt(0,0,0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        // setup models
        createModels();
        //ground = new ModelInstance(model, "ground");
        //ball = new ModelInstance(model, "ball");
        //ball.transform.setToTranslation(0, 9f, 0);
        // Adding models
        camController = new CameraInputController(cam);
        initInput();
    }

    private void createModels(){
        models = new ArrayMap<>();
        instances = new Array<>();
        models.put("box", (Model) game.get(GenTag.Mdl_Box));
        instances.add(new ModelInstance(models.get("box")));
        //instances.add(new ModelInstance(models.get("box")));

    }
    @Override
    public void render (float delta) {
        super.render(delta);
        camController.update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    private void initInput() {
        final InputAdapter backBtnProcess = new InputAdapter() {
            @Override
            public boolean keyUp(int keycode) {
                // Back button
                if (keycode == Input.Keys.BACK) {
                    playClick();
                    game.prevScene();
                }
                if (keycode == Input.Keys.ESCAPE) {
                    playClick();
                    game.prevScene();
                }
                return false;
            }
        };
        multiplexer.addProcessor(backBtnProcess);
        multiplexer.addProcessor(ui);
        multiplexer.addProcessor(camController);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void dispose () {
        super.dispose();
        modelBatch.dispose();
        //for(String key : models.keys()){
        //    models.get(key).dispose();
        //}
       // models.clear();
    }
}
