import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

public class general {
    public Position getXandYspeed(GameObject projectile) {
        int heading = projectile.currentHeading % 360;
        Position speed;

        if (heading >= 270) {
            heading = heading % 270;
            speed.setX(60 * Math.sin(Math.toRadians(heading)));
            speed.setY(60 * Math.cos(Math.toRadians(heading)));
        } else if (heading >= 180) {
            heading = heading % 180;
            speed.setX(60 * Math.cos(Math.toRadians(heading)));
            speed.setY(60 * Math.sin(Math.toRadians(heading)));
        } else if (heading >= 90) {
            heading = heading % 90;
            speed.setX(60 * Math.sin(Math.toRadians(heading)));
            speed.setY(60 * Math.cos(Math.toRadians(heading)));
        } else {
            speed.setX(60 * Math.cos(Math.toRadians(heading)));
            speed.setY(60 * Math.sin(Math.toRadians(heading)));
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
}
