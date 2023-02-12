package Decision.Defense;

import Enums.*;
import Models.*;

import java.util.*;
import java.util.stream.*;

import Decision.General.*;

public class Defense {
    public double prio;

    public void getDefensePrio(GameObject player, GameState gameState) {
        // mendeteksi jika bot dekat dengan lintasan torpedo, regardless jumlah torpedo yang mendekatinya
        // jika ada satu saja torpedo yang mendekati bot, maka bot akan mengeluarkan shield
        double min;
        int jari_jari = player.getSize()/2 + 1; // +1 karena pembagian di java dibulatkan kebawah
        // melihat 
        var torpedoList = gameState.getGameObjects()
            .stream().filter(item -> item.getGameObjectType() == ObjectTypes.TORPEDOSALVO)
            .sorted(Comparator.comparing(item -> General.distanceFromPlayerToProjectileTrajectory(item, player) - jari_jari))       
            .collect(Collectors.toList());

        if (torpedoList.size() == 0) {
            min = 100; // asumsi diurutkan membesar
        } else {
            min = General.distanceFromPlayerToProjectileTrajectory(torpedoList.get(0), player);
        }

        this.prio = min;
    }
}
