/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.network.chat;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ClickEvent {
    private final Action action;
    private final String value;

    public ClickEvent(Action action, String string) {
        this.action = action;
        this.value = string;
    }

    public Action getAction() {
        return this.action;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ClickEvent clickEvent = (ClickEvent)object;
        if (this.action != clickEvent.action) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(clickEvent.value) : clickEvent.value != null);
    }

    public String toString() {
        return "ClickEvent{action=" + (Object)((Object)this.action) + ", value='" + this.value + '\'' + '}';
    }

    public int hashCode() {
        int n = this.action.hashCode();
        n = 31 * n + (this.value != null ? this.value.hashCode() : 0);
        return n;
    }

    public static enum Action {
        OPEN_URL("open_url", true),
        OPEN_FILE("open_file", false),
        RUN_COMMAND("run_command", true),
        SUGGEST_COMMAND("suggest_command", true),
        CHANGE_PAGE("change_page", true),
        COPY_TO_CLIPBOARD("copy_to_clipboard", true);
        
        private static final Map<String, Action> LOOKUP;
        private final boolean allowFromServer;
        private final String name;

        private Action(String string2, boolean bl) {
            this.name = string2;
            this.allowFromServer = bl;
        }

        public boolean isAllowedFromServer() {
            return this.allowFromServer;
        }

        public String getName() {
            return this.name;
        }

        public static Action getByName(String string) {
            return LOOKUP.get(string);
        }

        static {
            LOOKUP = Arrays.stream(Action.values()).collect(Collectors.toMap(Action::getName, action -> action));
        }
    }

}

