600.421 Object-Oriented Software Engineering
Assignment 1b: Invasion UI
Alex Heinz (aheinz2@jhu.edu)


Known issues: There is an outstanding bug in the Swing toolkit which causes ComponentResized events to not be fired when the enclosing window's "maximize" button is clicked, even if the component in question must change size as a result of the event. The accepted workaround, (here: http://forums.sun.com/thread.jspa?threadID=764451) for reasons unknown to me, does not appear to work for this application. As a result, the board component will not be resized properly when the user clicks the maximize button.
