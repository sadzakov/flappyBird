package helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

public class Score {

    private static Score scoreInstance = new Score();

    private GameData gameData;
    private Json json = new Json();
    private FileHandle fileHandle = Gdx.files.local("bin/GameData.json");
    public int score;

    private Score() {

    }

    public void initializeGameData() {
        if (!fileHandle.exists()) {
            gameData = new GameData();
            gameData.setScore(0);
            saveData();
        } else {
            loadData();
        }
    }

    public void saveData() {
        if (gameData != null) {
            fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(gameData)), false);
        }
    }

    public void loadData() {
        gameData = json.fromJson(GameData.class, Base64Coder.decodeString(fileHandle.readString()));
    }

    public static Score getScoreInstance() {
        return scoreInstance;
    }

    public void setScore(int score) {
        this.score = score;
    }

}
