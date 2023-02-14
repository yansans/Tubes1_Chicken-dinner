package Decision.Offence;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.General.*;

public class Offence {

    private int supernovaSize = 10;
    private double supernovaRadius = 0.25 * supernovaSize;
    public double prio;
    private double kind;
    private double var;
    private int fuel = 5;

    public void getPrioGeneral(GameObject bot, GameState gameState){
        double max = 10;
        prio = getPrioType(bot, gameState) < 3 ? 0.0 : max;
    }

    public PlayerAction doOffence(GameObject bot, GameState gameState){
        var enemy = ObjectTypes.PLAYER;
        var action = PlayerActions.FIRETORPEDOES;
        int min_size = 30;
        if (bot.size < min_size || fuel < 0){
            action = PlayerActions.FORWARD;
            enemy = ObjectTypes.FOOD;
            fuel += 2;
        }
        if (kind == 2){
            action = PlayerActions.FIRESUPERNOVA;
        } else if (kind == 3){
            action = PlayerActions.FIRETELEPORT;
        }
        fuel--;
        return basicAttackDistance(action, enemy, false, gameState, bot);
    }

    public double getPrioType(GameObject bot, GameState gameState){
        double max = 3;
        double all_prio = 0;

        all_prio += getPrioTorpedoes(bot, gameState);
        all_prio += getPrioSupernova(bot, gameState);
        all_prio += getPrioEat(bot, gameState);

        kind = (int) all_prio / 10;
        var = (int) all_prio % 10;

        return max - kind - var;
    }
    
    public double getPrioTorpedoes(GameObject bot, GameState gameState){
        // mendapatkan nilai prioritas untuk melakukan aksi torpedo
        double prio = 0;
        int max_distance = 50;
        int min_size = bot.getSize();

        var playerListD = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListD.remove(bot);
        playerListS.remove(bot);

        // apabila player tidak jauh
        if (General.distanceFromPlayerToObject(bot, playerListD.get(0)) <= max_distance) {
            prio += 5;
        // apabila player lebih besar dari size bot
        } if (playerListS.get(0).getSize() > min_size){
            prio += 5;
        }

        return prio;
    }


    public double getPrioSupernova(GameObject bot, GameState gameState){
        double prio = 0;
        int min_size = bot.getSize();
        int mult = 10;
        
        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        Collections.reverse(playerListS);

        // apabila player lebih besar dari size bot
        if (playerListS.get(0).getSize() > min_size){
            prio += 5;
        }
        // apabila player lebih besar dari size bot * mult
        if (playerListS.get(0).getSize() > min_size * mult){
            prio += 5;
        }
        // ide lain liat player yang berkerumun

        return prio;
    }

    public double getPrioEat(GameObject bot, GameState gameState){
        double prio = 0;
        int min_size = bot.getSize();
        int min_distance = 50;

        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        // apabila player lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 2.5;
            if (General.distanceFromPlayerToObject(bot, playerListS.get(0)) <= min_distance){
                prio += 2.5;
            }
        }

        Collections.reverse(playerListS);
        // apabila player terbesar lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 5;
        }

        return prio;
    }

    public PlayerAction defaultAction(GameObject bot, GameState gameState){
        // return playeraction default yaitu Forward ke head makanan atau 0
        var playerAction = new PlayerAction(); 
        var command = PlayerActions.STOP;
        int head = 0;

        var foodList = General.getObjectListDistance(ObjectTypes.FOOD, gameState, bot);

        System.out.print("[");
        for(Object it : foodList) {
            System.out.print(it.toString() + ", ");
        }
        System.out.print("]");

        if (!foodList.isEmpty()) {
            head = General.objectHeading(foodList.get(0), bot);
        } 

        playerAction.action = command;
        playerAction.heading = head;

        return playerAction;
    }


    public PlayerAction basicAttackDistance(PlayerActions command, ObjectTypes object, boolean desc, GameState gameState, GameObject bot){
        // melakukan command dengan heading objek dengan jarak tertentu dari player
        // jika true jarak terdekat false jarak terjauh
        PlayerAction playerAction = new PlayerAction();
        playerAction.setAction(command);
        var objectList = General.getObjectListDistance(object, gameState, bot);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(bot);
        }

        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            playerAction.setHeading(General.objectHeading(objectList.get(0),  bot));
        } else {
            return defaultAction(bot, gameState);
        }

        return playerAction;
    }

    public PlayerAction basicAttackSize(PlayerActions command, ObjectTypes object, boolean desc, GameState gameState, GameObject bot){
        // melakukan command dengan heading objek dengan size tertentu dari player
        // jika true size terkecil false size terbesar
        PlayerAction playerAction = new PlayerAction();
        playerAction.setAction(command);
        var objectList = General.getObjectListSize(object, gameState, bot);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(bot);
        }

        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            playerAction.setHeading(General.objectHeading(objectList.get(0), bot));
        } else {
    
            return defaultAction(bot, gameState);
        }
        return playerAction;
    }

    // bisa pake fungsi default
    public PlayerAction fireTelport(GameObject player, GameObject bot){
        // tembak teleport ke player dari bot
        PlayerAction playerAction = new PlayerAction();
        playerAction.setAction(PlayerActions.FIRETELEPORT);
        playerAction.setHeading(General.objectHeading(player, bot));

        return playerAction;
    }

    public PlayerAction playerTeleport(){
        // melakukan teleport
        PlayerAction playerAction = new PlayerAction();
        playerAction.setAction(PlayerActions.TELEPORT);
        playerAction.setHeading(0);
        return playerAction;
    }

    public PlayerAction detonateSupernova(GameState gameState, GameObject bot){
        // meledakan supernova jika player memasuki radius ledakan
        PlayerAction playerAction = new PlayerAction();
        var command = PlayerActions.DETONATESUPERNOVA;
        int head = 0;

        var playerList = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        playerList.remove(bot);

        var supernovaBombList = General.getObjectListDistance(ObjectTypes.SUPERNOVABOMB, gameState, bot);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (General.distanceFromPlayerToObject(playerList.get(0), supernovaBombList.get(0)) > supernovaRadius) {
                return defaultAction(bot, gameState);
            }
        }

        playerAction.setAction(command);
        playerAction.setHeading(head);
        return playerAction;
    }

    // private int predictHead(GameObject projectile, GameObject player){
    //     // memprediksi heading yang dibutuhkan untuk menembak ke tempat player akan bergerak

    //     int projectile_speed = projectile.getSpeed();
    //     int player_speed = player.getSpeed();
    //     int player_heading = player.currentHeading;

    //     var bot = getBot();

    //     double distanceToPlayer = getDistanceBetween(bot, player);
    //     double timeToPlayer = distanceToPlayer / projectile_speed;
    //     double distanceToPlayerAfterTime = player_speed * timeToPlayer;

    //     double x = player.getPosition().x + distanceToPlayerAfterTime * Math.cos(player_heading);
    //     double y = player.getPosition().y + distanceToPlayerAfterTime * Math.sin(player_heading);

    //     double heading = Math.atan2(y - bot.getPosition().y, x - bot.getPosition().x);

    //     return (int) Math.toDegrees(heading);
    // }

}
