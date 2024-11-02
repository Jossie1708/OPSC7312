package com.frogstore.droneapp.control

import com.frogstore.droneapp.control.drawer.CircleControlDrawer
import com.frogstore.droneapp.control.drawer.ControlDrawer
import com.frogstore.droneapp.theme.ColorsScheme
import com.frogstore.droneapp.views.JoystickView

/**
 * [Control] that uses by default by the [CircleControlDrawer].
 */
open class CircleControl(
    colors: ColorsScheme,
    invalidRadius: Float,
    directionType: JoystickView.DirectionType,
    radiusRatio: Float
) : Control(invalidRadius, directionType) {

    override var drawer: ControlDrawer = CircleControlDrawer(colors, radiusRatio)

}