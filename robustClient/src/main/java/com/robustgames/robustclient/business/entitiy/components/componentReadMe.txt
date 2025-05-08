FXGL components are just tiny classes you “plug into” entities. In a turn‑based game you’ll often
split movement into state (where am I?) and actions (can I move here?)

Any game object that you can think of (player, coin, power-up, wall, dirt, particle, weapon, etc.)
is an entity. By itself an entity is nothing but a generic object. Components allow us to shape that entity
 to be anything we like.

 https://github.com/AlmasB/FXGL/wiki/Entity-Component-(FXGL-11)