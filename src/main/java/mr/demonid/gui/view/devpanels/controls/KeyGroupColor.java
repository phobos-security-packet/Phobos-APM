package mr.demonid.gui.view.devpanels.controls;

import java.awt.*;

public class KeyGroupColor {

    private Color normal;
    private Color pressed;
    private Color rollover;

    public KeyGroupColor(Color normal, Color pressed, Color rollover)
    {
        this.normal = normal;
        this.pressed = pressed;
        this.rollover = rollover;
    }

    public Color normal() {
        return normal;
    }

    public void setNormal(Color normal) {
        this.normal = normal;
    }

    public Color pressed() {
        return pressed;
    }

    public void setPressed(Color pressed) {
        this.pressed = pressed;
    }

    public Color rollover() {
        return rollover;
    }

    public void setRollover(Color rollover) {
        this.rollover = rollover;
    }

    @Override
    public String toString() {
        return "KeyGroupColor{" +
                "normal=" + normal +
                ", pressed=" + pressed +
                ", rollover=" + rollover +
                '}';
    }
}
