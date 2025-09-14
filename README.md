****Shadow Dungeon Game - Project 1****

**Assumptions**:

1. The room will only be changed if all doors are unlocked for a given room and the
 player is intersecting with any part of any unlocked door's bounding boxes. Furthermore,
 we define the process of changing rooms incomplete until the player leaves the bounding 
 box of the new room's primary door. This is to prevent against potentially moving from
 the end room to the prep room, without any movement in the intermediate battle rooms.

2. As defined in the specification, 'Also, clearing Battle Room B grants access to all
 rooms, including the End Room, enabling free navigation to open Treasure Boxes or switch
 characters in the Prep Room., this implies that treasure boxes will only be visible after
 defeating the enemies in battle room B. However, in the video provided, treasure boxes
 were visible before clearing battle room B. I have opted to follow the specification and
 wanted to clarify this inconsistency. 

3. 'When the player health drops below zero, the player loses the game.' is the wording 
 as defined in the specification. Reading this literally, the game should end when the health
 drops below zero, rather than at zero. Hence, the player loses the game with health -0.2, 
 rather than the nonsensical -0.0 as provided by the video. 

4. Movement is restricted so that no part of the player can touch any of the obstacles 
 (locked) doors. Similarly, any part of the player intersecting with the unlocked door, 
 provided that rooms aren't already being changed, will result in a room change process.

5. When transferring from battle Room A to the prep room, the player sprite shall be placed 
 at the coordinates of the secondary room in the prep room, since prep room has no primary
 door.