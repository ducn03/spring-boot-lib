package com.springboot.lib.sm;

import com.springboot.jpa.domain.SM;
import lombok.Data;

@Data
public abstract class SMData<T extends SMData<T>> {

    private SM sm;
    public abstract Template<T> getTemplate();

}
