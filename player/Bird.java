package player;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import helpers.GameInfo;

public class Bird extends Sprite {

    private World world;
    private Body body;
    private boolean isAlive;
    private Texture birdDead;

    public Bird(World world, float x, float y) {
        super(new Texture("Birds/Blue/Idle.png"));
        birdDead = new Texture("Birds/Blue/Dead.png");

        this.world = world;

        setPosition(x, y);
        createBody();
    }

    void createBody() {
        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.DynamicBody;

        bodyDef.position.set(getX() / GameInfo.PPM, getY() / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(false);

        CircleShape shape = new CircleShape();
        shape.setRadius((getHeight() / 2f + 8) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameInfo.BIRD;
        fixtureDef.filter.maskBits = GameInfo.GROUND | GameInfo.PIPE | GameInfo.COLLECTABLE;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData("Bird");

        shape.dispose();

        body.setActive(false);
    }

    public void activateBird() {
        isAlive = true;
        body.setActive(true);
    }

    public void birdFlap() {
        body.setLinearVelocity(0, 3);
    }

    public void stopBird() {
        body.setLinearVelocity(0, 0);
    }

    public void drawIdle(SpriteBatch batch) {
        batch.draw(this, (getX() - getWidth() / 2f) + 5, (getY() - getHeight() / 2f) - 5);
    }

    public void updateBird() {
        setPosition(body.getPosition().x * GameInfo.PPM, body.getPosition().y * GameInfo.PPM);
    }

    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getAlive() {
        return isAlive;
    }

    public void birdDied() {
        this.setTexture(birdDead);
    }

    public void disposePlayer() {
        this.getTexture().dispose();
    }

}
