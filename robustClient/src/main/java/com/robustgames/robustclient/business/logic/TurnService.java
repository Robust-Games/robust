package com.robustgames.robustclient.business.logic;


import static com.robustgames.robustclient.business.logic.TurnService.GamePhase.PLAYER_ONE_PLANNING;
import static com.robustgames.robustclient.business.logic.TurnService.GamePhase.PLAYER_TWO_PLANNING;

public class TurnService {
    enum GamePhase {
        PLAYER_ONE_PLANNING,
        PLAYER_TWO_PLANNING,
        TURN_EXECUTION
    }
    private GamePhase currentPhase = PLAYER_ONE_PLANNING;

    private TurnPlanService playerOnePlan = new TurnPlanService();
    private TurnPlanService playerTwoPlan = new TurnPlanService();

    public void submitPlan(TurnPlanService plan) {
        if (currentPhase == PLAYER_ONE_PLANNING) {
            playerOnePlan = plan;
            currentPhase = PLAYER_TWO_PLANNING;
        } else if (currentPhase == PLAYER_TWO_PLANNING) {
            playerTwoPlan = plan;
            currentPhase = GamePhase.TURN_EXECUTION;
            executePlans();
        }
    }

    private void executePlans() {
        // Process both plans — animation, movement, shooting
        playerOnePlan.execute();
        playerTwoPlan.execute();

        // Reset
        playerOnePlan.clear();
        playerTwoPlan.clear();
        currentPhase = PLAYER_ONE_PLANNING;
    }

    public GamePhase getPhase() {
        return currentPhase;
    }
}



