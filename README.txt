600.421 Object-Oriented Software Engineering
Assignment 1b: Invasion UI
Alex Heinz (aheinz2@jhu.edu)

Principle Class FQPN: edu.jhu.cs.aheinz2.oose.invasion.UI.MyInvasionMain
(Accepts a model class name as an argument; defaults to my model class.)

Added features:
-	"Space Invaders"-style graphics
-	Board maintains aspect-ratio with resize
-	"New Game" button at end of game

Known issues: There is an outstanding bug in the Swing toolkit which causes ComponentResized events not to be fired when the enclosing window's "maximize" button is clicked, even if the component in question must change size as a result of the event. The accepted workaround, (http://forums.sun.com/thread.jspa?threadID=764451) for reasons unknown to me, does not appear to work for this application. As a result, the board component will not be resized properly when the user clicks the maximize button. Adjusting the window size manually will correct the problem.
