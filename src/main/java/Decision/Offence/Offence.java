package Decision.Offence;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.General.*;

public class Offence {

    private int supernovaSize = 10;
    private double supernovaRadius = 0.25 * supernovaSize;
    private int min_size = 15;
    public double prio_gen;
    private double kind;
    private static boolean isSupernovaing = false;

    private GameObject bot;
    private GameState gameState;

    public Offence(GameObject bot, GameState gameState){
        this.bot = bot;
        this.gameState = gameState;
    }

    public void getPrioOffence(){
        //Procedure yang menghitung priority offence dan menyimpannya di prio_gen
        var playerList = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        playerList.remove(bot);
        if (playerList.size() == 0){
            prio_gen = 1000;
            return;
        } if (isSupernovaing){
            prio_gen = 0;
            return;
        }
        var player = playerList.get(0);
        double distance = General.distanceFromPlayerToObject(bot, player);
        prio_gen = distance;
    }

    public PlayerAction doOffence(){
        // Fungsi yang mengembalikan aksi terhadap kind yang didapatkan
        var enemy = ObjectTypes.PLAYER;
        var action = PlayerActions.FIRETORPEDOES;

        if (kind == 1){
            action = PlayerActions.FIRETORPEDOES;
        } else if (kind == 2){
            if (!isSupernovaing){
                isSupernovaing = true;
                return basicAttackSize(PlayerActions.FIRESUPERNOVA, enemy, true);
            } else {
                isSupernovaing = false;
                return detonateSupernova();
            }
        } else if (kind == 3){
            action = PlayerActions.FIRETELEPORT;
        }
        return basicAttackSize(action, enemy, false);
    }

    public void getPrioType(){
        double prio_tor = getPrioTorpedoes();
        double prio_sup = getPrioSupernova();
        double prio_eat = getPrioEat();

        if (prio_tor > prio_sup && prio_tor > prio_eat){
            kind = 1;
        } else if (prio_sup > prio_tor && prio_sup > prio_eat){
            kind = 2;
        } else if (prio_eat > prio_tor && prio_eat > prio_sup){
            kind = 3;
        } else {
            kind = 0;
        }
    }
    
    public double getPrioTorpedoes(){
        // mendapatkan nilai prioritas untuk melakukan aksi torpedo
        // max prio 10
        // min prio 5
        double prio = 0;
        int max_distance = 50;
        int min_size_attack = 25;

        var playerListD = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListD.remove(bot);
        playerListS.remove(bot);

        if (playerListD.size() == 0){
            return 0;
        }
        if (bot.getSize() < min_size){
            return 0;
        }

        // apabila player tidak jauh
        if (General.distanceFromPlayerToObject(bot, playerListD.get(0)) <= max_distance) {
            prio += 5;
        // apabila player lebih besar dari size bot
        } if (playerListS.get(0).getSize() > min_size_attack){
            prio += 5;
        }

        return prio;
    }

    public double getPrioSupernova(){
        // mendapatkan nilai prioritas untuk melakukan aksi supernova
        // max prio 220
        // min prio 20
        double prio = 0;
        int mult = 2;

        if (bot.supernovaAvailable == 0){
            return 0;
        }
        prio += 10;
        if (checkNearSupernova()){
            prio += 100;
        }

        return prio * mult;
    }

    public double getPrioEat(){
        // mendapatkan nilai prioritas untuk melakukan aksi makan
        // max prio 45
        // min prio 15
        double prio = 0;
        int min_size = bot.getSize();
        int min_distance = 50;
        int mult = 3;

        var playerListS = General.getObjectListSize(ObjectTypes.PLAYER, gameState, bot);
        playerListS.remove(bot);

        if (playerListS.size() == 0){
            return 0;
        }

        // apabila player lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 5;
            if (General.distanceFromPlayerToObject(bot, playerListS.get(0)) <= min_distance){
                prio += 5;
            }
        }

        Collections.reverse(playerListS);
        // apabila player terbesar lebih kecil dari size bot
        if (playerListS.get(0).getSize() < min_size){
            prio += 5;
        }

        return prio * mult;
    }

    public PlayerAction defaultAction(){
        // return playeraction default yaitu STOP ke head makanan atau 0
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

    public PlayerAction fireTeleport(GameObject player){
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

    public boolean checkNearSupernova(){
        // cek apakah ada player yang dekat supernova
        var playerList = General.getObjectListDistance(ObjectTypes.PLAYER, gameState, bot);
        playerList.remove(bot);

        var supernovaBombList = General.getObjectListDistance(ObjectTypes.SUPERNOVABOMB, gameState, bot);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            return (General.distanceFromPlayerToObject(playerList.get(0), supernovaBombList.get(0)) <= supernovaRadius);
        } else 
            return false;
    }

    public PlayerAction detonateSupernova(){
        // meledakan supernova jika player memasuki radius ledakan
        PlayerAction playerAction = new PlayerAction();
        var command = PlayerActions.DETONATESUPERNOVA;
        int head = 0;

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
}
