package decision.offence;

import Enums.*;
import Models.*;
import decision.general.*;

import java.util.*;
import java.util.stream.*;

public class offence {

    private int supernovaSize = 10;
    private double supernovaRadius = 0.25 * supernovaSize;

    public double getprioOffence(GameObject bot, GameState gameState){
        double max = 3;
        double all_prio = 0;

        all_prio += getPrioTorpedoes(bot, gameState);
        all_prio += getPrioSupernova(bot, gameState);
        all_prio += getPrioEat(bot, gameState);

        int kind = (int) all_prio / 10;
        int var = (int) all_prio % 10;

        if (kind == 0) {
            return max;
        } else if (kind == 1){
            if (var == 0){
                return 2;
            } else {
                return 1.5;
            }
        } else if (kind == 2){
            if (var == 0){
                return 1;
            } else if (var >= 5) {
                return 0.5;
            } else {
                return 0.25;
            }
        } else {
            return 0;
        }
    }
    
    public double getPrioTorpedoes(GameObject bot, GameState gameState){
        // mendapatkan nilai prioritas untuk melakukan aksi torpedo
        double prio = 0;
        int max_distance = 50;
        int min_size = bot.getSize();

        var playerListD = getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        var playerListS = getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListD.remove(bot);
        playerListS.remove(bot);

        // apabila player tidak jauh
        if (general.distanceFromPlayerToObject(bot, playerListD.get(0)) <= max_distance) {
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
        
        var playerListS = getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
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

        var playerListS = getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        // apabila player lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 2.5;
            if (general.distanceFromPlayerToObject(bot, playerListS.get(0)) <= min_distance){
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

    private List<GameObject> distanceFrom(GameObject object, ObjectTypes type, GameState gameState){
        // membuat list gameobject tersusun terdekat dari suatu object        
        var orderedList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> general.distanceFromPlayerToObject(object, item)))
                .collect(Collectors.toList());

        return orderedList;
    }
    
    private List<GameObject> getObjectListDistance(ObjectTypes type, GameState gameState, GameObject bot){
        // membuat list gameobject tersusun dari yang terdekat terhadap player
        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> general.distanceFromPlayerToObject(bot, item)))
                .collect(Collectors.toList());

        return objectList;
    }

    private List<GameObject> getObjectListSize(ObjectTypes type, GameState gameState, GameObject bot){
        // membuat list gameobject tersusun dari yang terkecil 
        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> item.getSize()))
                .collect(Collectors.toList());

        return objectList;
    }

    public PlayerAction commandPlayer(PlayerActions command, int head){
        // menset playeraction dari command dan head
        var playerAction = new PlayerAction();
        playerAction.action = command;
        playerAction.heading = head;

        return playerAction;
    }

    private PlayerAction defaultAction(GameObject bot, GameState gameState){
        // return playeraction default yaitu Forward ke head makanan atau 0
        var playerAction = new PlayerAction(); 
        var command = PlayerActions.FORWARD;
        int head = 0;

        var foodList = getObjectListDistance(ObjectTypes.FOOD, gameState, bot);

        // System.out.print("[");
        // for(Object it : foodList) {
        //     System.out.print(it.toString() + ", ");
        // }
        // System.out.print("]");

        if (!foodList.isEmpty()) {
            head = general.objectHeading(foodList.get(0), bot);
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
        var objectList = getObjectListDistance(object, gameState, bot);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(bot);
        }

        // System.out.print("[");
        // for(Object it : objectList) {
        //     System.out.print(it.toString() + ", ");
        // }
        // System.out.print("]");


        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            playerAction.setHeading(general.objectHeading(objectList.get(0),  bot));
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
        var objectList = getObjectListSize(object, gameState, bot);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(bot);
        }

        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            playerAction.setHeading(general.objectHeading(objectList.get(0), bot));
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
        playerAction.setHeading(general.objectHeading(player, bot));

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

        var playerList = getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        playerList.remove(bot);

        var supernovaBombList = getObjectListDistance(ObjectTypes.SUPERNOVABOMB, gameState, bot);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (general.distanceFromPlayerToObject(playerList.get(0), supernovaBombList.get(0)) > supernovaRadius) {
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

    private int tickFromDistance(GameObject projectile, GameObject player){
        // menghitung tick yang dibutuhkan projectile untuk menuju player
        return (int) general.distanceFromPlayerToObject(projectile, player) / projectile.getSpeed();
    }


}
