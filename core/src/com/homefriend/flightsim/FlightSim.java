package com.homefriend.flightsim;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.*;
import com.homefriend.flightsim.core.AssetTag;
import com.homefriend.flightsim.core.GenTag;
import com.homefriend.flightsim.core.Prefs;
import com.homefriend.flightsim.core.SkinTag;
import com.homefriend.flightsim.platform.SystemApi;
import com.homefriend.flightsim.scene.SceneMainMenu;
import com.homefriend.flightsim.scene.SceneSplash;

public class FlightSim extends Game {
	public AssetManager assets;
	public Preferences prefs;
	// System objects
	private ArrayMap<AssetTag, String> sysAssets;
	// generated textures
	private ArrayMap<GenTag, Object> gen;
	// images in skin
	private ArrayMap<SkinTag, String> skinAssets;
	private String lastBgmFile;
	private float density;
	public SystemApi config;

	private Array<Screen> screens = new Array<>();
	// Loading game
	public FlightSim(SystemApi config){
		assets = new AssetManager();
		sysAssets = new ArrayMap<>();
		gen = new ArrayMap<>();
		skinAssets = new ArrayMap<>();
		density = 1f;
		this.config = config;
	}

	public FlightSim(){
		assets = new AssetManager();
		sysAssets = new ArrayMap<>();
		gen = new ArrayMap<>();
		skinAssets = new ArrayMap<>();
		density = 1f;
	}
	@Override
	public void create() {
		screens.add(new SceneSplash(this));
		setScreen(screens.get(0));
	}

	@Override
	public void dispose(){
		try{
			assets.dispose();
		}
		catch(GdxRuntimeException e){

		}
		while(screens.size > 0){
			screens.pop().dispose();
		}
		super.dispose();
	}

	public void preload() {
		this.density = Math.max(1, Gdx.graphics.getDensity());
		sysAssets.put(AssetTag.Art_BG, "UI/bg.png");
		sysAssets.put(AssetTag.Art_BG_Vert, "UI/bg_vert.png");
		sysAssets.put(AssetTag.Art_Title, "UI/title.png");
		sysAssets.put(AssetTag.Art_Skin, "UI/skin.json");
		sysAssets.put(AssetTag.Data_Txt, "data/Strings");
		sysAssets.put(AssetTag.Font_Main, "UI/mainfont.ttf");
		sysAssets.put(AssetTag.Font_Msg, "UI/showfont.ttf");
		sysAssets.put(AssetTag.Font_Big, "UI/bigfont.ttf");
		sysAssets.put(AssetTag.Font_Small, "UI/smallfont.ttf");
		skinAssets.put(SkinTag.Font_Main, "default-font");
		skinAssets.put(SkinTag.Font_Big, "big-font");
		skinAssets.put(SkinTag.Font_Msg, "msg-font");
		skinAssets.put(SkinTag.Font_Small, "small-font");
		try{
			assets.load(sysAssets.get(AssetTag.Art_BG), Texture.class);
			assets.load(sysAssets.get(AssetTag.Art_BG_Vert), Texture.class);
			assets.load(sysAssets.get(AssetTag.Data_Txt), I18NBundle.class);
		}
		catch(GdxRuntimeException e){

		}
		this.density = Math.max(1, Gdx.graphics.getDensity());
		//Temp fonts
		FileHandleResolver resolver = new InternalFileHandleResolver();
		assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		// font name:
		FreetypeFontLoader.FreeTypeFontLoaderParameter mainFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		mainFont.fontFileName = sysAssets.get(AssetTag.Font_Main);
		mainFont.fontParameters.size = (int)(18 * density);
		mainFont.fontParameters.color = Color.BLACK;
		try{
			assets.load(mainFont.fontFileName, BitmapFont.class, mainFont);
		}
		catch(GdxRuntimeException e){

		}
		// Preferences
		prefs = Gdx.app.getPreferences("Game Settings");
		prefs.putBoolean("CREATED", true);
		prefs.flush();
		if(config != null){
			if(prefs.getBoolean("VERT", true))
				config.setOrientationPortrait();
			else
				config.setOrientationLandscape();
		}
	}

