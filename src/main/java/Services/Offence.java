package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;


// timestamp
// 32:38
// 34:55
// 35:16
// 1:42:22
// 1:49:00

// prio attack
// eat using tp
// fire supernova (depends on situation)
// fire torpedos (when size identical effective)

// circumtances
// asteroid - torpedoes 
// wormhole - projectiles

// when losing (size smallest) try to collect point from food and wormhole
public class Offence extends BotService {

    private int supernovaSize = 10;
    private double supernovaRadius = 0.25 * supernovaSize;

    public Offence(BotService b){
        setBot(getBot());
        setPlayerAction(getPlayerAction());
    }

    public void setOffence(){
        setBot(getBot());
        setPlayerAction(getPlayerAction());
    }

    public int getPrioTorpedoes(){
        // mendapatkan nilai prioritas untuk melakukan aksi torpedo
        int prio = 0;
        int max_distance = 50;
        var bot = getBot();
        int min_size = bot.getSize();

        var playerListD = getObjectListDistance(ObjectTypes.PLAYER);
        var playerListS = getObjectListSize(ObjectTypes.PLAYER);
        playerListD.remove(bot);
        playerListS.remove(bot);

        // apabila player tidak jauh
        if (getDistanceBetween(getBot(), playerListD.get(0)) <= max_distance) {
            prio += 1;
        // apabila player lebih besar dari size bot
        } if (playerListS.get(0).getSize() > min_size){
            prio += 1;
        }

        return prio;
    }


    public int getPrioSupernova(){
        int prio = 0;
        var bot = getBot();
        int min_size = bot.getSize();
        int mult = 10;
        
        var playerListS = getObjectListSize(ObjectTypes.PLAYER);
        playerListS.remove(bot);

        Collections.reverse(playerListS);

        // apabila player lebih besar dari size bot
        if (playerListS.get(0).getSize() > min_size){
            prio += 1;
        }
        // apabila player lebih besar dari size bot * mult
        if (playerListS.get(0).getSize() > min_size * mult){
            prio += 1;
        }
        // ide lain liat player yang berkerumun

        return prio;
    }

    public int getPrioEat(){
        int prio = 0;
        var bot = getBot();
        int min_size = bot.getSize();
        int min_distance = 50;

        var playerListS = getObjectListSize(ObjectTypes.PLAYER);
        playerListS.remove(bot);

        // apabila player lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 1;
            if (getDistanceBetween(bot, playerListS.get(0)) <= min_distance){
                prio += 1;
            }
        }

        Collections.reverse(playerListS);
        // apabila player terbesar lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 10;
        }

        return prio;
    }

    private List<GameObject> distanceFrom(GameObject object, ObjectTypes type){
        // membuat list gameobject tersusun terdekat dari suatu object
        var gameState = getGameState();
        
        var orderedList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(object, item)))
                .collect(Collectors.toList());

        return orderedList;
    }
    
    private List<GameObject> getObjectListDistance(ObjectTypes type){
        // membuat list gameobject tersusun dari yang terdekat terhadap player
        var gameState = getGameState();
        var bot = getBot();

        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return objectList;
    }

    private List<GameObject> getObjectListSize(ObjectTypes type){
        // membuat list gameobject tersusun dari yang terkecil 
        var gameState = getGameState();

        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> item.getSize()))
                .collect(Collectors.toList());

        return objectList;
    }

    public void commandPlayer(PlayerActions command, int head){
        // menset playeraction dari command dan head
        var playerAction = getPlayerAction();
        playerAction.action = command;
        playerAction.heading = head;

        setPlayerAction(playerAction);
    }

    private List<Object> defaultAction(){
        // return list of playeraction default yaitu Forward ke head makanan atau 0
        var command = PlayerActions.FORWARD;
        int head = 0;

        var playerList = getObjectListDistance(ObjectTypes.FOOD);

        if (!playerList.isEmpty()) {
            head = getHeadingBetween(playerList.get(0));
        } 

        return Arrays.asList(command, head);
    }


    public void basicAttackDistance(PlayerActions command, ObjectTypes object, boolean desc){
        // melakukan command dengan heading objek dengan jarak tertentu dari player
        // jika true jarak terdekat false jarak terjauh

        int head = 0;
        var objectList = getObjectListDistance(object);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(getBot());
        }

        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            head = getHeadingBetween(objectList.get(0));
        } else {
            var defaultAction = defaultAction();
            command = (PlayerActions) defaultAction.get(0);
            head = (int) defaultAction.get(1);
        }
        commandPlayer(command, head);
    }

    public void basicAttackSize(PlayerActions action, ObjectTypes object, boolean desc){
        // melakukan command dengan heading objek dengan size tertentu dari player
        // jika true size terkecil false size terbesar

        int head = 0;
        var objectList = getObjectListSize(object);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(getBot());
        }

        if (desc) {
            Collections.reverse(objectList);
        }

        if (!objectList.isEmpty()) {
            head = getHeadingBetween(objectList.get(0));
        } else {
            var defaultAction = defaultAction();
            action = (PlayerActions) defaultAction.get(0);
            head = (int) defaultAction.get(1);
        }
        commandPlayer(action, head);
    }

    // bisa pake fungsi default
    public void fireTelport(GameObject player){
        // tembak teleport ke player
        var command = PlayerActions.FIRE_TELEPORTER;
        int head = getHeadingBetween(player);

        commandPlayer(command, head);
    }

    public void playerTeleport(){
        // melakukan teleport
        commandPlayer(PlayerActions.FIRE_TELEPORTER, 0);
    }

    public void detonateSupernova(){
        // meledakan supernova jika player memasuki radius ledakan
        var command = PlayerActions.DETONATE_SUPERNOVA;
        int head = 0;

        var playerList = getObjectListDistance(ObjectTypes.PLAYER);
        playerList.remove(getBot());

        var supernovaBombList = getObjectListDistance(ObjectTypes.SUPER_NOVA_BOMB);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (getDistanceBetween(playerList.get(0), supernovaBombList.get(0)) > supernovaRadius) {
                var defaultAction = defaultAction();
                command = (PlayerActions) defaultAction.get(0);
                head = (int) defaultAction.get(1);
            }
        }
        commandPlayer(command, head);
    }

    private int predictHead(GameObject projectile, GameObject player){
        // memprediksi heading yang dibutuhkan untuk menembak ke tempat player akan bergerak

        int projectile_speed = projectile.getSpeed();
        int player_speed = player.getSpeed();
        int player_heading = player.currentHeading;

        var bot = getBot();

        double distanceToPlayer = getDistanceBetween(bot, player);
        double timeToPlayer = distanceToPlayer / projectile_speed;
        double distanceToPlayerAfterTime = player_speed * timeToPlayer;

        double x = player.getPosition().x + distanceToPlayerAfterTime * Math.cos(player_heading);
        double y = player.getPosition().y + distanceToPlayerAfterTime * Math.sin(player_heading);

        double heading = Math.atan2(y - bot.getPosition().y, x - bot.getPosition().x);

        return (int) Math.toDegrees(heading);
    }

    private int tickFromDistance(GameObject projectile, GameObject player){
        // menghitung tick yang dibutuhkan projectile untuk menuju player
        return (int) getDistanceBetween(projectile, player) / projectile.getSpeed();
    }

}
