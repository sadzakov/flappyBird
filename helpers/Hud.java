package helpers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sadzakov.sofija.GameMain;

public class Hud {

    private GameMain game;
    private Stage stage;
    private Viewport viewport;

    public Hud(GameMain game) {
        this.game = game;
        viewport = new FitViewport(GameInfo.WIDTH, GameInfo.HEIGHT,
                new OrthographicCamera());

        stage = new Stage(viewport, game.getBatch());

    }

    public Stage getStage() {
        return this.stage;
    }


}
