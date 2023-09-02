import java.util.Optional;
import java.util.Map;
import java.util.EnumMap;
import java.util.stream.Collectors;

import java.util.Scanner;

public class Runner {

    private static final
    double  FACTOR_U_C_B_ADDENDUM = 0.5;

    public static
    void    main(String[] args) {
        final
        Player_r_l
            player_r_l =
                new Player_r_l(FACTOR_U_C_B_ADDENDUM);
        final
        Context
            context =
                new Context(player_r_l);
        while (true) {
            final Command
                command =
                    input_command();
            if (
                command.run(context)
                    ==
                Command.Result.QUIT
            ) {
                return;
            }
        }
    }

    public
    enum Type_player {
        REINFORCEMENT_LEARNER,
        HUMAN
    }

    private static final
    Map<Side, Type_player>
        types_player_train;

    private static final
    Map<Side, Type_player>
        types_player_human_cross;

    private static final
    Map<Side, Type_player>
        types_player_human_naught;

    private static final
    Map<Side, Integer>
        rewards_draw;

    private static final
    Map<
        Side,
        Map<Side, Integer>
    >
        rewards_won;

    private static final
    int     REWARD_WIN = 1;

    private static final
    int     REWARD_DRAW = 0;

    private static final
    int     REWARD_LOSS = -1;

    private
    record Context(
        Player_r_l
            player_r_l
    ) {

        public
        Source_move
        get_source_move(
            Type_player
                type_player
        ) {
            switch (type_player) {
                case REINFORCEMENT_LEARNER -> {
                    return player_r_l.get_source_move();
                }
                case HUMAN -> {
                    return Source_move_human.get_instance();
                }
                default -> {
                    throw new IllegalArgumentException("Invalid value \"" + type_player + "\".");
                }
            }
        }
    }

    private
    interface Command {

        enum Result {

            CONTINUE,
            QUIT
        }

        Result  run(
            final
            Context
                context
        );
    }

    private
    record Command_play(
        int     count_episodes,
        Map<Side, Type_player>
            types_player
    ) implements Command {

        @Override
        public
        Result  run(
            final
            Context
                context
        ) {
            play(
                context,
                count_episodes,
                types_player
            );
            return Result.CONTINUE;
        }
    }

    private static
    class Command_diagnose
        implements Command {

        public static final
        Command_diagnose INSTANCE =
            new Command_diagnose();

        @Override
        public
        Result  run(
            final
            Context
                context
        ) {
            diagnose(context);
            return Result.CONTINUE;
        }
    }

    private static
    class Command_quit
        implements Command {

        public static final
        Command_quit INSTANCE =
            new Command_quit();

        @Override
        public
        Result  run(
            final
            Context
                context
        ) {
            return Result.QUIT;
        }
    }

    static {
        types_player_train = new EnumMap<>(Side.class);
        types_player_train.put(
            Side.CROSS,
            Type_player.REINFORCEMENT_LEARNER
        );
        types_player_train.put(
            Side.NAUGHT,
            Type_player.REINFORCEMENT_LEARNER
        );

        types_player_human_cross = new EnumMap<>(Side.class);
        types_player_human_cross.put(
            Side.CROSS,
            Type_player.HUMAN
        );
        types_player_human_cross.put(
            Side.NAUGHT,
            Type_player.REINFORCEMENT_LEARNER
        );

        types_player_human_naught = new EnumMap<>(Side.class);
        types_player_human_naught.put(
            Side.CROSS,
            Type_player.REINFORCEMENT_LEARNER
        );
        types_player_human_naught.put(
            Side.NAUGHT,
            Type_player.HUMAN
        );

        rewards_draw = new EnumMap<>(Side.class);
        rewards_draw.put(
            Side.CROSS,
            REWARD_DRAW
        );
        rewards_draw.put(
            Side.NAUGHT,
            REWARD_DRAW
        );

        Map<Side, Integer> rewards_won_cross =
            new EnumMap<>(Side.class);
        rewards_won_cross.put(
            Side.CROSS,
            REWARD_WIN
        );
        rewards_won_cross.put(
            Side.NAUGHT,
            REWARD_LOSS
        );

        Map<Side, Integer> rewards_won_naught =
            new EnumMap<>(Side.class);
        rewards_won_naught.put(
            Side.CROSS,
            REWARD_LOSS
        );
        rewards_won_naught.put(
            Side.NAUGHT,
            REWARD_WIN
        );

        rewards_won = new EnumMap<>(Side.class);
        rewards_won.put(
            Side.CROSS,
            rewards_won_cross
        );
        rewards_won.put(
            Side.NAUGHT,
            rewards_won_naught
        );
    }

