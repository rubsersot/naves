package com.naus.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.naus.AssetManager;
import com.naus.Naus;

public class GameOverScreen implements Screen {

    final Naus game;

    final OrthographicCamera camera;

    int finalScore;

    final int WIDTH = AssetManager.getWIDTH();
    final int HEIGHT = AssetManager.getHEIGHT();

    public GameOverScreen(final Naus game, int score) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WIDTH, HEIGHT);
        this.finalScore = score;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.2f, 0.2f, 1);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        game.font.draw(game.batch, "GAME OVER", WIDTH / 2 - 50, HEIGHT / 2);
        game.font.draw(game.batch, "Your score: " + finalScore, WIDTH / 2 - 50, HEIGHT / 2 - 25);
        game.font.draw(game.batch, "Tap ENTER to play again!", WIDTH / 2 - 50, HEIGHT / 2 - 50);

        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game));
            dispose();
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

    }
}
