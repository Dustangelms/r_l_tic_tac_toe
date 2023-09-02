import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import java.util.function.Function;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;

@RequiredArgsConstructor
@FieldDefaults(
    level =
        AccessLevel.PRIVATE,
    makeFinal = true
)
public class Player_r_l {

    @RequiredArgsConstructor
    @FieldDefaults(
        level =
            AccessLevel.PRIVATE
    )
    private static
    class Experience_move {

        int     count_visits = 0;

        int     reward_sum = 0;

        double  reward_average = 0;

        double  u_c_b_addendum = 0;

        double  u_c_b = Double.POSITIVE_INFINITY;

        public
        double  get_u_c_b() {
            return u_c_b;
        }

        public
        void    record_visit(
            int     reward
        ) {
            count_visits += 1;
            reward_sum +=
                reward;
            reward_average =
                ((double) reward_sum / count_visits);
        }

        public
        void    set_u_c_b_addendum(
            double  u_c_b_addendum
        ) {
            this.u_c_b_addendum =
                u_c_b_addendum;
        }

        public
        void    update_u_c_b() {
            u_c_b =
                reward_average
                    +
                u_c_b_addendum;
        }
    }

    @RequiredArgsConstructor
    @FieldDefaults(
        level =
            AccessLevel.PRIVATE
    )
    private static
    class Experience_board {

        int     count_visits = 0;

        Map<
            Move,
            Experience_move
        >
            experiences_move;

        public
        Experience_board(
            Board
                board
        ) {
            experiences_move =
                board.get_moves_valid()
                    .collect(
                        Collectors.toMap(
                            Function.identity(),
                            move ->
                                new Experience_move()
                        )
                    );
        }

        public
        Move
        get_move_best() {
            return
                get_experiences()
                    .max(
                        Comparator
                            .comparingDouble(
                                entryExperience ->
                                    entryExperience.getValue().get_u_c_b()
                            )
                    )
                    .get()
                    .getKey();
        }

        public
        void    record_visit(
            Move
                move,
            int     reward,
            Calculator_u_c_b_addendum
                calculator_u_c_b_addendum
        ) {
            count_visits += 1;
            get_experience(move)
                .record_visit(reward);
            get_experiences()
                .map(Map.Entry::getValue)
                .forEach(
                    experience ->
                        update_u_c_b(
                            experience,
                            calculator_u_c_b_addendum
                        )
                );
        }

        private
        void    update_u_c_b(
            Experience_move
                experience,
            Calculator_u_c_b_addendum
                calculator_u_c_b_addendum
        ) {
            final
            double u_c_b_addendum =
                calculator_u_c_b_addendum
                    .calculate(this, experience);
            experience.set_u_c_b_addendum
                (u_c_b_addendum);
            experience.update_u_c_b();
        }

        private
        Stream<
            Map.Entry<
                Move,
                Experience_move
            >
        >
        get_experiences() {
            return experiences_move.entrySet().stream();
        }

        private
        Experience_move
        get_experience(
            Move
                move
        ) {
            return
                experiences_move
                    .get(move);
        }
    }

    private
    interface Calculator_u_c_b_addendum {
        double  calculate(
            Experience_board
                experience_board,
            Experience_move
                experience_move
        );
    }

    @RequiredArgsConstructor
    @FieldDefaults(
        level =
            AccessLevel.PRIVATE,
        makeFinal = true
    )
    public
    class Source_move_r_l
        implements Source_move {

        private
        record Visit(
            Board
                board,
            Move
                move
        )
        {}

        List<Visit>
            visits = new ArrayList<>();

        @Override
        public
        Move
        get_move(
            final
            Board
                board
        ) {
            final
            Move
                move =
                    Player_r_l.this.get_move(board);
            visits.add(
                new Visit(
                    board,
                    move
                )
            );
            return move;
        }

        @Override
        public
        void accept_reward(
            final
            int     reward
        ) {
            visits.forEach(
                visit ->
                    Player_r_l.this.record_visit(
                        visit.board,
                        visit.move,
                        reward
                    )
            );
            visits.clear();
        }
    }

    double  factor_u_c_b_addendum;

    Map<
        Board.Key,
        Experience_board
    >
        experiences_board =
            new HashMap<>();

    public
    Source_move_r_l
    get_source_move() {
        return new Source_move_r_l();
    }

    private
    Move
    get_move(
        final
        Board
            board
    ) {
        return get_experience(board).get_move_best();
    }

    private
    void    record_visit(
        Board
            board,
        Move
            move,
        int     reward
    ) {
        get_experience(board)
            .record_visit(
                move,
                reward,
                this::calculate_u_c_b_addendum
            );
    }

    private
    Experience_board
    get_experience(
        Board
            board
    ) {
        final
        Board.Key
            key_board =
                board.get_key();
        Experience_board
            experience_board =
                experiences_board
                    .get(key_board);
        if (experience_board == null) {
            experience_board =
                new Experience_board
                    (board);
            experiences_board.put(
                key_board,
                experience_board
            );
        }
        return experience_board;
    }

    private
    double  calculate_u_c_b_addendum(
        Experience_board
            experience_board,
        Experience_move
            experience_move
    ) {
        return
            factor_u_c_b_addendum
                *
            Math.sqrt(
                Math.log(experience_board.count_visits)
                    /
                experience_move.count_visits
            );
    }

    public
    void    diagnose() {
        final
        Board
            board_1 =
                Board.get_instance_initial();
        print_experience_board(board_1);
        final
        Board
            board_2 =
                board_1.make_move_unsafe
                    (new Move(4));
        print_experience_board(board_2);
    }

    private
    void    print_experience_board(
        Board
            board
    ) {
        System.out.println(
            "[" +
                get_experience(board)
                    .get_experiences()
                    .sorted(
                        Comparator
                            .comparingInt(
                                entry_experience_move ->
                                    entry_experience_move.getKey().index_cell()
                            )
                    )
                    .map
                        (Map.Entry::getValue)
                    .map(
                        experience_move ->
                            experience_move.reward_average
                    )
                    .map(
                        reward_average ->
                            String.format("%.2f", reward_average)
                    )
                    .collect
                        (Collectors.joining("; "))
            + "]"
        );
    }
}