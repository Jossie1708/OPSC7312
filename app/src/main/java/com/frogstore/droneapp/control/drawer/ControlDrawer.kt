package com.frogstore.droneapp.control.drawer

import android.graphics.Canvas
import com.frogstore.droneapp.control.Control

/**
 * Interface for draw the control representation.
 */
fun interface ControlDrawer {
    /**
     * Draw the control representation.
     *
     * @param canvas The view canvas.
     * @param control The [Control] from where the drawer is used.
     */
    fun draw(canvas: Canvas, control: Control)
}