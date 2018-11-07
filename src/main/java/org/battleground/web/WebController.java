package org.battleground.web;

import org.battleground.dao.Dao;
import org.battleground.entities.Duel;
import org.battleground.entities.Player;
import org.battleground.logic.Server;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RestController
public class WebController {
    private final Dao dao;
    private final Server server;

    @Autowired
    public WebController(Dao dao, Server server) {
        this.dao = dao;
        this.server = server;
        server.initialize(dao);
    }

    //самый первый вход на сайт
    @GetMapping("/")
    public ModelAndView start() {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("start");
        long timeWorkCode = System.currentTimeMillis() - start;
        mav.addObject("generationTime", timeWorkCode);
        mav.addObject("dbreq", 0);
        mav.addObject("dbTime", 0);
        return mav;
    }

    //авторизация
    @PostMapping("/")
    public ModelAndView autentification(HttpSession session, @RequestParam String username, @RequestParam String password) {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        long dbTimeStart = System.currentTimeMillis();
        long timeWorkDb = 0;
        int dbreq = 1; //счётчик числа обращений к базе данных
        switch (dao.isUserExists(username, password)) {
            case 0:
                server.createNewPlayer(username, password, session);
                mav.setViewName("main");
                timeWorkDb = System.currentTimeMillis() - dbTimeStart; //время обращения к базе данных
                dbreq = +4;
                break;
            case 1:
                server.initializePlayer(username, session);
                mav.setViewName("main");
                timeWorkDb = System.currentTimeMillis() - dbTimeStart; //время обращения к базе данных
                dbreq++;
                break;
            case 2:
                mav.setViewName("start");
                mav.addObject("error", true);
                break;
        }
        long timeWorkCode = System.currentTimeMillis() - start; //время генерации странички
        mav.addObject("generationTime", timeWorkCode);
        mav.addObject("dbreq", dbreq);
        mav.addObject("dbTime", timeWorkDb);
        return mav;
    }

    //выход
    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session) {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("start");
        long timeWorkCode = System.currentTimeMillis() - start;
        mav.addObject("generationTime", timeWorkCode);
        mav.addObject("dbreq", 0);
        mav.addObject("dbTime", 0);
        return mav;
    }

    //главная страница дуэлей
    @GetMapping("/duel")
    public ModelAndView duel(HttpSession session) {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("duels");
        Player player = (Player) session.getAttribute("Player");
        mav.addObject("playerName", player.getName());
        mav.addObject("playerRating", player.getRating());
        session.setAttribute("Time", 31);
        long timeWorkCode = System.currentTimeMillis() - start;
        mav.addObject("generationTime", timeWorkCode);
        mav.addObject("dbreq", 0);
        mav.addObject("dbTime", 0);
        return mav;
    }

    //собственно дуэль
    @GetMapping("/fight")
    public ModelAndView fight(HttpSession session) {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        int time = (Integer) session.getAttribute("Time");
        if (time == 31) {
            mav.setViewName("waiting");
            Player player = (Player) session.getAttribute("Player");
            Duel duel = server.generateDuel(player);
            session.setAttribute("Duel", duel);
            if (player.getName().equals(duel.getPlayer1().getName())) {
                session.setAttribute("PlayerNumber", 1);
                session.setAttribute("Enemy", duel.getPlayer2());
            } else {
                session.setAttribute("PlayerNumber", 2);
                session.setAttribute("Enemy", duel.getPlayer1());
            }
            Player enemy = (Player) session.getAttribute("Enemy");
            mav.addObject("enemyName", enemy.getName());
            session.setAttribute("Time", 30);
            mav.addObject("time", 30);
        } else if (time > 0) {
            mav.setViewName("waiting");
            mav.addObject("time", time);
            Player enemy = (Player) session.getAttribute("Enemy");
            mav.addObject("enemyName", enemy.getName());
            time--;
            session.setAttribute("Time", time);
        } else {
            mav.setViewName("battle");
            Player player = (Player) session.getAttribute("Player");
            Player enemy = (Player) session.getAttribute("Enemy");
            mav.addObject("playerName", player.getName());
            mav.addObject("playerLife", "100");
            mav.addObject("playerLifeCapacity", player.getLifeCapasity());
            mav.addObject("playerDamage", player.getDamage());
            mav.addObject("enemyName", enemy.getName());
            mav.addObject("enemyLife", "100");
            mav.addObject("enemyLifeCapacity", enemy.getLifeCapasity());
            mav.addObject("enemyDamage", enemy.getDamage());
        }
        long timeWorkCode = System.currentTimeMillis() - start;
        mav.addObject("generationTime", timeWorkCode);
        mav.addObject("dbreq", 0);
        mav.addObject("dbTime", 0);
        return mav;
    }

    //Ведение боя
    @GetMapping("/hit")
    public ModelAndView hit(HttpSession session) {
        long start = System.currentTimeMillis();
        ModelAndView mav = new ModelAndView();
        mav.addObject("dbreq", 0);
        mav.addObject("dbTime", 0);
        int time = (Integer) session.getAttribute("Time");
        Duel duel = (Duel) session.getAttribute("Duel");
        int playerNumber = (int) session.getAttribute("PlayerNumber");
        duel.hit(playerNumber);
        if (duel.isPlayerFinishedDuel(playerNumber)) {
            mav.setViewName("main");
            mav.addObject("dbreq", 2);
        } else {
            mav.setViewName("battle");
            Player player = (Player) session.getAttribute("Player");
            Player enemy = (Player) session.getAttribute("Enemy");
            mav.addObject("playerName", player.getName());
            mav.addObject("playerLife", duel.life(playerNumber));
            mav.addObject("playerLifeCapacity", player.getLifeCapasity());
            mav.addObject("playerDamage", player.getDamage());
            mav.addObject("enemyName", enemy.getName());
            mav.addObject("enemyLife", duel.life(3 - playerNumber));
            mav.addObject("enemyLifeCapacity", enemy.getLifeCapasity());
            mav.addObject("enemyDamage", enemy.getDamage());
            mav.addObject("log", duel.getLog());
        }
        long timeWorkCode = System.currentTimeMillis() - start;
        mav.addObject("generationTime", timeWorkCode);
        return mav;
    }
}
