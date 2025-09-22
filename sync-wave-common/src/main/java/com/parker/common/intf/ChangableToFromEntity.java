package com.parker.common.intf;

public interface ChangableToFromEntity<E>{
    public E to();
    public void from (E entity);
}

