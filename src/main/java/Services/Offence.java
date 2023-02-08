package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class Offence extends BotService {

    
    private List<GameObject> getObjectList(int type){
        var gameState = getGameState();
        var bot = getBot();

        Enum x;
        switch (type) {
            case 1 :
                x = ObjectTypes.PLAYER;
                break;
            case 2 :
                x = ObjectTypes.FOOD;
                break;
            case 3 :
                x = ObjectTypes.WORMHOLE;
                break;
            case 4 :
                x = ObjectTypes.GAS_CLOUD;
                break;
            case 5 :
                x = ObjectTypes.ASTEROID_FIELD;
                break;
            case 6 :
                x = ObjectTypes.TORPEDO_SALVO;
                break;
            case 7 :
                x = ObjectTypes.SUPER_FOOD;
                break;
            case 8 :
                x = ObjectTypes.SUPER_NOVA_PICKUP;
                break;
            case 9 :
                x = ObjectTypes.SUPER_NOVA_BOMB;
                break;
            case 10 :
                x = ObjectTypes.TELEPORTER;
                break;
            case 11 :
                x = ObjectTypes.SHIELD;
                break;
            default :
                x = ObjectTypes.FOOD;
                break;
        }

        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == x)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return objectList;
    }

    public void fireTorpedoes(){
        var playerAction = getPlayerAction();
        playerAction.action = PlayerActions.FIRE_TORPEDOES;

        var playerList = getObjectList(1);
        playerList.remove(0);

        if (!playerList.isEmpty()) {
            playerAction.heading = getHeadingBetween(playerList.get(0));
        } else {
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = 0;
        }

        setPlayerAction(playerAction);
    }

    public void getSupernova(){
        var playerAction = getPlayerAction();
        playerAction.action = PlayerActions.FORWARD;
        playerAction.heading = 0;

        var supernovaList = getObjectList(8);

        if (!supernovaList.isEmpty()) {
            playerAction.heading = getHeadingBetween(supernovaList.get(0));
        } else {
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = 0;
        }

        setPlayerAction(playerAction);
    }

    public void fireSupernova() {
        var playerAction = getPlayerAction();
        playerAction.action = PlayerActions.FIRE_SUPERNOVA;

        var playerList = getObjectList(1);
        playerList.remove(0);

        if (!playerList.isEmpty()) {
            playerAction.heading = getHeadingBetween(playerList.get(0));
        } else {
            playerAction.action = PlayerActions.FORWARD;
            playerAction.heading = 0;
        }

        setPlayerAction(playerAction);
    }

    public void detonateSupernova(){
        var playerAction = getPlayerAction();
        playerAction.action = PlayerActions.DETONATE_SUPERNOVA;

        var playerList = getObjectList(1);
        playerList.remove(0);

        var supernovaBombList = getObjectList(9);

        int radius = 100;

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (getDistanceBetween(playerList.get(0), supernovaBombList.get(0)) > radius) {
                playerAction.action = PlayerActions.FORWARD;
                playerAction.heading = 0;
            }
        }
        setPlayerAction(playerAction);
    }

}
