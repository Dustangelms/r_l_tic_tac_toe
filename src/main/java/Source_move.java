public
interface Source_move {

    Move
    get_move(
        final
        Board
            board
    );

    void accept_reward(
        final
        int     reward
    );
}