600.421 Object-Oriented Software Engineering
Assignment 1a: Invasion Model
Alex Heinz (aheinz2@jhu.edu)

Fully-qualified package name: edu.jhu.cs.aheinz2.oose.invasion.MyInvasionModel

NOTE: This package contains two (nearly identical) model classes, one of which is a modification to the standard model that automatically ends the player's turn after a move if he or she has no additional possible moves. (For pirates, this is always the case; for bulgars, the turn ends if the first move was not a jump, or if no other jumps exist.) Pushing the button is still used to decline multiple jumps, or to forfeit a turn if no moves are possible.
I would ordinarily have only submitted this version, but the specification does not state whether it is necessary to require the player to push the "End Turn" button. If this is the case, (or if, for instance, your testing rig requires the button) please grade the "normal" model, and ignore the modified version. Otherwise, I hope the addition makes your testing a little easier. :)