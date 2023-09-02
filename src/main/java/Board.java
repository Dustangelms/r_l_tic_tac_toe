import java.util.Arrays;
import java.util.Optional;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.EnumSet;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Stream;
import java.util.stream.IntStream;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;

@RequiredArgsConstructor(
    access =
        AccessLevel.PRIVATE
)
@FieldDefaults(
    level =
        AccessLevel.PRIVATE,
    makeFinal = true
)
public class Board {

    private static final
    int     W = 3;

    private static final
    int     H = 3;

    private static final
    int     COUNT_CELLS = W * H;

    private static
    int     get_index_cell(
        int     index_row,
        int     index_column
    ) {
        return
            index_row * H
                +
            index_column;
    }

    public
    enum Mark {

        NONE,
        CROSS,
        NAUGHT
    }

    private static final
    Map<Side, Mark>
        marks_by_side;

    private static final
    Map<Mark, Side>
        sides_by_mark;

    static {
        marks_by_side =
            new EnumMap<>(Side.class);
        marks_by_side.put(
            Side.CROSS,
            Mark.CROSS
        );
        marks_by_side.put(
            Side.NAUGHT,
            Mark.NAUGHT
        );

        sides_by_mark =
            new EnumMap<>(Mark.class);
        sides_by_mark.put(
            Mark.NONE,
            null
        );
        sides_by_mark.put(
            Mark.CROSS,
            Side.CROSS
        );
        sides_by_mark.put(
            Mark.NAUGHT,
            Side.NAUGHT
        );
    }

    @RequiredArgsConstructor(
        access =
            AccessLevel.PRIVATE
    )
    @FieldDefaults(
        level =
            AccessLevel.PRIVATE,
        makeFinal = true
    )
    @EqualsAndHashCode
    public static
    class Key {

        List<Mark>
            marks_cell;
    }

    private static final
    List<int[]> AREAS_WIN =
        Arrays.asList(
            new int[]{ 0, 1, 2 },
            new int[]{ 3, 4, 5 },
            new int[]{ 6, 7, 8 },
            new int[]{ 0, 3, 6 },
            new int[]{ 1, 4, 7 },
            new int[]{ 2, 5, 8 },
            new int[]{ 0, 4, 8 },
            new int[]{ 2, 4, 6 }
        );

    private static final
    Board   INSTANCE_INITIAL =
        new Board(
            new Key(
                Collections.nCopies(
                    COUNT_CELLS,
                    Mark.NONE
                )
            ),
            0,
            Side.CROSS
        );

    public static
    Board   get_instance_initial() {
        return INSTANCE_INITIAL;
    }

    Key
        key;

    public
    Key
    get_key() {
        return key;
    }

    int     count_marks;

    Side
        side_move;

    public
    Side
    get_side_move() {
        return side_move;
    }

    public
    Optional<Side>
    calc_side_won() {
        for (
            int[]   area_win :
                AREAS_WIN
        ) {
            final
            Set<Mark>
                marks_area_win =
                    EnumSet.noneOf(Mark.class);
            for (
                int     index_cell_area_win :
                    area_win
            ) {
                marks_area_win.add(
                    key.marks_cell
                        .get(index_cell_area_win)
                );
            }
            if (marks_area_win.size() == 1) {
                final
                Mark
                    mark_win =
                        marks_area_win.iterator().next();
                if (
                    mark_win
                        ==
                    Mark.CROSS
                        ||
                    mark_win
                        ==
                    Mark.NAUGHT
                ) {
                    return Optional.of(
                        sides_by_mark
                            .get(mark_win)
                    );
                }
            }
        }
        return Optional.empty();
    }

    public
    boolean is_full() {
        return
            count_marks
                ==
            COUNT_CELLS;
    }

    public
    Stream<Move>
    get_moves_valid() {
        return
            IntStream.range(0, COUNT_CELLS)
                .filter(this::is_move_valid)
                .mapToObj(Move::new);
    }

    public
    boolean is_move_valid(
        Move
            move
    ) {
        return
            is_move_valid
                (move.index_cell());
    }

    private
    boolean is_move_valid(
        int     index_cell_move
    ) {
        final
        Mark
            mark_cell =
                key.marks_cell
                    .get(index_cell_move);
        return
            mark_cell
                ==
            Mark.NONE;
    }

    public
    Board   make_move_unsafe(
        Move
            move
    ) {
        final
        List<Mark> marks_cell_new =
            new ArrayList<>(key.marks_cell);
        marks_cell_new.set(
            move.index_cell(),
            marks_by_side
                .get(side_move)
        );
        return
            new Board(
                new Key(marks_cell_new),
                count_marks + 1,
                Rule.sides_next
                    .get(side_move)
            );
    }

    public
    String  render() {
        final
        List<String> texts_row =
            new ArrayList<>();
        for (
            int     index_row = 0;
            index_row < H;
            index_row += 1
        ) {
            final
            List<String> texts_cell =
                new ArrayList<>();
            for (
                int     index_column = 0;
                index_column < W;
                index_column += 1
            ) {
                texts_cell.add(
                    render_cell(
                        get_index_cell(
                            index_row,
                            index_column
                        )
                    )
                );
            }
            final
            String text_row =
                String.join("", texts_cell);
            texts_row
                .add(text_row);
        }
        return String.join("\n", texts_row);
    }

    private
    String  render_cell(
        int     index_cell
    ) {
        final
        Mark
            mark_cell =
                key.marks_cell
                    .get(index_cell);
        switch (mark_cell) {
            case NONE -> {
                return Integer.toString(index_cell + 1);
            }
            case CROSS -> {
                return "X";
            }
            case NAUGHT -> {
                return "O";
            }
            default -> {
                throw new IllegalArgumentException("Invalid value " + mark_cell + ".");
            }
        }
    }
}