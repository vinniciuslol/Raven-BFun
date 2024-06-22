package keystrokesmod.module.setting;

import keystrokesmod.utility.interfaces.BooleanFunction;
import com.google.gson.JsonObject;

public abstract class Setting {
    public String n;
	private BooleanFunction<? super Setting> predicate;

    public Setting(String n) {
        this.n = n;
    }

    public String getName() {
        return this.n;
    }

    public abstract void loadProfile(JsonObject data);
	
	public void showOnly(BooleanFunction<? super Setting> predicate) {
		this.predicate = predicate;
	}
	
	public boolean isHide(BooleanFunction<? super Setting> predicate) {
		return this.predicate != null && !this.predicate.getResult();
	}
}
