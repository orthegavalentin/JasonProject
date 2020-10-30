// mars robot 1

/* Initial beliefs */

at(P) :- pos(P,X,Y) & pos(player,X,Y).

/* Initial goal */

!check(slots).


/* Plans */

+!check(slots) : not enemyhere(enemy)
   <- next(slot);
      !check(slots).
+!check(slots).


+enemyhere(enemy):true <- !kill(enemy).


+!kill(enemy)<- kill(enemy);-enemyhere(enemy);!check(slots).




