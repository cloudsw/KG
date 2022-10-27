package com.kg.backend.consumer.utils;
import com.alibaba.fastjson2.JSONObject;
import com.kg.backend.consumer.WebSocketServer;
import com.kg.backend.pojo.Bot;
import com.kg.backend.pojo.Record;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final Integer rows;
    private final Integer cols;
    private final Integer inner_walls_count;
    private final int[][] g;
    private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
    private final Player playerA, playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private final ReentrantLock lock = new ReentrantLock();
    private String status = "playing";  // playing -> finished
    private String loser = "";  // all: 平局，A: A输，B: B输
    private final static String addBotUrl = "http://127.0.0.1:30002/bot/add/";

    public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Bot botA, Integer idB, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.inner_walls_count = inner_walls_count;
        this.g = new int[rows][cols];
        Integer botIdA = -1, botIdB = -1;
        String botCodeA = "", botCodeB = "";
        if (botA != null) {
            botIdA = botA.getId();
            botCodeA = botA.getContent();
        }
        if (botB != null) {
            botIdB = botB.getId();
            botCodeB = botB.getContent();
        }
        this.playerA = new Player(idA, botIdA, botCodeA, rows - 2, 1, new ArrayList<>());
        this.playerB = new Player(idB, botIdB, botCodeB, 1, cols - 2, new ArrayList<>());
    }

    public Player getPlayerA() {
        return this.playerA;
    }

    public Player getPlayerB() {
        return this.playerB;
    }

    public void setNextStepA(Integer nextStepA) {
        lock.lock();
        try {
            this.nextStepA = nextStepA;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(Integer nextStepB) {
        lock.lock();
        try {
            this.nextStepB = nextStepB;
        } finally {
            lock.unlock();
        }
    }

    public int[][] getG() {
        return g;
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = 1;

        for (int i = 0; i < 4; i ++ ) {
            int x = sx + dx[i], y = sy + dy[i];
            if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                if (check_connectivity(x, y, tx, ty)) {
                    g[sx][sy] = 0;
                    return true;
                }
            }
        }

        g[sx][sy] = 0;
        return false;
    }

    private boolean draw() {  // 画地图
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                g[i][j] = 0;
            }
        }

        for (int r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = 1;
        }
        for (int c = 0; c < this.cols; c ++ ) {
            g[0][c] = g[this.rows - 1][c] = 1;
        }

        Random random = new Random();
        for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(this.rows);
                int c = random.nextInt(this.cols);

                if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                    continue;
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;

                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                break;
            }
        }

        return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (draw())
                break;
        }
    }

    private String getInput(Player player) {
        Player me, you;
        if (playerA.getId().equals(player.getId())) {
            me = playerA;
            you = playerB;
        }else {
            me = playerB;
            you = playerA;
        }

        return getMapString() + "#" +
                me.getSx() + "#" +
                me.getSy() + "#(" +
                me.getStepsString() + "#)" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) return;

        MultiValueMap<String , String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);

    }

    private boolean nextStep() {  // 等待两名玩家的下一步操作
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++ ) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);
        if (g[cell.x][cell.y] == 1) return false;

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                return false;
        }

        for (int i = 0; i < n - 1; i ++ ) {
            if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                return false;
        }

        return true;
    }

    private void judge() {  // 判断两名玩家下一步操作是否合法
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = this.check_valid(cellsA, cellsB);
        boolean validB = this.check_valid(cellsB, cellsA);
        if (!validA || !validB) {
            status = "finished";

            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private void sendAllMessage(String message) {
        if (WebSocketServer.users.get(playerA.getId()) != null)
            WebSocketServer.users.get(playerA.getId()).sendMessage(message);
        if (WebSocketServer.users.get(playerB.getId()) != null)
            WebSocketServer.users.get(playerB.getId()).sendMessage(message);
    }

    private void sendMove() {  // 向两个Client传递移动信息
        lock.lock();
        try {
            JSONObject resp = new JSONObject();
            resp.put("event", "move");
            resp.put("a_direction", this.nextStepA);
            resp.put("b_direction", this.nextStepB);
            this.sendAllMessage(resp.toJSONString());
            this.nextStepA = this.nextStepB = null;
        } finally {
            lock.unlock();
        }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < this.rows; i ++ ) {
            for (int j = 0; j < this.cols; j ++ ) {
                res.append(this.g[i][j]);
            }
        }
        return res.toString();
    }

    private void saveToDatabase() {
        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );

        WebSocketServer.recordMapper.insert(record);
    }

    private void sendResult() {  // 向两个Client公布结果
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", this.loser);
        saveToDatabase();
        this.sendAllMessage(resp.toJSONString());
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++ ) {
            if (this.nextStep()) {  // 是否获取了两条蛇的下一步操作
                this.judge();
                if (this.status.equals("playing")) {
                    this.sendMove();
                } else {
                    this.sendResult();
                    break;
                }
            } else {
                this.status = "finished";
                lock.lock();
                try {
                    if (this.nextStepA == null && this.nextStepB == null) {
                        loser = "all";
                    } else if (this.nextStepA == null) {
                        this.loser = "A";
                    } else {
                        this.loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
