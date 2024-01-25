package com.naus.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.naus.AssetManager;
import com.naus.AsteroideRectangle;
import com.naus.Naus;

public class GameScreen implements Screen{

    final Naus game;

    OrthographicCamera camera;

    Rectangle nau;

    Rectangle estrella;

    Array<AsteroideRectangle> asteroides;
    long lastAsteroideTime;
    int vides = 3;
    int score = 0;
    int spawnFrequency = 1000000000;

    // Initialize directions and timer
    float directionX = MathUtils.random(-1f, 1f); // Generates a random float between -1 and 1
    float directionY = MathUtils.random(-1f, 1f); // Generates a random float between -1 and 1
    float timer = 0;

    int estrellaMoveSpeed = 100;

    private final int WIDTH = AssetManager.getWIDTH();
    private final int HEIGHT = AssetManager.getHEIGHT();

    public GameScreen(final Naus game) {
        this.game = game;

        AssetManager.load();

        AssetManager.bg_music.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);

        nau = new Rectangle();
        nau.width = 64;
        nau.height = 64;
        nau.x = WIDTH / 2 - nau.width / 2;
        nau.y = 20;

        asteroides = new Array<AsteroideRectangle>();
        spawnAsteroide();
    }

    private void spawnEstrella() {
        estrella = new Rectangle();
        estrella.width = 32;
        estrella.height = 32;
        estrella.x = MathUtils.random(0, WIDTH - estrella.width);
        estrella.y = MathUtils.random(0, HEIGHT - estrella.height);
    }

    private void spawnAsteroide() {
        AsteroideRectangle asteroide = new AsteroideRectangle();
        asteroide.width = 32;
        asteroide.height = 32;

        int border = MathUtils.random(1, 4);
        switch(border) {
            case 1: // top
                asteroide.x = MathUtils.random(0, WIDTH - asteroide.width);
                asteroide.y = HEIGHT;
                asteroide.vx = MathUtils.random(-200, 200);
                asteroide.vy = -200;
                break;
            case 2: // right
                asteroide.x = WIDTH;
                asteroide.y = MathUtils.random(0, HEIGHT - asteroide.height);
                asteroide.vx = -200;
                asteroide.vy = MathUtils.random(-200, 200);
                break;
            case 3: // bottom
                asteroide.x = MathUtils.random(0, WIDTH - asteroide.width);
                asteroide.y = 0;
                asteroide.vx = MathUtils.random(-200, 200);
                asteroide.vy = 200;
                break;
            case 4: // left
                asteroide.x = 0;
                asteroide.y = MathUtils.random(0, HEIGHT - asteroide.height);
                asteroide.vx = 200;
                asteroide.vy = MathUtils.random(-200, 200);
                break;
        }

        asteroides.add(asteroide);
        lastAsteroideTime = TimeUtils.nanoTime();
    }



    @Override
    public void show() {
        AssetManager.bg_music.play();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(AssetManager.background, 0, 0, WIDTH, HEIGHT);
        game.font.draw(game.batch, "Vides restants: " + vides, 0, 20);
        game.font.draw(game.batch, "Puntuacio: " + score, 0, HEIGHT - 20);
        game.batch.draw(AssetManager.nauImage, nau.x, nau.y, nau.width, nau.height);
        for (Rectangle asteroide : asteroides) {
            game.batch.draw(AssetManager.asteroideImage, asteroide.x, asteroide.y, asteroide.width, asteroide.height);
        }
        if(estrella != null) game.batch.draw(AssetManager.starImage, estrella.x, estrella.y, estrella.width, estrella.height);
        game.batch.end();

        //Touch controls
        if(Gdx.input.isTouched()){
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            nau.x = touchPos.x - nau.width / 2;
            nau.y = touchPos.y - nau.height / 2;
        }

        //Keyboard controls
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) nau.x -= 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) nau.x += 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.UP)) nau.y += 200 * Gdx.graphics.getDeltaTime();
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) nau.y -= 200 * Gdx.graphics.getDeltaTime();

        //Screen bounds
        if(nau.x < 0) nau.x = 0;
        if(nau.x > WIDTH - nau.width) nau.x = WIDTH - nau.width;
        if(nau.y < 0) nau.y = 0;
        if(nau.y > HEIGHT - nau.height) nau.y = HEIGHT - nau.height;

        //Asteroides spawn frequency
        if(TimeUtils.nanoTime() - lastAsteroideTime > spawnFrequency) spawnAsteroide();

        //Asteroides movement
        for(AsteroideRectangle asteroide : asteroides){
            asteroide.x += asteroide.vx * Gdx.graphics.getDeltaTime();
            asteroide.y += asteroide.vy * Gdx.graphics.getDeltaTime();
            if(asteroide.overlaps(nau)){
                asteroides.removeValue(asteroide, true);
                AssetManager.explosionSound.play();
                vides--;
                if(vides == 0){
                    game.setScreen(new GameOverScreen(game, score));
                    dispose();
                }
            }
        }

        //Estrella spawn
        if(estrella == null) spawnEstrella();
        if(estrella.overlaps(nau)){
            //Increase of score and spawn frequency (1% faster every star)
            spawnFrequency -= 10000000;
            estrella = null;
            score++;
        }

        //Estralla movement
        if(estrella != null){
            timer += Gdx.graphics.getDeltaTime();

            if(timer > 2){ // Change direction every 2 seconds
                directionX = MathUtils.random(-1f, 1f);
                directionY = MathUtils.random(-1f, 1f);
                timer = 0;
            }

            estrella.x += estrellaMoveSpeed * directionX * Gdx.graphics.getDeltaTime();
            estrella.y += estrellaMoveSpeed * directionY * Gdx.graphics.getDeltaTime();

            if(estrella.x > WIDTH) estrella.x = 0;
            if(estrella.x < 0) estrella.x = WIDTH;
            if(estrella.y > HEIGHT) estrella.y = 0;
            if(estrella.y < 0) estrella.y = HEIGHT;
        }

    }

    @Override
    public void resize(int width, int height) {

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
        AssetManager.dispose();
    }
}
