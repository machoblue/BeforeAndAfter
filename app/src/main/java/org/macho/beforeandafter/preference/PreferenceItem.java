package org.macho.beforeandafter.preference;

/**
 * Created by yuukimatsushima on 2017/08/13.
 */

public class PreferenceItem {
    private int title;
    private int description;
    private PreferenceAction action;
    public PreferenceItem(int title, int description, PreferenceAction action) {
        this.title = title;
        this.description = description;
        this.action = action;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public PreferenceAction getAction() {
        return action;
    }

    public void setAction(PreferenceAction action) {
        this.action = action;
    }

    interface PreferenceAction{
        public void doPreferenceAction();
    }
}
