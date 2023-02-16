package Decision.Defense;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.General.*;

public class Defense {
    public double prio;

    private GameObject bot;
    private GameState gameState;

    public Defense(GameObject player, GameState gameState) {
        this.bot = player;
        this.gameState = gameState;
    }

    public void getDefensePrio() {
        // mendeteksi jika bot dekat dengan lintasan torpedo, regardless jumlah torpedo yang mendekatinya
        // jika ada satu saja torpedo yang mendekati bot, maka bot akan mengeluarkan shield
        double min;
        // melihat 
        var torpedoList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO & General.isItHeadingTowards(item, bot))
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, bot)))       
            .collect(Collectors.toList());

        if (torpedoList.isEmpty()) {
            min = 100; // asumsi diurutkan membesar
        } else {
            min = General.distanceFromPlayerToProjectileTrajectory(torpedoList.get(0), bot);
        }

        this.prio = min;
   }

    public PlayerAction actionDefense() {
        PlayerAction playerAction = new PlayerAction();
        
        if (bot.getShieldCount() != 0) {
            playerAction.action = PlayerActions.ACTIVATESHIELD;
            playerAction.heading = 0;
        } else {
            var torpedoList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO & General.isItHeadingTowards(item, bot))
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, bot)))       
            .collect(Collectors.toList());

            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (General.objectHeading(torpedoList.get(0), bot) + 180) % 360;
        }

        return playerAction;
    }
}
