package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.engine.config.FileParam;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.dto.init.ClientBaseKing;
import ir.sharif.aichallenge.server.logic.dto.init.ClientMap;
import ir.sharif.aichallenge.server.logic.dto.init.InitialMessage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GameHandler implements GameLogic {
    private static final int RESPONSE_TIMEOUT = 300;
    public static final FileParam PARAM_MAP = new FileParam("Map", null, ".*\\.map");

    private Game game;
    private InitialMessage initialMessage;

    @Override
    public int getClientsNum() {
        return 4;
    }

    @Override
    public long getClientResponseTimeout() {
        return RESPONSE_TIMEOUT;
    }

    @Override
    public long getTurnTimeout() {
        return 0;
    }

    @Override
    public void init() {
        String initStr = readMapFile(PARAM_MAP);
        initialMessage = null;
        try {
            initialMessage = Json.GSON.fromJson(initStr, InitialMessage.class);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Invalid map file!");
            System.exit(0);
        }

        game.init(initialMessage);
    }

    private String readMapFile(FileParam paramMap) {
        StringBuilder result = new StringBuilder();
        File mapFile = paramMap.getValue();
        if (mapFile == null || !mapFile.exists()) {
            System.err.println("Invalid map file!");
            System.exit(0);
        }
        try (Scanner in = new Scanner(mapFile)) {
            while (in.hasNext()) {
                result.append(in.nextLine());
                result.append("\n");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Map file not found!");
            System.exit(0);
        }

        return result.toString();
    }

    @Override
    public Message getUIInitialMessage() {
        return null;
    }

    @Override
    public Message[] getClientInitialMessages() {
        Message[] initMessages = new Message[4];

        ClientMap map = initialMessage.getMap();
        List<ClientBaseKing> kings = map.getKings();
        ClientBaseKing[] sortedKings = new ClientBaseKing[4];
        for (ClientBaseKing king: kings) {
            sortedKings[king.getPlayerId()] = king;
        }

        map.setKings(Arrays.asList(sortedKings[0], sortedKings[2], sortedKings[1], sortedKings[3]));
        initMessages[0] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(initialMessage).getAsJsonObject());

        map.setKings(Arrays.asList(sortedKings[2], sortedKings[0], sortedKings[1], sortedKings[3]));
        initMessages[2] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(initialMessage).getAsJsonObject());

        map.setKings(Arrays.asList(sortedKings[1], sortedKings[3], sortedKings[0], sortedKings[2]));
        initMessages[1] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(initialMessage).getAsJsonObject());

        map.setKings(Arrays.asList(sortedKings[3], sortedKings[1], sortedKings[0], sortedKings[2]));
        initMessages[3] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(initialMessage).getAsJsonObject());

        return initMessages;
    }

    @Override
    public void simulateEvents(Map<String, List<ClientMessageInfo>> events) {
        if (game.getCurrentTurn().get() == 0) {
            game.pick(events);
            return;
        }
        game.turn(events);
    }

    @Override
    public void generateOutputs() {

    }

    @Override
    public Message getUIMessage() {
        return null;
    }

    @Override
    public Message getStatusMessage() {
        return null;
    }

    @Override
    public Message[] getClientMessages() {
        Message[] messages = new Message[4];
        if (game.getCurrentTurn().get() == 0) {     //todo check init is 0
            return getClientInitialMessages();
        }

        //todo turn
        return messages;
    }

    @Override
    public boolean isGameFinished() {
        return false;
    }

    @Override
    public void terminate() {

    }
}