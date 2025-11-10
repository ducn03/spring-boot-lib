package com.springboot.lib.sm;


import com.springboot.jpa.domain.SM;
import com.springboot.jpa.repository.SMRepository;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;

public abstract class ManualStateMachine<I extends SMInput, T extends SMData<T>> extends StateMachine<I, T> {

    protected ManualStateMachine(SMRepository smRepository) {
        super(smRepository);
    }

    protected void handle(I input, String act, ActionCallback<T> callback) {
        Template<T> template = getTemplate();
        T data = loadData(input, template);
        SM sm = data.getSm();
        State<T> state = template.getState(sm.getStatus());
        if (state == null) {
            throw new AppException(ErrorCodes.SYSTEM.SM.BAD_REQUEST_STATE_NOT_FOUND);
        }
        Action<T> action = state.getAction(act);
        if (action == null) {
            throw new AppException(ErrorCodes.SYSTEM.SM.BAD_REQUEST_ACTION_NOT_FOUND);
        }
        sm.setStatus(action.next().getKey());
        try {
            callback.doBack(data);
        } finally {
            saveData(data, template);
        }
    }
}
