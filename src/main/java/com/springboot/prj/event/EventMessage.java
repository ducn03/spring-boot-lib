package com.springboot.prj.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventMessage {
    private String type;
    private String message;
}
