Le Phuong Trang Nguyen
len054
11280441

The structure of my assignment handin:

APP:
-   ShipDemo

MODEL:
-   ShipModel
-   InteractionModel

CONTROLLER:
-   ShipController: I added 3 more states: DRAW_OR_UNSELECT, DRAW_RUBBERBAND, DRAW_RUBBERBAND_CONTROL
        When a user presses the mouse on the background, the state is set to DRAW_OR_UNSELECT. Then if user:
            drags:
                with control down: switches to DRAW_RUBBERBAND_CONTROL
                without control down: switches to DRAW_RUBBERBAND
            releases:
                unselect everything and is set back to READY

VIEW:
-   ShipView

INTERFACE:
-   Groupable (new): the interface of Ship and ShipGroup classes
-   ShipModelSubscriber

OTHERS:
-   Ship: For each of the variables which are coordinates or a list of coordinates,
        I created one more to keep track of the current rotated coordinates.
        And when the user switches to another task from rotation, I finalize the original to the value of 
        the current rotated coordinates.
        I have to do this so in a rotating session, ships are rotated relatively to their original coordinates,
        not their newly rotated coordinates.

        After each rotating session, I also created a new snapshot so that the pixelReader is updated with the
        new rotation of the ship and correctly determines if a point is in the ship (the contains() function)

-   ShipGroup (new): a group of groupable objects.
                     Comply to the Groupable interface.
                     Most functions use recursion to delegate the tasks to the children.

-   ShipClipboard (new): store a list of selected item and has 3 methods: cut, copy and paste which only
                        changes the clipboard's internal list and has nothing to do with model/iModel's list
                        ShipClipboard is managed by iModel. I followed the professor's advice that we do not have to make the
                        copies's location different from the original's location so the copies will be laid
                        exactly on top the original so they might be obvious.

-   XRectangle and XShape: reused from previous assignment for drawing rubberbands.
                             I added a attribute for XShape which is isShown. This acts like
                             a flip switch for whether to draw the rubberband or not.
                             Everytime we enter or leave the DRAW_RUBBERBAND/DRAW_RUBBERBAND_CONTROL state,
                             this switch is flipped.
