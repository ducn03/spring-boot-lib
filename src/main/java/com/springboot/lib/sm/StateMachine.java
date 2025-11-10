package com.springboot.lib.sm;

import com.springboot.jpa.domain.SM;
import com.springboot.jpa.repository.SMRepository;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

public abstract class StateMachine<I extends SMInput, T extends SMData<T>> {

    private final SMRepository smRepository;

    protected StateMachine(SMRepository smRepository) {
        this.smRepository = smRepository;
    }

    protected abstract Template<T> init();

    public Template<T> getTemplate() {
        return init();
    }

    public abstract T loadData(I input, Template<T> template);

    public void saveData(T data, Template<T> template) {
        this.smRepository.save(data.getSm());
        saveDataInternal(data, template);
    }

    protected abstract void saveDataInternal(T data, Template<T> template);

    protected SM createNew(long masterId, Template<T> template) {
        State<T> state = template.getDefaultInitState();
        SM sm = new SM();
        sm.setMasterId(masterId);
        sm.setTemplateId(template.getId());
        sm.setStatus(state.getKey());
        sm.setActionStatus(state.getDefaultActionStatus());
        long epochSecond = Instant.now().getEpochSecond();
        sm.setUpdatedAt(new Timestamp(epochSecond * 1000));
        return smRepository.save(sm);
    }

    protected SM loadInternal(I input, Template<T> template) {
        Optional<SM> smOptional = smRepository.findByTemplateIdAndMasterIdOrderByIdDesc(template.getId(), input.getMasterId());
        if (smOptional.isEmpty()) {
            throw new AppException(ErrorCodes.SYSTEM.SM.BAD_REQUEST_INPUT_NOT_FOUND);
        }
        return smOptional.get();
    }

    // on auto State Machine
//    public void handle(ITrigger<T> trigger) {
//        load();
//        T data = trigger.getData();
//        String stateKey = data.key();
//        State<T> state = this.template.getState(stateKey);
//        if (state == null) {
//            AppThrower.pe(ErrorCode.INTERNAL_ERROR.SM.BAD_REQUEST_STATE_NOT_FOUND);
//        }
//        String actionKey = data.action();
//        Action<T> action = state.getAction(actionKey);
//        if (action == null) {
//            AppThrower.pe(ErrorCode.INTERNAL_ERROR.SM.BAD_REQUEST_STATE_NOT_FOUND);
//        }
//        action.doAction(data);
//    }


}
