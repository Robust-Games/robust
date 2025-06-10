package com.robustgames.robustclient.business.logic;

import com.almasb.fxgl.entity.action.Action;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class TurnPlanService {
    private List<PlannedAction> actions = new ArrayList<>();

    public void addAction(PlannedAction action) {
        actions.add(action);
    }

    public void execute() {
        actions.forEach(PlannedAction::execute);
    }

    public void clear() {
        actions.clear();
    }
    public class PlannedAction{
        private Action action;
        private Point2D targetLocation;

        public void execute() {
        }
    }
}
