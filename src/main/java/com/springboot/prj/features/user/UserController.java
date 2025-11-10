package com.springboot.prj.features.user;

import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.HttpHelper;
import com.springboot.lib.service.controller.ControllerService;
import com.springboot.lib.utils.NumberUtils;
import com.springboot.prj.route.RouteConstant;
import com.springboot.prj.service.user.UserDataService;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.List;

@RestController
public class UserController {
    private final ControllerService controllerService;
    private final UserDataService userDataService;

    public UserController(ControllerService controllerService, UserDataService userDataService) {
        this.controllerService = controllerService;
        this.userDataService = userDataService;
    }

    @GetMapping(RouteConstant.APP.USER.USERS)
    public ResponseEntity<?> getUsers(ServerRequest request){
        String pageIndexStr = request.param(RestConstant.PAGE.PAGE_INDEX).toString();
        String pageSizeStr = request.param(RestConstant.PAGE.PAGE_SIZE).toString();

        int pageIndex = NumberUtils.tryToGetInteger(pageIndexStr);
        int pageSize = NumberUtils.tryToGetInteger(pageSizeStr);

        PagingData pagingData = new PagingData();
        pagingData.setPageIndex(pageIndex);
        pagingData.setPageSize(pageSize);

        List<UserDTO> users = userDataService.getUsers(pagingData);

        return controllerService.success(users, pagingData);
    }

    @GetMapping(RouteConstant.APP.USER.USER)
    public ResponseEntity<?> getUser(ServerRequest request){
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        return controllerService.success(userDataService.getUser(userId));
    }

    @PostMapping(RouteConstant.APP.USER.CREATE)
    public ResponseEntity<?> create(ServerRequest request){
        UserRequest userRequest = HttpHelper.body(request, UserRequest.class);
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return controllerService.success(userDataService.create(userRequest));
    }

    @PostMapping(RouteConstant.APP.USER.UPDATE)
    public ResponseEntity<?> update(ServerRequest request){
        UserRequest userRequest = HttpHelper.body(request, UserRequest.class);
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return controllerService.success(userDataService.update(userRequest, userId));
    }

    @PostMapping(RouteConstant.APP.USER.STATUS_CHANGE)
    public ResponseEntity<?> delete(ServerRequest request){
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        return controllerService.success(userDataService.delete(userId));
    }
}
