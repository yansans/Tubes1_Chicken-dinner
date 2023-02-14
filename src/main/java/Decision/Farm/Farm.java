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
                //tick: action/second
                //time: num of action
                //100 means most prioritize
                //The concept is if it takes a millenium to get food, we go cowabungga
                //bigger priority means more farming
                //A bit of suggestion, subtract farm priority with other priority
                double time = (getNearestFood(player, gameState)-player.size)/player.getSpeed();
                Integer tick = 60;
                this.prio = 100-(time/tick)*20;
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

        public PlayerAction normalFarm(GameObject player, GameState gameState){
                PlayerAction command = new PlayerAction();
                        var foodList = gameState.getGameObjects()
                        .stream().filter(item -> (item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD))
                        .sorted(Comparator
                                .comparing(item -> General.distanceFromPlayerToObject(item, player)))
                        .collect(Collectors.toList());
                        command.heading = General.objectHeading(foodList.get(0), player);
                        command.setAction(PlayerActions.FORWARD);
                return command;
        }

        public PlayerAction farmInCone(GameObject player, GameState gameState, Position x, int cone){
            /*An idea that farms while moving toward something*/
            /*x: Posistion to go to */
            /*cone: The limited degree for farmming */
            /*Say we are at C,food at B, we go to A*/
            /*Formula  acos((a^2+b^2-c^2)/2ab)*/
            /*a = getDistanceBetween(player.position,item) */
            /*c = getDistanceBetween(item,x) */
            double b = General.distanceFromPlayerToLocation(x, player);
            double temp = Math.toRadians(cone);
            
            PlayerAction command = new PlayerAction();
            var foodList = gameState.getGameObjects()
            .stream().filter((item -> item.getGameObjectType() == ObjectTypes.FOOD || item.getGameObjectType()==ObjectTypes.SUPERFOOD && 
            Math.cos(temp)<(Math.pow(General.distanceFromPlayerToObject(item, player),2)+Math.pow(b, 2)-Math.pow(General.distanceFromPlayerToLocation(x, item),2))/(2*b*General.distanceFromPlayerToObject(item, player))))
            .sorted(Comparator
                    .comparing(item -> General.distanceFromPlayerToObject(item, player)))
            .collect(Collectors.toList());
            command.heading = General.objectHeading(foodList.get(0), player);
            command.setAction(PlayerActions.FORWARD);
            return command;
        }
}

