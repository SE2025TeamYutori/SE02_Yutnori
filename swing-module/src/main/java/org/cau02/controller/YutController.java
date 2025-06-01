package org.cau02.controller;

import org.cau02.model.GameManager;
import org.cau02.model.Yut;

public class YutController {
    private GameController gameController;
    
    public YutController(GameController gameController) {
        this.gameController = gameController;
    }
    
    public Yut throwRandomYut() {
        GameManager gm = gameController.getGameManager();
        return gm != null ? gm.throwRandomYut() : null;
    }
    
    public void throwSelectedYut(Yut yut) {
        GameManager gm = gameController.getGameManager();
        if (gm != null) {
            gm.throwSelectedYut(yut);
        }
    }
    
    public boolean canThrowYut() {
        return gameController.getCurrentYutCount() > 0;
    }
}