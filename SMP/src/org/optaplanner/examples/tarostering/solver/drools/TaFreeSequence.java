/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.tarostering.solver.drools;

import java.io.Serializable;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.optaplanner.examples.tarostering.domain.Ta;

public class TaFreeSequence implements Comparable<TaFreeSequence>, Serializable {

    private Ta ta;
    private int firstDayIndex;
    private int lastDayIndex;

    public TaFreeSequence(Ta ta, int firstDayIndex, int lastDayIndex) {
        this.ta = ta;
        this.firstDayIndex = firstDayIndex;
        this.lastDayIndex = lastDayIndex;
    }

    public Ta getTa() {
        return ta;
    }

    public void setTa(Ta ta) {
        this.ta = ta;
    }

    public int getFirstDayIndex() {
        return firstDayIndex;
    }

    public void setFirstDayIndex(int firstDayIndex) {
        this.firstDayIndex = firstDayIndex;
    }

    public int getLastDayIndex() {
        return lastDayIndex;
    }

    public void setLastDayIndex(int lastDayIndex) {
        this.lastDayIndex = lastDayIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof TaFreeSequence) {
            TaFreeSequence other = (TaFreeSequence) o;
            return new EqualsBuilder()
                    .append(ta, other.ta)
                    .append(firstDayIndex, other.firstDayIndex)
                    .append(lastDayIndex, other.lastDayIndex)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(ta)
                .append(firstDayIndex)
                .append(lastDayIndex)
                .toHashCode();
    }

    @Override
    public int compareTo(TaFreeSequence other) {
        return new CompareToBuilder()
                .append(ta, other.ta)
                .append(firstDayIndex, other.firstDayIndex)
                .append(lastDayIndex, other.lastDayIndex)
                .toComparison();
    }

    @Override
    public String toString() {
        return ta + " is free between " + firstDayIndex + " - " + lastDayIndex;
    }

    public int getDayLength() {
        return lastDayIndex - firstDayIndex + 1;
    }

}
