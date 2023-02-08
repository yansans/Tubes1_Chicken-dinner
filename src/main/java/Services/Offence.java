package Services;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class Offence extends BotService {

    private int supernovaSize = 10;
    private double supernovaRadius = 0.25 * supernovaSize;

    private List<GameObject> getObjectList(ObjectTypes type){
        var gameState = getGameState();
        var bot = getBot();

        var objectList = gameState.getGameObjects()
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> getDistanceBetween(bot, item)))
                .collect(Collectors.toList());

        return objectList;
    }

    public void commandPlayer(PlayerActions command, int head){
        var playerAction = getPlayerAction();
        playerAction.action = command;
        playerAction.heading = head;

        setPlayerAction(playerAction);
    }

    private List<Object> defaultAction(){
        var action = PlayerActions.FORWARD;
        int head = 0;

        var playerList = getObjectList(ObjectTypes.FOOD);

        if (!playerList.isEmpty()) {
            head = getHeadingBetween(playerList.get(0));
        } 

        return Arrays.asList(action, head);
    }


    public void basicAttack(PlayerActions action, ObjectTypes object){
        int head = 0;
        var objectList = getObjectList(object);

        if(object == ObjectTypes.PLAYER){
            objectList.remove(0);
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

    public void detonateSupernova(){
        var action = PlayerActions.DETONATE_SUPERNOVA;
        int head = 0;

        var playerList = getObjectList(ObjectTypes.PLAYER);
        playerList.remove(0);

        var supernovaBombList = getObjectList(ObjectTypes.SUPER_NOVA_BOMB);

        if (!playerList.isEmpty() && !supernovaBombList.isEmpty()) {
            if (getDistanceBetween(playerList.get(0), supernovaBombList.get(0)) > supernovaRadius) {
                action = PlayerActions.FORWARD;
                head = 0;
            }
        }
        commandPlayer(action, head);
    }

    private int predictHead(GameObject projectile, GameObject player){

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

}
