package net.runelite.client.plugins.mafhamtoa.Palm;

import net.runelite.api.GameObject;

public class Spike {
    private GameObject gameObject;
    private Integer previousAnim;
    private Integer cycle;
    private Integer direction;

    public Spike(GameObject gameObject, Integer previousAnim, Integer cycle, Integer direction) {
        this.gameObject = gameObject;
        this.previousAnim = previousAnim;
        this.cycle = cycle;
        this.direction = direction;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public Integer getPreviousAnim() {
        return previousAnim;
    }

    public void setPreviousAnim(Integer previousAnim) {
        this.previousAnim = previousAnim;
    }

    public Integer getCycle() {
        return cycle;
    }

    public void setCycle(Integer cycle) {
        this.cycle = cycle;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }
}
