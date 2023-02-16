package Decision.Retreat;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.General.*;

public class Retreat {
    public double prio;
    public int kind;

    private GameObject bot;
    private GameState gameState;

    public Retreat(GameObject bot, GameState gameState) {
        this.bot = bot;
        this.gameState = gameState;
    }

    public void getRetreatPrio() {
        // priority untuk kabur dari player lebih penting daripada menghindari gas cloud
        // untuk gas cloud
        double gas_prio = 1000000;
        var gasList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToObject(item, bot)))       
            .collect(Collectors.toList());

        if (!gasList.isEmpty()){
            gas_prio = General.distanceFromPlayerToObject(gasList.get(0), bot) * 3; // skalanya dapat berubah
        }

        // untuk kabur dari player
        double run_prio = 1000000;
        var playerlist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToObject(item, bot)))
            .collect(Collectors.toList());

        if (!playerlist.isEmpty()){
            run_prio = General.distanceFromPlayerToObject(playerlist.get(1), bot);
        }

        // untuk kabur dari supernova
        double supernova_prio = 1000000;
        var supernovalist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, bot)))
            .collect(Collectors.toList());

        if (!supernovalist.isEmpty()) {
            supernova_prio = General.distanceFromPlayerToProjectileTrajectory(supernovalist.get(0), bot);
        }

        // untuk kabur dari border
        double border_prio;
        if (gameState.getWorld().getRadius() != null) {
            border_prio = gameState.getWorld().getRadius() * gameState.getWorld().getRadius();
            border_prio -= (bot.getPosition().getX()*bot.getPosition().getX() + bot.getPosition().getY()*bot.getPosition().getY());
        } else {
            border_prio = 1000000;
        }

        if (run_prio <= gas_prio & run_prio <= supernova_prio & run_prio <= border_prio) {
            this.prio = run_prio;
            this.kind = 2;
        } else if (gas_prio <= run_prio & gas_prio <= supernova_prio & gas_prio <= border_prio) {
            this.prio = gas_prio;
            this.kind = 1;
        } else if (supernova_prio <= run_prio & supernova_prio <= gas_prio & supernova_prio <= border_prio) {
            this.prio = supernova_prio;
            this.kind = 3;
        } else {
            this.prio = border_prio;
            this.kind = 4;
        }
    }

    public PlayerAction actionRetreat() {
        PlayerAction playerAction = new PlayerAction();
        
        if (kind == 1) { // lari dari gas
            var gasList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.GASCLOUD)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToObject(item, bot)))       
            .collect(Collectors.toList());

            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (General.objectHeading(gasList.get(0), bot) + 180) % 360;
        } else if (kind == 2) { // lari dari player lain
            var playerlist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToObject(item, bot)))
            .collect(Collectors.toList());

            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (General.objectHeading(playerlist.get(1), bot) + 180) % 360;           

        } else if (kind == 3) { // lari dari supernova
            // menjauh dari tempat supernova sekarang
            var supernovalist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.SUPERNOVABOMB)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, bot)))
            .collect(Collectors.toList());

            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (General.objectHeading(supernovalist.get(0), bot) + 180) % 360;
        } else {
            // menjauh dari border menuju pusat map
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = (General.objectHeadingtoPoint(0, 0, bot)) % 360;
        }


        return playerAction;
    }
}
