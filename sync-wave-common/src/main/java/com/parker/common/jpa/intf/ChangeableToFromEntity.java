package com.parker.common.jpa.intf;

public interface ChangeableToFromEntity<E> {
    E to();

    void from(E entity);
}
