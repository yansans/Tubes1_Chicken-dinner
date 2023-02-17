package Decision.General;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class General {
    public static double distanceFromPlayerToProjectileTrajectory(GameObject projectile, GameObject player) {
        int jari_jari = player.getSize()/2 + 1; // +1 karena pembagian di java dibulatkan kebawah
        Position projectile_position = projectile.getPosition(); 
        Position player_position = player.getPosition();

        double nx;
        double ny;

        int heading = projectile.currentHeading % 360;
        int object_speed = projectile.getSpeed();

        if (heading >= 270) {
            heading = heading % 270;
            nx = (object_speed * Math.sin(Math.toRadians(heading)));
            ny = (object_speed * Math.cos(Math.toRadians(heading)));
        } else if (heading >= 180) {
            heading = heading % 180;
            nx = (object_speed * Math.cos(Math.toRadians(heading)));
            ny = (object_speed * Math.sin(Math.toRadians(heading)));
        } else if (heading >= 90) {
            heading = heading % 90;
            nx = (object_speed * Math.sin(Math.toRadians(heading)));
            ny = (object_speed * Math.cos(Math.toRadians(heading)));
        } else {
            nx = (object_speed * Math.cos(Math.toRadians(heading)));
            ny = (object_speed * Math.sin(Math.toRadians(heading)));
        }
        double temp = nx;
        nx = ny;
        ny = (-1 * temp);
        Position PQ = new Position();
        PQ.setX(player_position.getX()-projectile_position.getX());
        PQ.setY(player_position.getY()-projectile_position.getY());

        double distance = Math.abs((PQ.getX()*nx + PQ.getY()*ny)/(Math.sqrt(nx*nx + ny*ny)));
        distance -= Double.valueOf(jari_jari);
        return distance;
    }

    public static double distanceFromPlayerToObject(GameObject object1, GameObject player) {
        var triangleX = Math.abs(object1.getPosition().x - player.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - player.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    public static double distanceFromPlayerToPlayer(GameObject player1, GameObject player2) {
        var triangleX = Math.abs(player1.getPosition().x - player2.getPosition().x);
        var triangleY = Math.abs(player1.getPosition().y - player2.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY) - player1.getSize() - player2.getSize();
    }

    public static double distanceFromPlayerToLocation(Position x, GameObject player) {
        var triangleX = Math.abs(x.x - player.getPosition().x);
        var triangleY = Math.abs(x.y - player.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private static int radToDegree(double v) {
        return (int) (v * (180 / Math.PI));
    }

    public static int objectHeading(GameObject object1, GameObject object2) {
        // mencari derajat heading object2 agar bisa menuju object1
        Position object1_position = object1.getPosition();
        Position object2_position = object2.getPosition();

        int direction = radToDegree(Math.atan2(object1_position.getY() - object2_position.getY(), object1_position.getX() - object2_position.getX()));

        return (direction + 360) % 360;
    }

    public static int objectHeadingtoPoint(int x, int y, GameObject object2) {
        // mencari derajat heading object2 agar bisa menuju object1
        Position object2_position = object2.getPosition();

        int direction = radToDegree(Math.atan2(y - object2_position.getY(), x - object2_position.getX()));

        return (direction + 360) % 360;
    }

    public static List<GameObject> distanceFrom(GameObject object, ObjectTypes type, GameState gameState){
        // membuat list gameobject tersusun terdekat dari suatu object    
        List<GameObject> listObject;
        if (type == ObjectTypes.PLAYER){
            listObject = gameState.getPlayerGameObjects();
        } else {
            listObject = gameState.getGameObjects();
        }
        var orderedList = listObject
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> General.distanceFromPlayerToObject(object, item)))
                .collect(Collectors.toList());

        return orderedList;
    }
    
    public static List<GameObject> getObjectListDistance(ObjectTypes type, GameState gameState, GameObject bot){
        // membuat list gameobject tersusun dari yang terdekat terhadap player
        List<GameObject> listObject ,orderedList;

        if (type == ObjectTypes.PLAYER){
            listObject = gameState.getPlayerGameObjects();
            orderedList = getObjectPlayerDistance(gameState, bot);
        } else {
            listObject = gameState.getGameObjects();
            orderedList = listObject
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> General.distanceFromPlayerToObject(bot, item)))
                .collect(Collectors.toList());

        }
        return orderedList;
    }

    public static List<GameObject> getObjectPlayerDistance(GameState gameState, GameObject bot){
        // membuat list gameobject tersusun dari yang terdekat terhadap player
        List<GameObject> listObject;
        listObject = gameState.getPlayerGameObjects();
        var orderedList = listObject
                .stream().filter(item -> item.getGameObjectType() == ObjectTypes.PLAYER)
                .sorted(Comparator
                        .comparing(item -> General.distanceFromPlayerToObject(bot, item)-bot.getSize()-item.getSize()))
                .collect(Collectors.toList());

        return orderedList;
    }

    public static List<GameObject> getObjectListSize(ObjectTypes type, GameState gameState, GameObject bot){
        // membuat list gameobject tersusun dari yang terkecil 
        List<GameObject> listObject;
        if (type == ObjectTypes.PLAYER){
            listObject = gameState.getPlayerGameObjects();
        } else {
            listObject = gameState.getGameObjects();
        }
        var orderedList = listObject
                .stream().filter(item -> item.getGameObjectType() == type)
                .sorted(Comparator
                        .comparing(item -> item.getSize()))
                .collect(Collectors.toList());

        return orderedList;
    }

    public static int tickFromDistance(GameObject projectile, GameObject player){
        // menghitung tick yang dibutuhkan projectile untuk menuju player
        return (int) General.distanceFromPlayerToObject(projectile, player) / projectile.getSpeed();
    }

    public static boolean isItHeadingTowards(GameObject object1, GameObject object2) {
        int heading = object1.currentHeading % 360;
        int x = object2.getPosition().getX();
        int y = object2.getPosition().getY();
        int x2 = object2.getPosition().getX();
        int y2 = object2.getPosition().getY();

        if (heading >= 270) {
            if (x2 >= x & y2 <= y) {
                return true;
            } else {
                return false;
            }
        } else if (heading >= 180) {
            if (x2 <= x & y2 <= y) {
                return true;
            } else {
                return false;
            }
        } else if (heading >= 90) {
            if (x2 <= x & y2 >= y) {
                return true;
            } else {
                return false;
            }
        } else {
            if (x2 >= x & y2 >= y) {
                return true;
            } else {
                return false;
            }
        }
    }
}
