import java.util.Scanner;

public
class Source_move_human
    implements Source_move
{
    private static final
    Source_move_human INSTANCE =
        new Source_move_human();

    public static
    Source_move_human get_instance() {
        return INSTANCE;
    }

    @Override
    public
    Move
    get_move(
        final
        Board
            board
    ) {
        final
        Scanner
            scanner =
                new Scanner(System.in);
        final
        String  text_board =
            board.render();

        while (true) {
            System.out.println(text_board);
            System.out.println("Enter your move:");

            final String line_move =
                scanner.nextLine();
            final int index_cell_move;
            try {
                index_cell_move =
                    Integer
                        .parseInt(line_move);
            } catch (NumberFormatException unused) {
                continue;
            }

            final
            Move
                move =
                    new Move
                        (index_cell_move - 1);
            if (
                board.is_move_valid
                    (move)
            ) {
                return move;
            }
        }
    }

    @Override
    public
    void accept_reward(
        final
        int     reward
    ) {
        switch (reward) {
            case -1 -> {
                System.out.println("You lost.");
            }
            case 0 -> {
                System.out.println("You drew.");
            }
            case 1 -> {
                System.out.println("You won.");
            }
            default -> {
                throw new IllegalArgumentException("Invalid value " + reward + ".");
            }
        }
    }
}