	// Get files recursively
	private Array<FileHandle> tmpFileLst;
	private void getFiles(FileHandle entry){
		for(FileHandle e : entry.list()){
			if(e.isDirectory()){
				getFiles(e);
			}
			else{
				tmpFileLst.add(e);
			}
		}
	}
	private Array<FileHandle> getFiles(String path){
		FileHandle files = Gdx.files.internal(path);
		tmpFileLst = new Array<>();
		getFiles(files);
		return tmpFileLst;
	}

	public void load(){
        // Loop to get texture files
		Array<FileHandle> files = getFiles("gfx/2d");
        for (FileHandle entry: files) {
            if(!entry.isDirectory() && isValidFile(entry, new String[]{"pack"})){
                assets.load(entry.path(), TextureAtlas.class);
            }
        }
        files = getFiles("gfx/3d");
		for (FileHandle entry: files) {
			if(!entry.isDirectory() && isValidFile(entry, new String[]{"obj", "g3db"})){
				assets.load(entry.path(), Model.class);
			}
		}
        files = getFiles("sfx");
        String[] soundExts = {"wav", "ogg", "mp3"};
        for (FileHandle entry: files) {
            if(!entry.isDirectory() && isValidFile(entry, soundExts))
                assets.load(entry.path(), Sound.class);
        }
        files = getFiles("mfx");
        for (FileHandle entry: files) {
            if(!entry.isDirectory() && isValidFile(entry, soundExts))
                assets.load(entry.path(), Music.class);
        }
        files = getFiles("bgm");
        for (FileHandle entry: files) {
            if(!entry.isDirectory() && isValidFile(entry, soundExts))
                assets.load(entry.path(), Music.class);
        }
        files = getFiles("particles");
        for (FileHandle entry: files) {
            if(!entry.isDirectory() && entry.extension().equalsIgnoreCase("p"))
                assets.load(entry.path(), ParticleEffect.class);
        }
        // Title logo
        assets.load(sysAssets.get(AssetTag.Art_Title), Texture.class);
        //
        // Loading fonts and skin
        //
		// Small Font
		FreetypeFontLoader.FreeTypeFontLoaderParameter smallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		smallFont.fontFileName = sysAssets.get(AssetTag.Font_Small);
		smallFont.fontParameters.size = (int)(12 * density);
		assets.load(smallFont.fontFileName, BitmapFont.class, smallFont);
		// Big Font
		FreetypeFontLoader.FreeTypeFontLoaderParameter bigFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
		bigFont.fontFileName = sysAssets.get(AssetTag.Font_Big);
		bigFont.fontParameters.size = (int)(32 * density);
		assets.load(bigFont.fontFileName, BitmapFont.class, bigFont);
        // Msg Font
        FreetypeFontLoader.FreeTypeFontLoaderParameter showFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        showFont.fontFileName = sysAssets.get(AssetTag.Font_Msg);
        showFont.fontParameters.size = (int)(48* density);
        showFont.fontParameters.color = Color.GOLD;
        showFont.fontParameters.borderWidth = 1;
        showFont.fontParameters.shadowOffsetX = 4;
        showFont.fontParameters.shadowOffsetY = 4;
        assets.load(showFont.fontFileName, BitmapFont.class, showFont);
        // map multiple fonts here

	}
	public void setup() {
	    // generated assets

        ModelBuilder modelBuilder = new ModelBuilder();
        Model b = modelBuilder.createBox(5f, 5f, 5f,
               new Material(ColorAttribute.createDiffuse(Color.GREEN)),
               VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        gen.put(GenTag.Mdl_Box, b);
        // Skin and font adjustments
        ObjectMap<String, Object> fontMap = new ObjectMap<>();
        fontMap.put(skinAssets.get(SkinTag.Font_Main), get(AssetTag.Font_Main, BitmapFont.class));
        fontMap.put(skinAssets.get(SkinTag.Font_Big), get(AssetTag.Font_Big, BitmapFont.class));
        fontMap.put(skinAssets.get(SkinTag.Font_Msg), get(AssetTag.Font_Msg, BitmapFont.class));
		fontMap.put(skinAssets.get(SkinTag.Font_Small), get(AssetTag.Font_Small, BitmapFont.class));
        SkinLoader.SkinParameter parameter = new SkinLoader.SkinParameter(fontMap);
        // skin
        assets.load(sysAssets.get(AssetTag.Art_Skin), Skin.class, parameter);
        assets.finishLoading();
	}


	// Scene handling
	public void nextScene(Screen s){
		screens.add(s);
		setScreen(s);
	}
	public void prevScene(){
		screens.pop().dispose();
		setScreen(screens.peek());
	}
	public void resetToMenu(){
		while(screens.size > 0){
			screens.pop().dispose();
		}
		screens.add(new SceneMainMenu(this));
		setScreen(screens.get(0));
	}

	public void playSfx(AssetTag sfx_click) {
	}

	public float getDensity(){
		return density;
	}
	// Asset retrieval
	public <T> T get(GenTag tag){
		return (T) gen.get(tag);
	}
	public String get(SkinTag tag){
		return skinAssets.get(tag);
	}
	public <T> T get(AssetTag tag, java.lang.Class<T> type){
		return assets.get(sysAssets.get(tag), type);
	}
	public <T> boolean isLoaded(AssetTag tag, java.lang.Class<T> type){
		return assets.isLoaded(sysAssets.get(tag), type);
	}
	// Custom asset retrieval
	public <T> T get(String filename, java.lang.Class<T> type){
		return assets.get(filename, type);
	}
	public <T> boolean isLoaded(String filename, java.lang.Class<T> type){
		return assets.isLoaded(filename, type);
	}
    private boolean isValidFile(FileHandle entry, String[] exts){
        for(String e : exts){
            if(entry.extension().equalsIgnoreCase(e)){
                return true;
            }
        }
        return false;
    }

    private String getValidFilename(String filename, String assetFolder, String[] exts){
        if(!assetFolder.equals("")){
            if(!assetFolder.endsWith("/")) assetFolder += "/";
            else if(assetFolder.endsWith("\\")) assetFolder = assetFolder.replace("\\", "/" );
        }
        String filePath = assetFolder + filename;
        if(Gdx.files.internal(filePath).exists()) return filePath;
        else{
            for(String e : exts){
                String testPath = filePath + "." + e;
                if(Gdx.files.internal(testPath).exists()){
                    return testPath;
                }
            }
        }
        return "";
    }

	public void loadSettings(){
		Prefs.inst.sndVol = prefs.getFloat("SND_VOL", 1.0f);
		Prefs.inst.musVol = prefs.getFloat("MUS_VOL", 1.0f);
		Prefs.inst.soundMute = prefs.getBoolean("SND_MUTE", false);
		Prefs.inst.musicMute = prefs.getBoolean("MUS_MUTE", false);
		Prefs.inst.vert = prefs.getBoolean("VERT", false);
	}
	public void saveSettings(){
		prefs.putBoolean("MUS_MUTE", Prefs.inst.musicMute);
		prefs.putFloat("MUS_VOL", Prefs.inst.musVol);
		prefs.putBoolean("SND_MUTE", Prefs.inst.soundMute);
		prefs.putFloat("SND_VOL", Prefs.inst.sndVol);
		prefs.putBoolean("VERT", Prefs.inst.vert);
		prefs.flush();
	}

	public boolean isMobileApp(){
		return Gdx.app.getType() == Application.ApplicationType.Android ||
				Gdx.app.getType() == Application.ApplicationType.iOS;
	}

    public void pauseBgm() {
    }

    public void resumeLastBgm() {
    }

    public void playBgm(AssetTag tag) {
    }

    public void stopBgm(AssetTag bgm_theme) {
    }
}
