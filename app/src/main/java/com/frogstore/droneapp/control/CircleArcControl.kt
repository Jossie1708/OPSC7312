package com.frogstore.droneapp.control

import com.frogstore.droneapp.control.drawer.CircleArcControlDrawer
import com.frogstore.droneapp.control.drawer.ControlDrawer
import com.frogstore.droneapp.theme.ColorsScheme
import com.frogstore.droneapp.views.JoystickView

/**
 * [Control] that uses by default by the [CircleArcControlDrawer].
 */
open class CircleArcControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    strokeWidth: Float,
    sweepAngle: Float,
    radiusProportion: Float
) : Control(invalidRadius, directionType) {

    override var drawer: ControlDrawer = CircleArcControlDrawer(colors, strokeWidth, sweepAngle, radiusProportion)
}