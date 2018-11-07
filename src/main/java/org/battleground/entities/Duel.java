package org.battleground.entities;

//Класс, в котором происходит дуэль: ожидаются действия игроков, и происходит определение победителя

import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

public class Duel {
    private Player player1, player2; //участники дуэли
    private Integer life1, life2, damage1, damage2;
    private ArrayList<String> log;
    private Boolean player1FinishedDuel, player2FinishedDuel;

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Integer getLife1() {
        return life1;
    }

    public void setLife1(Integer life1) {
        this.life1 = life1;
    }

    public Integer getLife2() {
        return life2;
    }

    public void setLife2(Integer life2) {
        this.life2 = life2;
    }

    public ArrayList<String> getLog() {
        return log;
    }

    public void setLog(ArrayList<String> log) {
        this.log = log;
    }

    public Boolean getPlayer1FinishedDuel() {
        return player1FinishedDuel;
    }

    public void setPlayer1FinishedDuel(Boolean player1FinishedDuel) {
        this.player1FinishedDuel = player1FinishedDuel;
    }

    public Boolean getPlayer2FinishedDuel() {
        return player2FinishedDuel;
    }

    public void setPlayer2FinishedDuel(Boolean player2FinishedDuel) {
        this.player2FinishedDuel = player2FinishedDuel;
    }

    public Duel(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        life1 = player1.getLifeCapasity();
        life2 = player2.getLifeCapasity();
        damage1 = player1.getDamage();
        damage2 = player2.getDamage();
        log = new ArrayList<>();
        player1FinishedDuel = player2FinishedDuel = false;
    }

    public void hit(int playerNumber) {
        if (life1 > 0 && life2 > 0) {
            switch (playerNumber) {
                case 1:
                    life2 -= damage1;
                    log.add(player1.getName() + " нанёс ударом урон " + player1.getDamage());
                    if (life2 < 0) {
                        life2 = 0;
                        log.add(player2.getName() + " убит");
                    }
                    break;
                case 2:
                    life1 -= damage2;
                    log.add(player2.getName() + " нанёс ударом урон " + player2.getDamage());
                    if (life1 < 0) {
                        life1 = 0;
                        log.add(player1.getName() + " убит");
                    }
                    break;
            }
        } else {
            switch (playerNumber) {
                case 1:
                    player1FinishedDuel = true;
                    break;
                case 2:
                    player2FinishedDuel = true;
                    break;
            }
        }
    }

    public boolean isPlayerFinishedDuel(int playerNumber) {
        boolean result = false;
        switch (playerNumber) {
            case 1:
                result = player1FinishedDuel;
                break;
            case 2:
                result = player2FinishedDuel;
                break;
        }
        return result;
    }

    public int life(int playerNumber) {
        int result = 0;
        switch (playerNumber) {
            case 1:
                result = life1;
                break;
            case 2:
                result = life2;
                break;
        }
        return result;
    }
}
