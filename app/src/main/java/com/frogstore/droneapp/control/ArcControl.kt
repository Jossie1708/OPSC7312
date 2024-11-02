package com.frogstore.droneapp.control

import com.frogstore.droneapp.control.drawer.ArcControlDrawer
import com.frogstore.droneapp.control.drawer.ControlDrawer
import com.frogstore.droneapp.theme.ColorsScheme
import com.frogstore.droneapp.views.JoystickView

/**
 * [Control] that uses by default by the [ArcControlDrawer].
 */
open class ArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
) : Control(invalidRadius, directionType) {
    override var drawer: ControlDrawer = ArcControlDrawer(colors, strokeWidth, sweepAngle)
}