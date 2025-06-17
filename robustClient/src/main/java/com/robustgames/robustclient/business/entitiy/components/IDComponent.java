package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;

public class IDComponent extends Component {
    private long id;
    public IDComponent(long id) { this.id = id; }
    public long getId() { return id; }
}