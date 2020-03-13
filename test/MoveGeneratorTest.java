import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MoveGeneratorTest {
    private List<MoveGenTest> moveGenTests;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("test/resources/testPositions.json")){
            JSONArray tests = (JSONArray) jsonParser.parse(reader);
            moveGenTests = new ArrayList<>();
            tests.forEach(t -> moveGenTests.add(new MoveGenTest((JSONObject) t)));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    void generateMoves() {
        for (MoveGenTest t : moveGenTests) {
            Perft p = new Perft(t.getBoard());
            assertEquals(t.getNodes(), p.perft(t.getDepth()));
        }
    }

    private static class MoveGenTest {
        private int depth;
        private long nodes;
        private Board board;

        public MoveGenTest(JSONObject test) {
            depth = ((Long) test.get("depth")).intValue();
            nodes = (long) test.get("nodes");
            try {
                board = FENParser.parse((String) test.get("fen"));
            } catch (FENParser.FENParseException e) {
                e.printStackTrace();
            }
        }

        public int getDepth() {
            return depth;
        }

        public long getNodes() {
            return nodes;
        }

        public Board getBoard() {
            return board;
        }
    }
}