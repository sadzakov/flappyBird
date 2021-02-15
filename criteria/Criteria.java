package criteria;

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
import helpers.Score;

public class Criteria extends Sprite {

    private World world;
    private Body body;
    private Fixture fixture;
    private String name;

    public Criteria(World world, String name) {
        super(new Texture("Criteria/" + name + ".png"));
        this.world = world;
        this.name = name;

        setPosition(GameInfo.WIDTH / 2f + 50, GameInfo.HEIGHT / 2f + 50);
        createBody();
    }

    void createBody() {

        BodyDef bodyDef = new BodyDef();

        bodyDef.type = BodyDef.BodyType.KinematicBody;

        bodyDef.position.set(this.getX()/ GameInfo.PPM, this.getY() / GameInfo.PPM);

        body = world.createBody(bodyDef);
        body.setFixedRotation(false);

        CircleShape shape = new CircleShape();
        shape.setRadius((getHeight() / 2f + 8) / GameInfo.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(name);

        shape.dispose();
    }

    public void drawCriteria(SpriteBatch batch) {
        batch.draw(this, (getX() - getWidth() / 2f) + 5, (getY() - getHeight() / 2f) - 5);
    }

    public void removeCriteria() {
        this.getTexture().dispose();
    }

    public void changeCriteria(String name) {
        fixture.setUserData(name);
        this.setTexture(new Texture("Criteria/" + name + ".png"));
    }

    public void incrementScore(int score) {
        Score.getScoreInstance().score += score;
    }

    public Fixture getFixture() {
        return this.fixture;
    }

    public void disposeCriteria() {
        this.getTexture().dispose();
    }

}
