import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class general {
    public Position getXandYspeed(GameObject projectile) {
        int heading = projectile.currentHeading % 360;
        int object_speed = projectile.getSpeed();
        Position speed;

        if (heading >= 270) {
            heading = heading % 270;
            speed.setX(object_speed * Math.sin(Math.toRadians(heading)));
            speed.setY(object_speed * Math.cos(Math.toRadians(heading)));
        } else if (heading >= 180) {
            heading = heading % 180;
            speed.setX(object_speed * Math.cos(Math.toRadians(heading)));
            speed.setY(object_speed * Math.sin(Math.toRadians(heading)));
        } else if (heading >= 90) {
            heading = heading % 90;
            speed.setX(object_speed * Math.sin(Math.toRadians(heading)));
            speed.setY(object_speed * Math.cos(Math.toRadians(heading)));
        } else {
            speed.setX(object_speed * Math.cos(Math.toRadians(heading)));
            speed.setY(object_speed * Math.sin(Math.toRadians(heading)));
        }
        
        return speed;
    }

    public double distanceFromPlayerToProjectileTrajectory(GameObject projectile, GameObject player) {
        int jari_jari = player.getSize/2 + 1; // +1 karena pembagian di java dibulatkan kebawah
        Position projectile_position = projectile.getPosition(); 
        Position player_position = player.getPosition();
        Position n = getXandYspeed(projectile);
        int temp = n.getX();
        n.setX(n.getY());
        n.setY(-1 * temp);
        Position PQ;
        PQ.setX(player_position.getX()-projectile_position.getX());
        PQ.setY(player_position.getY()-projectile_position.getY());

        double distance = Math.abs((PQ.getX()*n.getX() + PQ.getY()*n.getY())/(Math.sqrt(n.getX()*n.getX() + n.getY()*n.getY())));
        distance -= Double.valueOf(jari_jari);

        return distance;
    }

    public double distanceFromPlayerToObject(GameObject object1, GameObject player) {
        var triangleX = Math.abs(object1.getPosition().x - player.getPosition().x);
        var triangleY = Math.abs(object1.getPosition().y - player.getPosition().y);
        return Math.sqrt(triangleX * triangleX + triangleY * triangleY);
    }

    private int radToDegree(double v) {
        return (int) (v * (180 / Math.PI));
    }

    public int objectHeading(GameObject object1, GameObject object2) {
        // mencari derajat heading object2 agar bisa menuju object1
        Position object1_position = object1.getPosition();
        Position object2_position = object2.getPosition();

        int direction = radToDegree(Math.atan2(object1_position.y - object2_position.y, object1_position.x - object2_position.x));

        return (direction + 360) % 360;
    }
}
