package collectables;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.Random;

import helpers.GameInfo;

public class Collectable extends Sprite {

    private World world;
    private Body body;
    private Random random = new Random();
    private Fixture fixture;
    private String name;

    public Collectable(World world, String name) {
        super(new Texture("Collectables/Idle" + name + ".png"));
        this.name = name;
        this.world = world;

        createBody();
    }

    void createBody() {

        setPosition(GameInfo.WIDTH / 2f, getRandomY());

        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.KinematicBody;

        bodyDef.position.set(this.getX() / GameInfo.PPM, this.getY() / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(false);

        CircleShape shape = new CircleShape();
        shape.setRadius((getHeight() / 2f) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = GameInfo.COLLECTABLE;
        fixtureDef.isSensor = true;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(name);

        shape.dispose();
    }

    public void changeFilter() {
        Filter filter = new Filter();
        filter.categoryBits = GameInfo.DISTROYED;
        fixture.setFilterData(filter);
    }

    float getRandomY() {
        float max = GameInfo.HEIGHT / 2f + 150f;
        float min = GameInfo.HEIGHT / 2f - 150f;

        return random.nextFloat() * (max - min) + min;
    }

    public void drawCollectables(SpriteBatch batch) {
        batch.draw(this, (getX() - getWidth() / 2f) + 5, (getY() - getHeight() / 2f) - 5);
    }

    public void moveCollectables() {
        body.setLinearVelocity(-1, 0);
    }

    public void stopCollectables() {
        body.setLinearVelocity(0, 0);
    }

    public void updateCollectables() {
        this.setPosition(body.getPosition().x * GameInfo.PPM, body.getPosition().y * GameInfo.PPM);
    }

    public void removeCollectables() {
        if (body.getUserData() == "Remove") {
            this.changeFilter();
            this.getTexture().dispose();
        }
    }

    public void disposeCollectables() {
        this.getTexture().dispose();
    }

}
