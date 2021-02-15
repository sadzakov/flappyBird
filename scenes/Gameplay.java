package scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sadzakov.sofija.GameMain;

import java.util.Random;

import collectables.Collectable;
import criteria.Criteria;
import ground.GroundBody;
import helpers.GameInfo;
import helpers.Hud;
import helpers.Score;
import pipes.Pipes;
import player.Bird;

public class Gameplay implements Screen, ContactListener {

    private GameMain game;
    private World world;
    private OrthographicCamera mainCamera;
    private OrthographicCamera debugCamera;
    private Viewport viewport;
    private Box2DDebugRenderer debugRenderer;
    private Bird bird;
    private boolean firstTouch;
    private GroundBody groundBody;
    private Hud hud;
    private Random random = new Random();
    private Random randomNo = new Random();
    private Criteria criteria;

    private Array<Collectable> collectableArray = new Array<>();
    private Array<Sprite> backgroundArr = new Array<>();
    private Array<Sprite> groundArr = new Array<>();
    private Array<Pipes> pipesArray = new Array<>();
    private Array<Criteria> criteriaArray = new Array<>();
    private Array<String> colorArray = new Array<>();

    private final int DISTANCE_BETWEEN_PIPES = 220;

    public Gameplay(GameMain game) {
        this.game = game;

        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);
        viewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        debugCamera = new OrthographicCamera();
        debugCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM, GameInfo.HEIGHT / GameInfo.PPM);
        debugCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

        debugRenderer = new Box2DDebugRenderer();

        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(this);

        bird = new Bird(world, GameInfo.WIDTH / 2f - 80, GameInfo.HEIGHT / 2f);
        hud = new Hud(game);

        createBackground();
        createGround();

        groundBody = new GroundBody(world, groundArr.get(0));

    }

    void checkForTheFirstTouch() {
        if (!firstTouch) {
            if (Gdx.input.justTouched()) {
                firstTouch = true;
                bird.activateBird();
                createAllPipes();
                createCriteriaArray();
            }
        }
    }

    void update(float deltaTime) {

        checkForTheFirstTouch();

        if (bird.getAlive()) {
            moveBackground();
            moveGround();
            birdFlap();
            updatePipes();
            movePipes();
            moveCollectables();
            for (Collectable c : collectableArray) {
                c.updateCollectables();
            }
        }
    }

    void createBackground() {
        for (int i = 0; i < 3; i++) {
            Sprite bg = new Sprite(new Texture("Background/Day.jpg"));
            bg.setPosition(i * bg.getWidth(), 0);
            backgroundArr.add(bg);
        }
    }

    void drawBackground(SpriteBatch batch) {
        for (Sprite s : backgroundArr) {
            batch.draw(s, s.getX(), s.getY());
        }
    }

    void moveBackground() {
        for (Sprite bg : backgroundArr) {
            float x1 = bg.getX() - 2f;
            bg.setPosition(x1, bg.getY());

            if (bg.getX() + GameInfo.WIDTH + (bg.getWidth() / 2f) < mainCamera.position.x) {
                float x2 = bg.getX() + bg.getWidth() * backgroundArr.size;
                bg.setPosition(x2, bg.getY());
            }
        }
    }

    void createGround() {
        for (int i = 0; i < 3; i++) {
            Sprite ground = new Sprite(new Texture("Background/Ground.png"));
            ground.setPosition(i * ground.getWidth(), -ground.getHeight() / 2 - 55);
            groundArr.add(ground);
        }
    }

    void drawGround(SpriteBatch batch) {
        for (Sprite s : groundArr) {
            batch.draw(s, s.getX(), s.getY());
        }
    }

    void moveGround() {
        for (Sprite ground : groundArr) {
            float x1 = ground.getX() - 1f;
            ground.setPosition(x1, ground.getY());

            if (ground.getX() + GameInfo.WIDTH + (ground.getWidth() / 2f) < mainCamera.position.x) {
                float x2 = ground.getX() + ground.getWidth() * groundArr.size;
                ground.setPosition(x2, ground.getY());
            }
        }
    }

    void birdFlap() {
        if (Gdx.input.justTouched()) {
            bird.birdFlap();
        }
    }

    void createAllPipes() {
        RunnableAction run = new RunnableAction();
        run.setRunnable(new Runnable() {
            @Override
            public void run() {
                createPipes();
                createArrayOfCollectables();
            }
        });

        SequenceAction sa = new SequenceAction();
        sa.addAction(Actions.delay(3f));
        sa.addAction(run);
        hud.getStage().addAction(Actions.forever(sa));
    }

    void createPipes() {
        Pipes p = new Pipes(world, GameInfo.WIDTH + DISTANCE_BETWEEN_PIPES);
        p.setMainCamera(mainCamera);
        pipesArray.add(p);
    }

    void drawPipes(SpriteBatch batch) {
        for(Pipes pipe : pipesArray) {
            pipe.drawPipes(batch);
        }
    }

    void updatePipes() {
        for(Pipes pipe : pipesArray) {
            pipe.updatePipes();
        }
    }

    void movePipes() {
        for(Pipes pipe : pipesArray) {
            pipe.movePipes();
        }
    }

    void stopPipes() {
        for(Pipes pipe : pipesArray) {
            pipe.stopPipes();
        }
    }

    void birdDied() {
        bird.setAlive(false);
        bird.birdDied();
        stopPipes();
        stopCollectables();
        stopBird();
        hud.getStage().clear();
    }

    void createArrayOfCollectables() {

       int randomNo = random.nextInt(100);

        if (randomNo >= 60) {
           collectableArray.add(new Collectable(world, "Blue"));
       } else if (randomNo <= 30) {
           collectableArray.add(new Collectable(world, "Red"));
       } else {
           collectableArray.add(new Collectable(world, "Green"));
       }

        collectableArray.shuffle();
    }

    void drawCollectable(SpriteBatch batch) {
        for (Collectable c : collectableArray) {
            c.drawCollectables(batch);
        }
    }

    void moveCollectables() {
        for (Collectable c : collectableArray) {
            c.moveCollectables();
        }
    }

    void stopCollectables() {
        for (Collectable c : collectableArray) {
            c.stopCollectables();
        }
    }

    void stopBird() {
        bird.stopBird();
    }

    void removeCollectables() {
        for (Collectable c : collectableArray) {
            c.removeCollectables();
        }
        collectableArray.clear();
    }

    void createCriteriaArray() {

        colorArray.add("Red");
        colorArray.add("Green");
        colorArray.add("Blue");
        colorArray.shuffle();

        criteriaArray.add(criteria = new Criteria(world, colorArray.random()));
    }

    void drawCriteria(SpriteBatch batch) {
        for (Criteria c : criteriaArray) {
            c.drawCriteria(batch);
        }
    }

    void removeCriteria() {
        for (Criteria c : criteriaArray) {
            c.removeCriteria();
        }
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.getBatch().begin();

        drawBackground(game.getBatch());
        drawGround(game.getBatch());
        bird.drawIdle(game.getBatch());
        drawPipes(game.getBatch());
        drawCollectable(game.getBatch());
        drawCriteria(game.getBatch());

        game.getBatch().end();

        debugRenderer.render(world, debugCamera.combined);
        bird.updateBird();

        game.getBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();
        hud.getStage().act();

        world.step(Gdx.graphics.getDeltaTime(), 6, 2);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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

        world.dispose();

        for (Sprite bg : backgroundArr) {
            bg.getTexture().dispose();
        }
        for (Sprite ground : groundArr) {
            ground.getTexture().dispose();
        }
        for (Pipes p : pipesArray) {
            p.disposePipes();
        }
        for (Criteria criteria : criteriaArray) {
            criteria.disposeCriteria();
        }
        for (Collectable collectable : collectableArray) {
            collectable.disposeCollectables();
        }

        bird.disposePlayer();
    }

    @Override
    public void beginContact(Contact contact) {

        Fixture body1, body2;

        if(contact.getFixtureA().getUserData().equals("Bird")) {
            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();
        } else {
            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();
        }

        if(body1.getUserData().equals("Bird") && body2.getUserData().equals("Ground")) {
            if(bird.getAlive()) {
                birdDied();
            }
        }

        if(body1.getUserData().equals("Bird") && body2.getUserData().equals("Pipe")) {
            if(bird.getAlive()) {
                birdDied();
            }
        }

        if(body1.getUserData().equals("Bird") && body2.getUserData().equals("Blue")) {
            if(bird.getAlive()) {

                if (criteria.getFixture().getUserData().equals("Blue")) {
                    criteria.incrementScore(1);

                    if (Score.getScoreInstance().score == 3) {
                        removeCriteria();
                        criteria.changeCriteria(colorArray.random());
                        Score.getScoreInstance().setScore(0);
                    }
                }
                body2.setUserData("Remove");
                removeCollectables();

            }
        }

        if(body1.getUserData().equals("Bird") && body2.getUserData().equals("Green")) {
            if(bird.getAlive()) {

                if (criteria.getFixture().getUserData().equals("Green")) {
                    criteria.incrementScore(1);

                    if (Score.getScoreInstance().score == 3) {
                        removeCriteria();
                        criteria.changeCriteria(colorArray.random());
                        Score.getScoreInstance().setScore(0);
                    }
                }
                body2.setUserData("Remove");
                removeCollectables();

            }
        }

        if(body1.getUserData().equals("Bird") && body2.getUserData().equals("Red")) {
            if(bird.getAlive()) {

                if (criteria.getFixture().getUserData().equals("Red")) {
                    criteria.incrementScore(1);

                    if (Score.getScoreInstance().score == 3) {
                        removeCriteria();
                        criteria.changeCriteria(colorArray.random());
                        Score.getScoreInstance().setScore(0);
                    }
                }
                body2.setUserData("Remove");
                removeCollectables();

            }
        }



    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
