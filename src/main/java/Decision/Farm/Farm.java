package Decision.Farm;

import Enums.*;
import Models.*;

import java.lang.Math;
import java.util.*;
import java.util.stream.*;

import Decision.General.*;


public class Farm {

        public double prio;
        

        public void getFarmPrio(GameObject player, GameState gameState){
                this.prio = 200 + gameState.getWorld().getCurrentTick()*1.1;     
        }


        public double getNearestFood(GameObject player, GameState gameState){
                var foodList = gameState.getGameObjects()
                .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD))
                .sorted(Comparator
                        .comparing(item -> General.distanceFromPlayerToObject(item, player)))
                .collect(Collectors.toList());
                if(foodList.size() == 0){
                        return 9999;
                }else{
                        return General.distanceFromPlayerToObject(foodList.get(0), player);
                }
                
        }

        public PlayerAction normalFarm(PlayerAction command1, GameObject player, GameState gameState){
                PlayerAction command = new PlayerAction();
                        var foodList = gameState.getGameObjects()
                        .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD || item.getGameObjectType()==ObjectTypes.SUPERNOVAPICKUP))
                        .sorted(Comparator
                                .comparing(item -> General.distanceFromPlayerToObject(item, player)))
                        .collect(Collectors.toList());
                        command.heading = General.objectHeading(foodList.get(0), player);
                        command.setAction(PlayerActions.FORWARD);
               return command;
        }
}

