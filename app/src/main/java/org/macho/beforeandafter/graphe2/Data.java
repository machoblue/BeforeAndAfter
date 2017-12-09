package org.macho.beforeandafter.graphe2;

import java.util.List;

/**
 * Created by yuukimatsushima on 2017/12/04.
 */

public class Data {
        private List<Poin2> poin2s;
        private boolean isLeftYAxis;
        private int color;
        private boolean isDottedLine;

        public List<Poin2> getPoin2s() {
            return poin2s;
        }

        public void setPoin2s(List<Poin2> poin2s) {
            this.poin2s = poin2s;
        }

        public boolean isLeftYAxis() {
            return isLeftYAxis;
        }
        public void setLeftYAxis(boolean leftYAxis) {
            isLeftYAxis = leftYAxis;
        }
        public int getColor() {
            return color;
        }
        public void setColor(int color) {
            this.color = color;
        }
        public boolean isDottedLine() {
            return isDottedLine;
        }
        public void setDottedLine(boolean dottedLine) {
            isDottedLine = dottedLine;
        }
}
