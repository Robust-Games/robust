/**
 * @author Ersin Yesiltas
 */
package com.robustgames.robustclient.business.entitiy.components;

import com.almasb.fxgl.entity.component.Component;

/**
 * Component that adds a unique long ID to an entity.
 * <p>
 * This can be used to uniquely identify entities across the network
 * or for internal game logic that requires persistent identification.
 */
public class IDComponent extends Component {

    /**
     * The unique identifier assigned to the entity.
     */
    private final long id;

    /**
     * Constructs an IDComponent with the given unique ID.
     *
     * @param id the unique long identifier for this entity
     */
    public IDComponent(long id) {
        this.id = id;
    }

    /**
     * Returns the unique ID associated with this entity.
     *
     * @return the entity's unique long ID
     */
    public long getId() {
        return id;
    }
}
