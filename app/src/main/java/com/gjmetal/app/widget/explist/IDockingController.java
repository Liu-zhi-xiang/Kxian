package com.gjmetal.app.widget.explist;

/**
 * Created by hgh on 2018/3/30.
 */

public interface IDockingController {

    int DOCKING_HEADER_HIDDEN = 1;

    int DOCKING_HEADER_DOCKING = 2;

    int DOCKING_HEADER_DOCKED = 3;



    int getDockingState(int firstVisibleGroup, int firstVisibleChild);

}
