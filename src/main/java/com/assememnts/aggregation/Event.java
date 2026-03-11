package com.assememnts.aggregation;

import java.util.Objects;

public record Event(String id, long timestamp, double value) {
    @Override
    public int hashCode() {
        return id.hashCode() + Objects.hashCode(timestamp);
    }

    @Override
    public boolean equals(final Object event) {
        if (event instanceof Event parsed) {
            return (
                    parsed.hashCode() == this.hashCode() &&
                            (parsed.id.equals(this.id) && parsed.timestamp == this.timestamp)
            );
        } else {
            return Boolean.FALSE;
        }
    }
}
