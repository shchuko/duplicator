package swingUtils

import java.awt.Toolkit
import javax.swing.JFrame

fun JFrame.centring() {
    val dim = Toolkit.getDefaultToolkit().screenSize
    setLocation(
        dim.width / 2 - width / 2,
        dim.height / 2 - height / 2
    )
}