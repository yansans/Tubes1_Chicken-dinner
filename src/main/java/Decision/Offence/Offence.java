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
    private static int fuel;

    private GameObject bot;
    private GameState gameState;

    public Offence(GameObject bot, GameState gameState){
        this.bot = bot;
        this.gameState = gameState;
        fuel += fuel;
    }

    public void getPrioGeneral(){
        double max = 10;
        prio = getPrioType() < 3 ? 0.0 : max;
    }

    public PlayerAction doOffence(){
        var enemy = ObjectTypes.PLAYER;
        var action = PlayerActions.FIRETORPEDOES;
        int min_size = 25;
        System.out.println("fuel " + fuel);
        if (bot.size < min_size || fuel <= 0){
            action = PlayerActions.FORWARD;
            enemy = ObjectTypes.FOOD;
            fuel += bot.getSize() / 10;
        }
        if (kind == 2){
            action = PlayerActions.FIRESUPERNOVA;
        } else if (kind == 3){
            action = PlayerActions.FIRETELEPORT;
        }
        fuel -= 5;
        return basicAttackDistance(action, enemy, false);
    }

    public double getPrioType(){
        double max = 3;
        double all_prio = 0;

        all_prio += getPrioTorpedoes();
        all_prio += getPrioSupernova();
        all_prio += getPrioEat();

        kind = (int) all_prio / 10;
        var = (int) all_prio % 10;

        return max - kind - var;
    }
    
    public double getPrioTorpedoes(){
        // mendapatkan nilai prioritas untuk melakukan aksi torpedo
        double prio = 0;
        int max_distance = 50;
        int min_size = 25;

        var playerListD = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListD.remove(bot);
        playerListS.remove(bot);

        if (playerListD.size() == 0){
            return 0;
        }

        // apabila player tidak jauh
        if (General.distanceFromPlayerToObject(bot, playerListD.get(0)) <= max_distance) {
            prio += 5;
        // apabila player lebih besar dari size bot
        } if (playerListS.get(0).getSize() > min_size){
            prio += 5;
        }

        return prio;
    }


    public double getPrioSupernova(){
        double prio = 0;
        int min_size = bot.getSize();
        int mult = 10;
        
        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        Collections.reverse(playerListS);

        if (playerListS.size() == 0){
            return 0;
        }

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

    public double getPrioEat(){
        double prio = 0;
        int min_size = bot.getSize();
        int min_distance = 50;

        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        if (playerListS.size() == 0){
            return 0;
        }

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

    public PlayerAction defaultAction(){
        // return playeraction default yaitu Forward ke head makanan atau 0
        var playerAction = new PlayerAction(); 
        var command = PlayerActions.STOP;
        int head = 0;

        var foodList = General.getObjectListDistance(ObjectTypes.FOOD, gameState, bot);

        if (!foodList.isEmpty()) {
            head = General.objectHeading(foodList.get(0), bot);
            command = PlayerActions.FORWARD;
        } 

        playerAction.action = command;
        playerAction.heading = head;
        System.out.println("default offence");
        return playerAction;
    }


    public PlayerAction basicAttackDistance(PlayerActions command, ObjectTypes object, boolean desc){
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
            return defaultAction();
        }

        return playerAction;
    }

    public PlayerAction basicAttackSize(PlayerActions command, ObjectTypes object, boolean desc){
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
    
            return defaultAction();
        }
        return playerAction;
    }

    // bisa pake fungsi default
    public PlayerAction fireTelport(GameObject player){
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

    private boolean checkSupernova(){
        // cek apakah ada supernova pickup di world
        var supernovaPickupList = General.getObjectListDistance(ObjectTypes.SUPERNOVAPICKUP, gameState, bot);
        return !supernovaPickupList.isEmpty();
        
    }

    public PlayerAction attackSupernova(PlayerAction before, int supernova_phase){
        System.out.println(supernova_phase);
        if (bot.supernovaAvailable == 1 && supernova_phase == 0) {
            System.out.println("Mendapatkan supernova pickup");
            return basicAttackDistance(PlayerActions.STOP, ObjectTypes.PLAYER, false);
        }
        if (supernova_phase == 0){
            // default phase : tidak punya supernova
            // cek apakah ada supernova pickup di world
            if (checkSupernova()){
                // jika ada, ambil supernova pickup
                System.out.println("Menuju supernova pickup");
                return basicAttackDistance(PlayerActions.FORWARD, ObjectTypes.SUPERNOVAPICKUP, false);
            }
        } else if (supernova_phase == 1){
                // sudah punya supernova
                // menembak supernova ke lawan
                System.out.println("Menembak supernova");
                return basicAttackDistance(PlayerActions.FIRESUPERNOVA, ObjectTypes.PLAYER, false);
                
        } else if (supernova_phase == 2){
            // menunggu supernova ledakan
            System.out.println("Meledakan supernova");
            return detonateSupernova();
        }
        return before;
    }

    public PlayerAction detonateSupernova(){
        // meledakan supernova jika player memasuki radius ledakan
        PlayerAction playerAction = new PlayerAction();
        var command = PlayerActions.DETONATESUPERNOVA;
        int head = 0;

        var playerList = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        playerList.remove(bot);

        var supernovaBombList = General.getObjectListDistance(ObjectTypes.SUPERNOVABOMB, gameState, bot);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (General.distanceFromPlayerToObject(playerList.get(0), supernovaBombList.get(0)) > supernovaRadius) {
                return defaultAction();
            }
        }

        playerAction.setAction(command);
        playerAction.setHeading(head);
        return playerAction;
    }

    public PlayerAction chasePlayer(GameState gameState){
        // meledakan supernova jika player memasuki radius ledakan
        PlayerAction playerAction = new PlayerAction();

        var playerlist = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER & item.getSize() <= 0.75*bot.getSize())
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToObject(item, bot)))
            .collect(Collectors.toList());

        if (bot.getEffect() == 1 || bot.getEffect() == 3 || bot.getEffect() == 5 || bot.getEffect() == 7) {
            // ketika afterburner telah dihidupkan
            playerAction.setAction(PlayerActions.FORWARD);
            playerAction.setHeading(General.objectHeading(playerlist.get(1), bot));
        } else {
            // hidupkan afterburner untuk mengejar
            playerAction.setAction(PlayerActions.STARTAFTERBURNER);
            playerAction.setHeading(General.objectHeading(playerlist.get(1), bot));
        }

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
