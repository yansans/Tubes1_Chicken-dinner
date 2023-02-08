import Enums.*;
import Models.*;
import general.*;

import java.util.*;
import java.util.stream.*;

public class defense {
    public double prio;

    public void getDefensePrio(GameObject player) {
        double min;
        int jari_jari = player.getSize/2 + 1; // +1 karena pembagian di java dibulatkan kebawah
        // melihat 
        var torpedoList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDO_SALVO)
            .sorted(Comparator.comparing(item -> distanceFromPlayerToProjectileTrajectory(item, player) - jari_jari))       
            .collect(Collectors.toList());

        if (torpedoList.size() == 0) {
            min = 100; // asumsi diurutkan membesar
        } else {
            min = distanceFromPlayerToProjectileTrajectory(torpedoList.get(0), player);
        }

        this.prio = min;
    }
}
