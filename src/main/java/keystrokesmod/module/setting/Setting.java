package keystrokesmod.module.setting;

import com.google.gson.JsonObject;
import keystrokesmod.utility.interfaces.BooleanFunction;

public abstract class Setting {
    public String n;

    public Setting(String n) {
        this.n = n;
    }

    public String getName() {
        return this.n;
    }

    public abstract void loadProfile(JsonObject data);
}
