package org.battleground.logic;

import org.battleground.dao.Dao;
import org.battleground.entities.Duel;
import org.battleground.entities.Player;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;


//класс для генерации дуэлей
class DuelGenerator extends Thread {
    private LinkedBlockingQueue<Player> playersReadyForDuel;
    private HashSet<Duel> duelsHashSet;

    public DuelGenerator(LinkedBlockingQueue<Player> playersReadyForDuel, HashSet<Duel> duelsHashSet) {
        this.playersReadyForDuel = playersReadyForDuel;
        this.duelsHashSet = duelsHashSet;
    }

    @Override
    public void run() {
        while (true) {
            Player player1, player2;
            Duel duel;
            try {
                player1 = playersReadyForDuel.take();
                player2 = playersReadyForDuel.take();
                duel = new Duel(player1, player2);
                duelsHashSet.add(duel);
            } catch (InterruptedException ignored) {
            }
        }
    }
}

//класс для подведения итогов завершившихся дуэлей и их удаления из памяти
class DuelGarbageColector extends Thread {
    private Dao dao;
    private HashSet<Duel> duelsHashSet;

    public DuelGarbageColector(Dao dao, HashSet<Duel> duelsHashSet) {
        this.dao = dao;
        this.duelsHashSet = duelsHashSet;
    }

    @Override
    public void run() {
        while (true) {
            if (!duelsHashSet.isEmpty()) {
                for (Duel duel : duelsHashSet) {
                    if (duel.getPlayer1FinishedDuel() && duel.getPlayer2FinishedDuel()) {
                        Player winner, looser;
                        if (duel.getLife1() == 0) {
                            winner = duel.getPlayer2();
                            looser = duel.getPlayer1();
                        } else {
                            winner = duel.getPlayer1();
                            looser = duel.getPlayer2();
                        }
                        winner.setRating(winner.getRating() + 1);
                        looser.setRating(looser.getRating() - 1);
                        winner.setDamage(winner.getDamage() + 1);
                        looser.setDamage(looser.getDamage() + 1);
                        winner.setLifeCapasity(winner.getLifeCapasity() + 1);
                        looser.setLifeCapasity(looser.getLifeCapasity() + 1);
                        dao.updatePlayer(winner);
                        dao.updatePlayer(looser);
                        duelsHashSet.remove(duel);
                    }
                }
            } else {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }

    }
}

//Класс, занимающийся обработкой деятельности игроков
@Component
public class Server {
    private Dao dao;
    private LinkedBlockingQueue<Player> playersReadyForDuel; //очередь, в которую помещаются игроки, готовые к дуэли
    private HashSet<Duel> duelsHashSet; //текущие дуэли


    //запускаем процессы, которые будут отслеживать текущую деятельность
    public void initialize(Dao dao) {
        this.dao = dao;

        playersReadyForDuel = new LinkedBlockingQueue<>();
        duelsHashSet = new HashSet<>();

        DuelGenerator duelGenerator = new DuelGenerator(playersReadyForDuel, duelsHashSet);
        duelGenerator.start();

        DuelGarbageColector duelGarbageColector = new DuelGarbageColector(dao, duelsHashSet);
        duelGarbageColector.start();

        //болванка для тестирования
        Player dump = dao.playerByName("dump");
        playersReadyForDuel.add(dump);
    }

    //заведение нового пользователя
    public void createNewPlayer(String username, String password, HttpSession session) {
        dao.createNewUser(username, password);
        initializePlayer(username, session);
    }

    //подключение существующего пользователя
    public void initializePlayer(String username, HttpSession session) {
        Player player = dao.playerByName(username);
        session.setAttribute("Player", player);
    }


    public Duel generateDuel(Player player) {
        playersReadyForDuel.add(player);
        while (true) {
            for (Duel d : duelsHashSet) {
                if ((d.getPlayer1() == player) || (d.getPlayer2() == player)) {
                    return d;
                }
            }
        }

    }

}
