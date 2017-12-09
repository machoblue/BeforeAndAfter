package org.macho.beforeandafter.graphe2;

/**
 * Created by yuukimatsushima on 2017/12/04.
 */

public class Poin2 {
        private long dateTime;
        private float value;
        public Poin2(long dateTime, float value) {
            this.dateTime = dateTime;
            this.value = value;
        }
        public long getDateTime() {
            return dateTime;
        }
        public void setDateTime(long dateTime) {
            this.dateTime = dateTime;
        }
        public float getValue() {
            return value;
        }
        public void setValue(float value) {
            this.value = value;
        }
}
