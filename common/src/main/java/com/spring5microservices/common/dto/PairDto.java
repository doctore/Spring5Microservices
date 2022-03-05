package com.spring5microservices.common.dto;

import lombok.Getter;

import java.util.Objects;

@Getter
public class PairDto<T, E> {

    private final T first;
    private final E second;

    private PairDto(T first, E second) {
        this.first = first;
        this.second = second;
    }

    public static <T, E> PairDto<T, E> of(T first, E second) {
        return new PairDto<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PairDto)) {
            return false;
        } else {
            PairDto p = (PairDto)o;
            return equals(this.first, p.first) && equals(this.second, p.second);
        }
    }

    @Override
    public int hashCode() {
        return 31 *
                (this.first != null ? this.first.hashCode() : 0) +
                (this.second != null ? this.second.hashCode() : 0);
    }

    @Override
    public String toString() {
        return "PairDto{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    private boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

}
