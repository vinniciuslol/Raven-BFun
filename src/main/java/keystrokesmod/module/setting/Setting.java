package keystrokesmod.module.setting;

import com.google.gson.JsonObject;
import keystrokesmod.utility.interfaces.BooleanFunction;

public abstract class Setting {
    public String n;
    private BooleanFunction<? extends Setting> predicate;

    public Setting(String n) {
        this.n = n;
    }

    public String getName() {
        return this.n;
    }

    public abstract void loadProfile(JsonObject data);

    public void showOnly(BooleanFunction<? extends Setting> predicate) {
        this.predicate = predicate;
    }

    public boolean isHide() {
        return predicate != null && !predicate.getResult();
    }
}