    private static
    Command
    input_command() {
        final
        Scanner
            scanner =
                new Scanner(System.in);
        while (true) {
            System.out.println("Input command: t<#> to train model, px to play as crosses vs model, po to play as naughts vs model, d for diagnostics, q to quit:");
            final
            String  text_command =
                scanner.nextLine();
            final
            Command
                command =
                    parse_command
                        (text_command);
            if (command != null) {
                return command;
            }
        }
    }

    private static
    Command
    parse_command(
        final
        String  text_command
    ) {
        if (text_command.length() < 1) {
            return null;
        }
        switch (text_command.charAt(0)) {
            case 't' -> {
                return
                    parse_command_t
                        (text_command.substring(1));
            }
            case 'p' -> {
                return
                    parse_command_p
                        (text_command.substring(1));
            }
            case 'd' -> {
                return Command_diagnose.INSTANCE;
            }
            case 'q' -> {
                return Command_quit.INSTANCE;
            }
            default -> {
                return null;
            }
        }
    }

    private static
    Command
    parse_command_t(
        final
        String  text_command
    ) {
        final
        int     count_episodes;
        try {
            count_episodes =
                Integer
                    .parseInt(text_command);
        } catch (NumberFormatException unused) {
            return null;
        }
        if (count_episodes <= 0) {
            return null;
        }
        return
            new Command_play(
                count_episodes,
                types_player_train
            );
    }


    private static
    Command
    parse_command_p(
        final
        String  text_command
    ) {
        if (text_command.length() != 1) {
            return null;
        }
        if (text_command.charAt(0) == 'x') {
            return
                new Command_play(
                    1,
                    types_player_human_cross
                );
        }
        if (text_command.charAt(0) == 'o') {
            return
                new Command_play(
                    1,
                    types_player_human_naught
                );
        }
        return null;
    }

    private static
    void play(
        final
        Context
            context,
        final
        int     count_episodes,
        final
        Map<Side, Type_player>
            types_player
    ) {
        final
        Map<Side, Source_move>
            sources_move =
                types_player.entrySet().stream()
                    .collect(
                        Collectors.toMap(
                            Map.Entry::getKey,
                            entry ->
                                context.get_source_move(entry.getValue())
                        )
                    );
        for (
            int     index_episode = 0;
            index_episode
                <
            count_episodes;
            index_episode += 1
        ) {
            Map<Side, Integer> rewards = play_episode(sources_move);
            rewards.forEach(
                (side, reward) ->
                    sources_move.get(side).accept_reward
                        (reward)
            );
        }
    }

    private static
    Map<Side, Integer> play_episode(
        final
        Map<Side, Source_move>
            sources_move
    ) {
        Board
            board =
                Board.get_instance_initial();
        while (true) {
            final
            Optional<Side>
                maybe_side_won =
                    board.calc_side_won();
            if (maybe_side_won.isPresent()) {
                return
                    rewards_won
                        .get(maybe_side_won.get());
            }
            if (board.is_full()) {
                return rewards_draw;
            }
            final
            Move
                move =
                    sources_move
                        .get(board.get_side_move())
                        .get_move(board);
            board =
                board.make_move_unsafe
                    (move);
        }
    }

    private static
    void    diagnose(
        Context
            context
    ) {
        context.player_r_l.diagnose();
    }
}