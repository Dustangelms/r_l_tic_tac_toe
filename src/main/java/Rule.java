import java.util.Map;
import java.util.EnumMap;

public
class Rule {

    public static final
    Map<
        Side,
        Side
    >
        sides_next;

    static {
        sides_next =
            new EnumMap<>(Side.class);
        sides_next.put(
            Side.CROSS,
            Side.NAUGHT
        );
        sides_next.put(
            Side.NAUGHT,
            Side.CROSS
        );
    }
}
