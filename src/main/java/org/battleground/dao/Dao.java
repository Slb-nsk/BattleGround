package org.battleground.dao;

import org.battleground.entities.Player;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class Dao {

    private final JdbcTemplate jdbcTemplate;

    public Dao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //проверяем, есть ли пользователь с такими именем и паролем
    public int isUserExists(String username, String password) {
        try {
            String p;
            p = jdbcTemplate.queryForObject("SELECT password FROM users WHERE username=?", String.class, username);
            if (p.equals(password)) {
                return 1;
            } else {
                return 2;
            }
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    //заводим нового пользователя
    public void createNewUser(String username, String password) {
        String role = "ROLE_USER";
        jdbcTemplate.update("INSERT INTO users (username,password,enabled) VALUES (?, ?, ?)", username, password, true);
        jdbcTemplate.update("INSERT INTO authorities (username,authority) VALUES (?, ?)", username, role);
        jdbcTemplate.update("INSERT INTO players (name,rating,damage,lifecapacity) VALUES (?,?,?,?)", username, 0, 10, 100);
    }

    public Player playerByName(String name) {
        return jdbcTemplate.queryForObject("SELECT * FROM players WHERE name=?", new RowMapper<Player>() {
            @Override
            public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Player(rs.getString("name"), rs.getInt("rating"), rs.getInt("damage"), rs.getInt("lifeCapacity"));
            }
        }, name);
    }

    //обновление данных игрока
    public void updatePlayer(Player player) {
        String sql = "UPDATE players SET rating = :rating, damage = :damage, lifecapacity = :lifecapacity WHERE name = :name";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("rating", player.getRating());
        parameters.put("damage", player.getDamage());
        parameters.put("lifecapacity", player.getLifeCapasity());
        parameters.put("name", player.getName());
        jdbcTemplate.update(sql, parameters);
    }
}